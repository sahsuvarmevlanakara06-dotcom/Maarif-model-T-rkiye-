package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.curriculum.CurriculumCatalog
import com.example.data.curriculum.QuestionGeneratorEngine
import com.example.data.local.PortalRepository
import com.example.data.local.QuizHistoryEntity
import com.example.data.local.StudyGoalEntity
import com.example.data.local.UserProgressData
import com.example.data.model.AchievementBadge
import com.example.data.model.GoalPeriod
import com.example.data.model.QuestionFormat
import com.example.data.model.QuizAnswerState
import com.example.data.model.QuizResultSummary
import com.example.data.model.QuizSession
import com.example.data.model.RecommendedQuiz
import com.example.data.model.StudentRankInfo
import com.example.data.model.SubjectCategory
import com.example.data.model.SubjectItem
import com.example.data.model.SubjectMasteryInfo
import com.example.data.remote.LeaderboardEntry
import com.example.data.remote.LeaderboardRepository
import com.example.data.remote.LeaderboardUiState
import com.example.data.remote.OperationType
import com.example.data.remote.RankedStudent
import com.example.data.remote.WeekKeyUtils
import com.example.data.remote.handleFirestoreError
import com.example.ui.theme.AppLanguage
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class PortalTab {
    COURSES,
    STUDY_PLAN,
    LEADERBOARD,
    PROFILE,
    SETTINGS
}

data class QuizSetupModalState(
    val subject: SubjectItem,
    val step: Int = 1,
    val selectedTopics: Set<String> = emptySet(),
    val selectedFormats: Set<QuestionFormat> = setOf(
        QuestionFormat.MULTIPLE_CHOICE,
        QuestionFormat.TRUE_FALSE,
        QuestionFormat.WORD_CHOICE
    ),
    val questionCount: Int = 6
)

data class GoalWithProgress(
    val goal: StudyGoalEntity,
    val completedCount: Int,
    val subject: SubjectItem?
) {
    val progressFraction: Float
        get() = (completedCount.toFloat() / goal.targetQuizzes.coerceAtLeast(1)).coerceIn(0f, 1f)
    val isCompleted: Boolean
        get() = completedCount >= goal.targetQuizzes
}

