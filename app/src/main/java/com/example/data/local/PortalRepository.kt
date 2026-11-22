package com.example.data.local

import com.example.data.curriculum.CurriculumCatalog
import com.example.data.model.AchievementBadge
import com.example.data.model.BadgeIconType
import com.example.data.model.GoalPeriod
import com.example.data.model.PotionDrop
import com.example.data.model.PotionRarity
import com.example.data.model.QuestionFormat
import com.example.data.model.QuizResultSummary
import com.example.data.model.QuizSession
import com.example.data.model.RecommendedQuiz
import com.example.data.model.StudentRankInfo
import com.example.data.model.SubjectMasteryInfo
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class PortalRepository(
    private val progressStore: UserProgressStore,
    private val quizHistoryDao: QuizHistoryDao,
    private val studyGoalDao: StudyGoalDao
) {
    val userProgressFlow: Flow<UserProgressData> = progressStore.progressFlow
    val quizHistoryFlow: Flow<List<QuizHistoryEntity>> = quizHistoryDao.observeAllHistory()
    val studyGoalsFlow: Flow<List<StudyGoalEntity>> = studyGoalDao.observeAllGoals()

    suspend fun setSelectedGrade(grade: Int) {
        progressStore.setSelectedGrade(grade)
    }

    suspend fun updateStudentProfile(name: String, avatarColorIndex: Int) {
        progressStore.updateStudentProfile(name, avatarColorIndex)
    }

    suspend fun seedDisplayNameIfDefault(googleDisplayName: String?) {
        progressStore.seedDisplayNameIfDefault(googleDisplayName)
    }

    suspend fun setAppLanguage(languageCode: String) {
        progressStore.setAppLanguage(languageCode)
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        progressStore.setDarkTheme(enabled)
    }

    suspend fun getProgressSnapshot(): UserProgressData = progressStore.getSnapshot()

    suspend fun clearExpiredMultiplierIfNeeded(nowMillis: Long) {
        progressStore.clearExpiredMultiplier(nowMillis)
    }

    suspend fun saveStudyGoal(
        grade: Int,
        subjectId: String,
        subjectName: String,
        focusTopics: List<String>,
        period: GoalPeriod,
        targetQuizzes: Int,
        targetAccuracy: Int
    ) {
        studyGoalDao.insertGoal(
            StudyGoalEntity(
                grade = grade,
                subjectId = subjectId,
                subjectName = subjectName,
                focusTopicsCsv = focusTopics.joinToString("||"),
                periodType = period.name,
                targetQuizzes = targetQuizzes.coerceIn(1, 20),
                targetAccuracy = targetAccuracy.coerceIn(50, 100),
                isActive = true
            )
        )
    }

    suspend fun updateStudyGoal(goal: StudyGoalEntity) {
        studyGoalDao.updateGoal(goal)
    }

    suspend fun deleteStudyGoal(id: Long) {
        studyGoalDao.deleteGoalById(id)
    }

    /**
     * Finalizes a submitted quiz: calculates base & multiplied points, rolls rare 1-hour
     * Loot Drop potion on 100% accuracy, updates persistent DataStore (including weeklyPoints),
     * and logs to Room history.
     */
    suspend fun completeAndRecordQuiz(
        session: QuizSession,
        evaluationMap: Map<String, Boolean>,
        currentWeekKey: String
    ): Pair<QuizResultSummary, UserProgressData> {
        val now = System.currentTimeMillis()
        val snapshot = progressStore.getSnapshot()

        val activeMult = if (snapshot.multiplierExpiresAtMillis > now && snapshot.activeMultiplier > 1L) {
            snapshot.activeMultiplier
        } else {
            1L
        }

        val totalCount = session.questions.size
        val correctCount = session.questions.count { evaluationMap[it.id] == true }
        val wrongCount = totalCount - correctCount
        val accuracyPercent = if (totalCount > 0) (correctCount * 100) / totalCount else 0
        val isPerfect = totalCount > 0 && correctCount == totalCount

        val basePoints = session.questions.sumOf { q ->
            if (evaluationMap[q.id] == true) q.points.toLong() else 0L
        }
        val totalEarnedPoints = safeMultiply(basePoints, activeMult)

        // Roll rare 1-hour Loot Drop Multiplier Potion when user scores 100%!
        val potionDrop: PotionDrop? = if (isPerfect) {
            val rarity = rollLootPotion()
            val replaces = rarity.multiplier >= activeMult || activeMult <= 1L
            PotionDrop(
                rarity = rarity,
                durationMillis = 3_600_000L,
                replacedPrevious = replaces
            )
        } else {
            null
        }

        val todayEpochDay = TimeUnit.MILLISECONDS.toDays(now)
        val updatedProgress = progressStore.recordQuizReward(
            earnedPoints = totalEarnedPoints,
            todayEpochDay = todayEpochDay,
            currentWeekKey = currentWeekKey,
            isPerfect = isPerfect,
            newPotionMultiplier = potionDrop?.rarity?.multiplier,
            newPotionExpiresAt = potionDrop?.let { now + it.durationMillis }
        )

        quizHistoryDao.insertHistory(
            QuizHistoryEntity(
                grade = session.subject.grade,
                subjectId = session.subject.id,
                subjectName = session.subject.name,
                topicsSummary = session.selectedTopics.take(3).joinToString(", "),
                examModeLabel = session.examModeLabel,
                correctCount = correctCount,
                totalCount = totalCount,
                accuracyPercent = accuracyPercent,
                basePoints = basePoints,
                multiplierApplied = activeMult,
                earnedPoints = totalEarnedPoints,
                isPerfect = isPerfect,
                potionWonMultiplier = potionDrop?.rarity?.multiplier ?: 0L,
                completedAtMillis = now
            )
        )

        val summary = QuizResultSummary(
            subjectName = session.subject.name,
            grade = session.subject.grade,
            examModeLabel = session.examModeLabel,
            correctCount = correctCount,
            wrongCount = wrongCount,
            totalCount = totalCount,
            basePoints = basePoints,
            appliedMultiplier = activeMult,
            totalEarnedPoints = totalEarnedPoints,
            newTotalPoints = updatedProgress.totalPoints,
            streakCount = updatedProgress.streakCount,
            isPerfect = isPerfect,
            potionDrop = potionDrop
        )
        return summary to updatedProgress
    }

    /**
     * Weighted Loot Drop roll:
     * 50.0% -> 2x (Yaygın)
     * 30.0% -> 5x (Nadir)
     * 12.0% -> 10x (Epik)
     * 6.0%  -> 50x (Çok Nadir)
     * 1.5%  -> 500x (Mitik)
     * 0.5%  -> 1.000.000x (Efsanevi!)
     */
    fun rollLootPotion(rng: Random = Random.Default): PotionRarity {
        val roll = rng.nextDouble(0.0, 100.0)
        return when {
            roll < 0.5 -> PotionRarity.LEGENDARY
            roll < 2.0 -> PotionRarity.MYTHIC
            roll < 8.0 -> PotionRarity.ULTRA_RARE
            roll < 20.0 -> PotionRarity.EPIC
            roll < 50.0 -> PotionRarity.RARE
            else -> PotionRarity.COMMON
        }
    }

    private fun safeMultiply(a: Long, b: Long): Long {
        if (a <= 0L || b <= 0L) return 0L
        return if (a > Long.MAX_VALUE / b) Long.MAX_VALUE else a * b
    }

    fun computeGoalProgressCount(goal: StudyGoalEntity, history: List<QuizHistoryEntity>): Int {
        val cutoffMillis = if (goal.periodType == GoalPeriod.DAILY.name) {
            startOfTodayMillis()
        } else {
            startOfLast7DaysMillis()
        }
        return history.count { entry ->
            entry.subjectId == goal.subjectId && entry.completedAtMillis >= cutoffMillis
        }
    }

    fun computeSubjectMastery(history: List<QuizHistoryEntity>): List<SubjectMasteryInfo> {
        return history.groupBy { it.subjectId }.map { (subjectId, entries) ->
            val first = entries.first()
            SubjectMasteryInfo(
                subjectId = subjectId,
                subjectName = first.subjectName,
                grade = first.grade,
                attempts = entries.size,
                averageAccuracy = entries.map { it.accuracyPercent }.average().toInt(),
                bestAccuracy = entries.maxOf { it.accuracyPercent },
                totalPointsEarned = entries.sumOf { it.earnedPoints }
            )
        }.sortedByDescending { it.attempts }
    }

    /**
     * Generates personalized, performance-aware quiz recommendations combining active Study Goals
     * and historical accuracy metrics from Room.
     */
    fun buildPersonalizedRecommendations(
        selectedGrade: Int,
        goals: List<StudyGoalEntity>,
        history: List<QuizHistoryEntity>
    ): List<RecommendedQuiz> {
        val recs = mutableListOf<RecommendedQuiz>()
        val usedSubjectIds = mutableSetOf<String>()
        val masteryBySubject = computeSubjectMastery(history).associateBy { it.subjectId }

        // 1. Prioritize active study goals that still have remaining daily/weekly quota
        goals.filter { it.isActive }.forEach { goal ->
            val subject = CurriculumCatalog.findSubjectById(goal.subjectId) ?: return@forEach
            val done = computeGoalProgressCount(goal, history)
            if (done < goal.targetQuizzes && usedSubjectIds.add(subject.id)) {
                val topics = goal.focusTopicsList().ifEmpty { subject.topics.take(3) }
                val periodLabel = if (goal.periodType == GoalPeriod.DAILY.name) "Günlük" else "Haftalık"
                recs.add(
                    RecommendedQuiz(
                        id = "rec_goal_${goal.id}",
                        subject = subject,
                        focusTopics = topics,
                        suggestedFormats = setOf(
                            QuestionFormat.MULTIPLE_CHOICE,
                            QuestionFormat.TRUE_FALSE,
                            QuestionFormat.FILL_BLANK,
                            QuestionFormat.WORD_CHOICE
                        ),
                        reasonTag = "Hedefinde ($done/${goal.targetQuizzes})",
                        reasonDetail = "$periodLabel çalışma planın için sıradaki odak sınavı.",
                        priorityAccentHex = 0xFFC8102E,
                        questionCount = 6
                    )
                )
            }
        }

        // 2. Weakest subjects from performance history (< 80% average accuracy)
        masteryBySubject.values
            .filter { it.averageAccuracy < 80 }
            .sortedBy { it.averageAccuracy }
            .forEach { mastery ->
                if (recs.size >= 5) return@forEach
                val subject = CurriculumCatalog.findSubjectById(mastery.subjectId) ?: return@forEach
                if (usedSubjectIds.add(subject.id)) {
                    recs.add(
                        RecommendedQuiz(
                            id = "rec_weak_${subject.id}",
                            subject = subject,
                            focusTopics = subject.topics.take(3),
                            suggestedFormats = setOf(
                                QuestionFormat.FLASHCARD,
                                QuestionFormat.MULTIPLE_CHOICE,
                                QuestionFormat.WORD_CHOICE
                            ),
                            reasonTag = "Gelişim Fırsatı • %${mastery.averageAccuracy}",
                            reasonDetail = "Önceki ortalaman %${mastery.averageAccuracy}. Bilgi kartları + test ile pekiştir!",
                            priorityAccentHex = 0xFFD97706,
                            questionCount = 6
                        )
                    )
                }
            }

        // 3. Untested core subjects in user's current selected grade
        val gradeSubjects = CurriculumCatalog.getSubjects(selectedGrade)
        gradeSubjects.forEach { subject ->
            if (recs.size >= 5) return@forEach
            if (subject.id !in masteryBySubject && usedSubjectIds.add(subject.id)) {
                recs.add(
                    RecommendedQuiz(
                        id = "rec_discover_${subject.id}",
                        subject = subject,
                        focusTopics = subject.topics.take(3),
                        suggestedFormats = setOf(
                            QuestionFormat.MULTIPLE_CHOICE,
                            QuestionFormat.TRUE_FALSE,
                            QuestionFormat.FLASHCARD
                        ),
                        reasonTag = if (selectedGrade == 8) "LGS Odak" else if (selectedGrade == 12) "YKS Odak" else "Yeni Keşif",
                        reasonDetail = "$selectedGrade. sınıf müfredatından henüz çözmediğin temel konular.",
                        priorityAccentHex = 0xFF2563EB,
                        questionCount = 6
                    )
                )
            }
        }

        // 4. Potion Hunt challenge on strong subjects
        if (recs.size < 4) {
            masteryBySubject.values
                .sortedByDescending { it.averageAccuracy }
                .forEach { mastery ->
                    if (recs.size >= 4) return@forEach
                    val subject = CurriculumCatalog.findSubjectById(mastery.subjectId) ?: return@forEach
                    if (usedSubjectIds.add(subject.id)) {
                        recs.add(
                            RecommendedQuiz(
                                id = "rec_potion_${subject.id}",
                                subject = subject,
                                focusTopics = subject.topics.shuffled().take(3),
                                suggestedFormats = setOf(
                                    QuestionFormat.MULTIPLE_CHOICE,
                                    QuestionFormat.TRUE_FALSE,
                                    QuestionFormat.WORD_CHOICE
                                ),
                                reasonTag = "İksir Avı • %100 Hedef",
                                reasonDetail = "Güçlü olduğun bu derste %100 yap, 1 saatlik Çarpan İksiri kazan!",
                                priorityAccentHex = 0xFF7C3AED,
                                questionCount = 6
                            )
                        )
                    }
                }
        }

        return recs
    }

    fun startOfTodayMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun startOfLast7DaysMillis(): Long = startOfTodayMillis() - TimeUnit.DAYS.toMillis(6)

    fun computeStudentRank(totalPoints: Long): StudentRankInfo {
        val tiers = listOf(
            Triple(1, "Başlangıç Kaşifi", 0L),
            Triple(2, "Bilgi Yolcusu", 250L),
            Triple(3, "Azimli Öğrenci", 1_000L),
            Triple(4, "Maarif Bilgini", 3_500L),
            Triple(5, "Üstün Başarı Yıldızı", 10_000L),
            Triple(6, "Efsanevi Akademisyen", 50_000L)
        )
        var currentIdx = 0
        for (i in tiers.indices) {
            if (totalPoints >= tiers[i].third) currentIdx = i
        }
        val current = tiers[currentIdx]
        val next = tiers.getOrNull(currentIdx + 1)
        val fraction = if (next == null) {
            1f
        } else {
            val span = (next.third - current.third).coerceAtLeast(1L)
            ((totalPoints - current.third).toFloat() / span.toFloat()).coerceIn(0f, 1f)
        }
        return StudentRankInfo(
            title = current.second,
            level = current.first,
            currentPoints = totalPoints,
            nextRankPoints = next?.third,
            progressFraction = fraction
        )
    }

    fun computeAchievements(
        progress: UserProgressData,
        history: List<QuizHistoryEntity>,
        goals: List<StudyGoalEntity>
    ): List<AchievementBadge> {
        val totalQuizzes = history.size
        val distinctSubjects = history.map { it.subjectId }.distinct().size
        val examModeCount = history.count { it.examModeLabel != null }
        val highAccuracyCount = history.count { it.accuracyPercent >= 80 }
        val pointsCapped = progress.totalPoints.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        val highestPotionCapped = progress.highestPotionWon.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()

        return listOf(
            AchievementBadge(
                id = "badge_first_quiz",
                title = "İlk Adım",
                description = "İlk interaktif sınavını başarıyla tamamla.",
                iconType = BadgeIconType.FIRST_STEP,
                accentHex = 0xFF2563EB,
                currentProgress = minOf(totalQuizzes, 1),
                targetProgress = 1,
                isUnlocked = totalQuizzes >= 1
            ),
            AchievementBadge(
                id = "badge_first_perfect",
                title = "Kusursuz Başarı",
                description = "Bir sınavda %100 doğru yanıt vererek İksir kazan.",
                iconType = BadgeIconType.PERFECT_SCORE,
                accentHex = 0xFFD97706,
                currentProgress = minOf(progress.perfectQuizCount, 1),
                targetProgress = 1,
                isUnlocked = progress.perfectQuizCount >= 1
            ),
            AchievementBadge(
                id = "badge_quiz_5",
                title = "Sınav Ustası",
                description = "Toplam 5 sınav tamamlayarak ritmini yakala.",
                iconType = BadgeIconType.QUIZ_MASTER,
                accentHex = 0xFF059669,
                currentProgress = minOf(totalQuizzes, 5),
                targetProgress = 5,
                isUnlocked = totalQuizzes >= 5
            ),
            AchievementBadge(
                id = "badge_high_acc_3",
                title = "Yüksek Hedef",
                description = "3 farklı sınavda %80 ve üzeri başarı yakala.",
                iconType = BadgeIconType.QUIZ_MASTER,
                accentHex = 0xFF0D9488,
                currentProgress = minOf(highAccuracyCount, 3),
                targetProgress = 3,
                isUnlocked = highAccuracyCount >= 3
            ),
            AchievementBadge(
                id = "badge_perfect_3",
                title = "Hatasız Seri",
                description = "Toplam 3 kez %100 kusursuz sınav tamamla.",
                iconType = BadgeIconType.PERFECT_SCORE,
                accentHex = 0xFFC8102E,
                currentProgress = minOf(progress.perfectQuizCount, 3),
                targetProgress = 3,
                isUnlocked = progress.perfectQuizCount >= 3
            ),
            AchievementBadge(
                id = "badge_streak_3",
                title = "Alevli Seri",
                description = "3 gün üst üste sınav çözerek serini koru.",
                iconType = BadgeIconType.STREAK_FLAME,
                accentHex = 0xFFF97316,
                currentProgress = minOf(progress.streakCount, 3),
                targetProgress = 3,
                isUnlocked = progress.streakCount >= 3
            ),
            AchievementBadge(
                id = "badge_streak_7",
                title = "Disiplin Şampiyonu",
                description = "7 günlük kesintisiz çalışma serisine ulaş.",
                iconType = BadgeIconType.STREAK_FLAME,
                accentHex = 0xFFEA580C,
                currentProgress = minOf(progress.streakCount, 7),
                targetProgress = 7,
                isUnlocked = progress.streakCount >= 7
            ),
            AchievementBadge(
                id = "badge_points_500",
                title = "Puan Avcısı",
                description = "Toplam 500 puan barajını aş.",
                iconType = BadgeIconType.STAR_POINTS,
                accentHex = 0xFFF59E0B,
                currentProgress = minOf(pointsCapped, 500),
                targetProgress = 500,
                isUnlocked = progress.totalPoints >= 500L
            ),
            AchievementBadge(
                id = "badge_points_5000",
                title = "Altın Koleksiyoncu",
                description = "İksirlerle birlikte 5.000 toplam puana ulaş.",
                iconType = BadgeIconType.STAR_POINTS,
                accentHex = 0xFFB45309,
                currentProgress = minOf(pointsCapped, 5000),
                targetProgress = 5000,
                isUnlocked = progress.totalPoints >= 5_000L
            ),
            AchievementBadge(
                id = "badge_potion_rare",
                title = "İksir Simyacısı",
                description = "En az 5x Nadir Çarpan İksiri kazan.",
                iconType = BadgeIconType.POTION_BOLT,
                accentHex = 0xFF7C3AED,
                currentProgress = minOf(highestPotionCapped, 5),
                targetProgress = 5,
                isUnlocked = progress.highestPotionWon >= 5L
            ),
            AchievementBadge(
                id = "badge_potion_ultra",
                title = "Efsanevi Şans",
                description = "50x veya üzeri Çok Nadir / Efsanevi iksir yakala.",
                iconType = BadgeIconType.POTION_BOLT,
                accentHex = 0xFFDB2777,
                currentProgress = minOf(highestPotionCapped, 50),
                targetProgress = 50,
                isUnlocked = progress.highestPotionWon >= 50L
            ),
            AchievementBadge(
                id = "badge_multi_subject",
                title = "Çok Yönlü Öğrenci",
                description = "En az 3 farklı dersten sınav tamamla.",
                iconType = BadgeIconType.EXPLORER,
                accentHex = 0xFF0284C7,
                currentProgress = minOf(distinctSubjects, 3),
                targetProgress = 3,
                isUnlocked = distinctSubjects >= 3
            ),
            AchievementBadge(
                id = "badge_exam_mode",
                title = "LGS / YKS Savaşçısı",
                description = "LGS veya YKS Çıkmış Sorular Modu sınavını bitir.",
                iconType = BadgeIconType.EXAM_CHAMPION,
                accentHex = 0xFF991B1B,
                currentProgress = minOf(examModeCount, 1),
                targetProgress = 1,
                isUnlocked = examModeCount >= 1
            ),
            AchievementBadge(
                id = "badge_study_planner",
                title = "Planlı Çalışan",
                description = "Kişisel Çalışma Planına en az 1 hedef ekle.",
                iconType = BadgeIconType.PLANNER,
                accentHex = 0xFF4F46E5,
                currentProgress = minOf(goals.size, 1),
                targetProgress = 1,
                isUnlocked = goals.isNotEmpty()
            )
        )
    }
}