data class PortalUiState(
    val activeTab: PortalTab = PortalTab.COURSES,
    val selectedGrade: Int = 8,
    val selectedCategory: SubjectCategory = SubjectCategory.CORE,
    val subjectsForFilter: List<SubjectItem> = emptyList(),
    val progress: UserProgressData = UserProgressData(),
    val activeMultiplier: Long = 1L,
    val multiplierRemainingMillis: Long = 0L,
    val quizHistory: List<QuizHistoryEntity> = emptyList(),
    val goalsWithProgress: List<GoalWithProgress> = emptyList(),
    val recommendations: List<RecommendedQuiz> = emptyList(),
    val subjectMasteryMap: Map<String, SubjectMasteryInfo> = emptyMap(),
    val last7DaysQuizCounts: List<Pair<String, Int>> = emptyList(),
    val achievements: List<AchievementBadge> = emptyList(),
    val studentRank: StudentRankInfo = StudentRankInfo(
        title = "Başlangıç Kaşifi",
        level = 1,
        currentPoints = 0L,
        nextRankPoints = 250L,
        progressFraction = 0f
    ),
    val setupModal: QuizSetupModalState? = null,
    val activeQuiz: QuizSession? = null,
    val resultSummary: QuizResultSummary? = null,
    val showPotionSheet: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class PortalViewModel(
    private val repository: PortalRepository,
    private val leaderboardRepository: LeaderboardRepository,
    private val currentUserId: String,
    private val initialGoogleDisplayName: String? = null
) : ViewModel() {

    private val _activeTab = MutableStateFlow(PortalTab.COURSES)
    private val _selectedCategory = MutableStateFlow(SubjectCategory.CORE)
    private val _setupModal = MutableStateFlow<QuizSetupModalState?>(null)
    private val _activeQuiz = MutableStateFlow<QuizSession?>(null)
    private val _resultSummary = MutableStateFlow<QuizResultSummary?>(null)
    private val _showPotionSheet = MutableStateFlow(false)
    private val _nowMillis = MutableStateFlow(System.currentTimeMillis())
    private val _leaderboardRetryTick = MutableStateFlow(0)

    private val interactionState = combine(
        _activeTab,
        _selectedCategory,
        _setupModal,
        _activeQuiz,
        _resultSummary
    ) { tab, cat, modal, quiz, result ->
        InteractionSnapshot(tab, cat, modal, quiz, result)
    }

    private val dataState = combine(
        repository.userProgressFlow,
        repository.quizHistoryFlow,
        repository.studyGoalsFlow,
        _nowMillis,
        _showPotionSheet
    ) { progress, history, goals, now, showPotion ->
        DataSnapshot(progress, history, goals, now, showPotion)
    }

    val uiState: StateFlow<PortalUiState> = combine(interactionState, dataState) { ui, data ->
        val grade = data.progress.selectedGrade
        val subjects = CurriculumCatalog.getSubjects(grade, ui.selectedCategory)
        val remaining = (data.progress.multiplierExpiresAtMillis - data.nowMillis).coerceAtLeast(0L)
        val liveMultiplier = if (remaining > 0L && data.progress.activeMultiplier > 1L) {
            data.progress.activeMultiplier
        } else {
            1L
        }

        val goalsWithProgress = data.goals.map { goal ->
            GoalWithProgress(
                goal = goal,
                completedCount = repository.computeGoalProgressCount(goal, data.history),
                subject = CurriculumCatalog.findSubjectById(goal.subjectId)
            )
        }
        val masteryList = repository.computeSubjectMastery(data.history)
        val recommendations = repository.buildPersonalizedRecommendations(
            selectedGrade = grade,
            goals = data.goals,
            history = data.history
        )
        val achievements = repository.computeAchievements(
            progress = data.progress,
            history = data.history,
            goals = data.goals
        )
        val studentRank = repository.computeStudentRank(data.progress.totalPoints)

        PortalUiState(
            activeTab = ui.activeTab,
            selectedGrade = grade,
            selectedCategory = ui.selectedCategory,
            subjectsForFilter = subjects,
            progress = data.progress,
            activeMultiplier = liveMultiplier,
            multiplierRemainingMillis = remaining,
            quizHistory = data.history,
            goalsWithProgress = goalsWithProgress,
            recommendations = recommendations,
            subjectMasteryMap = masteryList.associateBy { it.subjectId },
            last7DaysQuizCounts = computeLast7DaysActivity(data.history, data.nowMillis),
            achievements = achievements,
            studentRank = studentRank,
            setupModal = ui.setupModal,
            activeQuiz = ui.activeQuiz,
            resultSummary = ui.resultSummary,
            showPotionSheet = data.showPotion
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = PortalUiState(
            subjectsForFilter = CurriculumCatalog.getSubjects(8, SubjectCategory.CORE)
        )
    )

    // Real-time Firestore Leaderboard Stream with Two-Tier CancellationException-safe Error Unwrapping
    val leaderboardState: StateFlow<LeaderboardUiState> = _leaderboardRetryTick
        .flatMapLatest {
            leaderboardRepository.observeLeaderboard()
                .map<List<LeaderboardEntry>, LeaderboardUiState> { entries ->
                    val weekKey = WeekKeyUtils.currentIsoWeekKey()

                    val globalRanked = entries
                        .sortedWith(
                            compareByDescending<LeaderboardEntry> { it.totalPoints }
                                .thenByDescending { it.streakCount }
                                .thenBy { it.displayName }
                        )
                        .mapIndexed { idx, entry ->
                            RankedStudent(
                                rank = idx + 1,
                                entry = entry,
                                isCurrentUser = entry.userId == currentUserId,
                                displayPoints = entry.totalPoints
                            )
                        }

                    val weeklyRanked = entries
                        .filter { it.weekKey == weekKey && it.weeklyPoints >= 0L }
                        .sortedWith(
                            compareByDescending<LeaderboardEntry> { it.weeklyPoints }
                                .thenByDescending { it.totalPoints }
                                .thenBy { it.displayName }
                        )
                        .mapIndexed { idx, entry ->
                            RankedStudent(
                                rank = idx + 1,
                                entry = entry,
                                isCurrentUser = entry.userId == currentUserId,
                                displayPoints = entry.weeklyPoints
                            )
                        }

                    LeaderboardUiState.Success(
                        globalRanking = globalRanked,
                        weeklyRanking = weeklyRanked,
                        currentWeekKey = weekKey,
                        myGlobalRank = globalRanked.firstOrNull { it.isCurrentUser }?.rank,
                        myWeeklyRank = weeklyRanked.firstOrNull { it.isCurrentUser }?.rank
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val rootError = firestoreError ?: error
                    handleFirestoreError(rootError, OperationType.LIST, "leaderboard")
                    Log.w(TAG, "Error observing real-time leaderboard", rootError)
                    emit(
                        LeaderboardUiState.Error(
                            rootError.localizedMessage ?: "Liderlik tablosu yüklenirken bir hata oluştu."
                        )
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = LeaderboardUiState.Loading
        )

    init {
        // Seed initial display name from Google profile if still default & publish initial leaderboard presence
        viewModelScope.launch {
            repository.seedDisplayNameIfDefault(initialGoogleDisplayName)
            syncCurrentProgressToLeaderboard()
        }

        // Real-time 1-second ticker for active multiplier potion countdown & automatic expiration
        viewModelScope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                _nowMillis.value = now
                val current = uiState.value.progress
                if (current.multiplierExpiresAtMillis > 0L && now >= current.multiplierExpiresAtMillis) {
                    repository.clearExpiredMultiplierIfNeeded(now)
                    syncCurrentProgressToLeaderboard()
                }
                delay(1000L)
            }
        }
    }

    fun selectTab(tab: PortalTab) {
        _activeTab.value = tab
    }

    fun selectGrade(grade: Int) {
        viewModelScope.launch {
            repository.setSelectedGrade(grade)
            syncCurrentProgressToLeaderboard()
        }
    }

    fun updateStudentProfile(name: String, avatarColorIndex: Int) {
        viewModelScope.launch {
            repository.updateStudentProfile(name, avatarColorIndex)
            syncCurrentProgressToLeaderboard()
        }
    }

    fun saveSettingsProfile(name: String, avatarColorIndex: Int, grade: Int) {
        viewModelScope.launch {
            repository.updateStudentProfile(name, avatarColorIndex)
            repository.setSelectedGrade(grade)
            syncCurrentProgressToLeaderboard()
        }
    }

    fun setAppLanguage(language: AppLanguage) {
        viewModelScope.launch {
            repository.setAppLanguage(language.code)
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDarkTheme(enabled)
        }
    }

    fun syncLeaderboardNow() {
        viewModelScope.launch {
            syncCurrentProgressToLeaderboard()
        }
    }

    fun retryLeaderboard() {
        _leaderboardRetryTick.update { it + 1 }
    }

    private suspend fun syncCurrentProgressToLeaderboard(overrideProgress: UserProgressData? = null) {
        val snapshot = overrideProgress ?: repository.getProgressSnapshot()
        val now = System.currentTimeMillis()
        val weekKey = WeekKeyUtils.currentIsoWeekKey(now)
        val liveWeeklyPoints = if (snapshot.weeklyPointsWeekKey == weekKey) snapshot.weeklyPoints else 0L
        val liveMultiplier = if (snapshot.multiplierExpiresAtMillis > now && snapshot.activeMultiplier > 1L) {
            snapshot.activeMultiplier
        } else {
            1L
        }
        leaderboardRepository.upsertCurrentUserLeaderboard(
            userId = currentUserId,
            displayName = snapshot.studentName,
            avatarColorIndex = snapshot.avatarColorIndex,
            grade = snapshot.selectedGrade,
            totalPoints = snapshot.totalPoints,
            weeklyPoints = liveWeeklyPoints,
            weekKey = weekKey,
            streakCount = snapshot.streakCount,
            perfectQuizCount = snapshot.perfectQuizCount,
            activeMultiplier = liveMultiplier
        )
    }

    fun selectCategory(category: SubjectCategory) {
        _selectedCategory.value = category
    }

    fun togglePotionSheet(visible: Boolean) {
        _showPotionSheet.value = visible
    }

    // --- 2-Step Quiz Setup Modal ---

    fun openQuizSetup(subject: SubjectItem, preselectedTopics: Set<String> = emptySet()) {
        _setupModal.value = QuizSetupModalState(
            subject = subject,
            step = 1,
            selectedTopics = preselectedTopics
        )
    }

    fun dismissQuizSetup() {
        _setupModal.value = null
    }

    fun toggleTopicSelection(topic: String) {
        _setupModal.update { current ->
            if (current == null) return@update null
            val next = if (topic in current.selectedTopics) {
                current.selectedTopics - topic
            } else {
                current.selectedTopics + topic
            }
            current.copy(selectedTopics = next)
        }
    }

    fun selectAllTopics() {
        _setupModal.update { current ->
            current?.copy(selectedTopics = current.subject.topics.toSet())
        }
    }

    fun clearAllTopics() {
        _setupModal.update { current ->
            current?.copy(selectedTopics = emptySet())
        }
    }

    fun goToSetupStep(step: Int) {
        _setupModal.update { current ->
            if (current == null) return@update null
            if (step == 2 && current.selectedTopics.isEmpty()) return@update current
            current.copy(step = step)
        }
    }

    fun toggleFormatSelection(format: QuestionFormat) {
        _setupModal.update { current ->
            if (current == null) return@update null
            val next = if (format in current.selectedFormats) {
                current.selectedFormats - format
            } else {
                current.selectedFormats + format
            }
            current.copy(selectedFormats = next)
        }
    }

    fun selectQuestionCount(count: Int) {
        _setupModal.update { current ->
            current?.copy(questionCount = count)
        }
    }

    fun startConfiguredQuiz() {
        val setup = _setupModal.value ?: return
        if (setup.selectedTopics.isEmpty() || setup.selectedFormats.isEmpty()) return

        val questions = QuestionGeneratorEngine.generateQuiz(
            subject = setup.subject,
            selectedTopics = setup.selectedTopics.toList(),
            selectedFormats = setup.selectedFormats,
            questionCount = setup.questionCount
        )
        _setupModal.value = null
        _resultSummary.value = null
        _activeQuiz.value = QuizSession(
            subject = setup.subject,
            selectedTopics = setup.selectedTopics.toList(),
            selectedFormats = setup.selectedFormats,
            questions = questions
        )
    }

    fun startSpecialExamMode(subject: SubjectItem) {
        val label = when (subject.grade) {
            8 -> "LGS Çıkmış Sorular Modu"
            12 -> "YKS Çıkmış Sorular Modu"
            else -> return
        }
        val questions = QuestionGeneratorEngine.generateExamModeQuiz(subject, label)
        _setupModal.value = null
        _resultSummary.value = null
        _activeQuiz.value = QuizSession(
            subject = subject,
            selectedTopics = subject.topics,
            selectedFormats = setOf(
                QuestionFormat.MULTIPLE_CHOICE,
                QuestionFormat.TRUE_FALSE,
                QuestionFormat.WORD_CHOICE,
                QuestionFormat.FILL_BLANK
            ),
            examModeLabel = label,
            questions = questions
        )
    }

    fun launchRecommendedQuiz(rec: RecommendedQuiz) {
        val questions = QuestionGeneratorEngine.generateQuiz(
            subject = rec.subject,
            selectedTopics = rec.focusTopics,
            selectedFormats = rec.suggestedFormats,
            questionCount = rec.questionCount
        )
        _resultSummary.value = null
        _activeQuiz.value = QuizSession(
            subject = rec.subject,
            selectedTopics = rec.focusTopics,
            selectedFormats = rec.suggestedFormats,
            questions = questions
        )
    }

    // --- Active Quiz Answer Updates ---

    fun updateMultipleChoiceAnswer(questionId: String, optionIndex: Int) {
        updateAnswer(questionId) { it.copy(selectedOptionIndex = optionIndex) }
    }

    fun updateTextInputAnswer(questionId: String, text: String) {
        updateAnswer(questionId) { it.copy(textInput = text) }
    }

    fun updateTrueFalseAnswer(questionId: String, choice: Boolean) {
        updateAnswer(questionId) { it.copy(trueFalseChoice = choice) }
    }

    fun flipFlashcard(questionId: String) {
        updateAnswer(questionId) { it.copy(flashcardFlipped = true) }
    }

    private fun updateAnswer(
        questionId: String,
        transform: (QuizAnswerState) -> QuizAnswerState
    ) {
        _activeQuiz.update { session ->
            if (session == null || session.isSubmitted) return@update session
            val current = session.answers[questionId] ?: QuizAnswerState()
            session.copy(answers = session.answers + (questionId to transform(current)))
        }
    }

    /**
     * Single-submission locked scoring trigger: immediately sets isSubmitted = true so repeated
     * clicks can never farm points, evaluates every question, persists rewards + Loot Drop potion,
     * and pushes updated score immediately to the live Global & Weekly Firestore leaderboard.
     */
    fun submitActiveQuiz() {
        val session = _activeQuiz.value ?: return
        if (session.isSubmitted) return // Hard anti-cheat single-click lock

        val evaluationMap = session.questions.associate { question ->
            question.id to QuestionGeneratorEngine.evaluateAnswer(question, session.answers[question.id])
        }

        val lockedSession = session.copy(
            isSubmitted = true,
            evaluationMap = evaluationMap
        )
        _activeQuiz.value = lockedSession

        viewModelScope.launch {
            val weekKey = WeekKeyUtils.currentIsoWeekKey()
            val (summary, updatedProgress) = repository.completeAndRecordQuiz(
                session = lockedSession,
                evaluationMap = evaluationMap,
                currentWeekKey = weekKey
            )
            _resultSummary.value = summary
            syncCurrentProgressToLeaderboard(updatedProgress)
        }
    }

    fun dismissResultAndReturnHome() {
        _resultSummary.value = null
        _activeQuiz.value = null
        _activeTab.value = PortalTab.COURSES
    }

    fun dismissResultToReviewAnswers() {
        _resultSummary.value = null
    }

    fun exitQuiz() {
        _resultSummary.value = null
        _activeQuiz.value = null
    }

    // --- Personalized Study Plan Actions ---

    fun addStudyGoal(
        subject: SubjectItem,
        focusTopics: List<String>,
        period: GoalPeriod,
        targetQuizzes: Int,
        targetAccuracy: Int
    ) {
        viewModelScope.launch {
            repository.saveStudyGoal(
                grade = subject.grade,
                subjectId = subject.id,
                subjectName = subject.name,
                focusTopics = focusTopics.ifEmpty { subject.topics.take(2) },
                period = period,
                targetQuizzes = targetQuizzes,
                targetAccuracy = targetAccuracy
            )
        }
    }

    fun adjustGoalTarget(goal: StudyGoalEntity, delta: Int) {
        val next = (goal.targetQuizzes + delta).coerceIn(1, 20)
        if (next == goal.targetQuizzes) return
        viewModelScope.launch {
            repository.updateStudyGoal(goal.copy(targetQuizzes = next))
        }
    }

    fun toggleGoalActive(goal: StudyGoalEntity) {
        viewModelScope.launch {
            repository.updateStudyGoal(goal.copy(isActive = !goal.isActive))
        }
    }

    fun deleteStudyGoal(goalId: Long) {
        viewModelScope.launch {
            repository.deleteStudyGoal(goalId)
        }
    }

    private fun computeLast7DaysActivity(
        history: List<QuizHistoryEntity>,
        nowMillis: Long
    ): List<Pair<String, Int>> {
        val dayLabels = listOf("Paz", "Pzt", "Sal", "Çar", "Per", "Cum", "Cmt")
        val cal = java.util.Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val days = mutableListOf<Pair<String, Int>>()
        for (offset in 6 downTo 0) {
            val dayStart = cal.timeInMillis - offset * 86_400_000L
            val dayEnd = dayStart + 86_400_000L
            val tmpCal = java.util.Calendar.getInstance().apply { timeInMillis = dayStart }
            val label = dayLabels[tmpCal.get(java.util.Calendar.DAY_OF_WEEK) - 1]
            val count = history.count { it.completedAtMillis in dayStart until dayEnd }
            days.add(label to count)
        }
        return days
    }

    private data class InteractionSnapshot(
        val activeTab: PortalTab,
        val selectedCategory: SubjectCategory,
        val setupModal: QuizSetupModalState?,
        val activeQuiz: QuizSession?,
        val resultSummary: QuizResultSummary?
    )

    private data class DataSnapshot(
        val progress: UserProgressData,
        val history: List<QuizHistoryEntity>,
        val goals: List<StudyGoalEntity>,
        val nowMillis: Long,
        val showPotion: Boolean
    )

    private companion object {
        const val TAG = "PortalVM"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
