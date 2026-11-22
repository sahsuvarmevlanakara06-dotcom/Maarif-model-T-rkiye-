package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class SubjectCategory(val displayName: String, val shortName: String) {
    CORE("Temel Dersler", "Temel"),
    ELECTIVE("Seçmeli & Alan Dersleri", "Seçmeli & Alan"),
    LANGUAGES("Yabancı & Türk Dilleri", "Diller"),
    RELIGION("Din & Ahlak", "Din & Ahlak")
}

enum class SubjectDomain {
    TURKISH,
    LITERATURE,
    MATH,
    SCIENCE,
    PHYSICS,
    CHEMISTRY,
    BIOLOGY,
    SOCIAL_STUDIES,
    HISTORY,
    REVOLUTION_HISTORY,
    GEOGRAPHY,
    PHILOSOPHY,
    PSYCHOLOGY,
    SOCIOLOGY,
    LOGIC,
    RELIGION_ETHICS,
    QURAN,
    PROPHET_LIFE,
    ISLAMIC_CULTURE,
    ENGLISH,
    GERMAN,
    ARABIC,
    TURKIC_LANGUAGES,
    OTTOMAN_TURKISH,
    COMPUTER_SCIENCE,
    TECH_DESIGN,
    ARTS_MUSIC,
    PHYSICAL_ED,
    MIND_GAMES,
    WRITING_DRAMA,
    MEDIA_LAW_DEMOCRACY,
    HEALTH_GUIDANCE,
    ASTRONOMY,
    ECONOMY_ENTREPRENEURSHIP,
    COMMUNICATION_DICTION,
    FOREIGN_LIT
}

data class SubjectItem(
    val id: String,
    val grade: Int,
    val name: String,
    val category: SubjectCategory,
    val domain: SubjectDomain,
    val accentHex: Long,
    val badgeTag: String? = null,
    val topics: List<String>
) {
    val accentColor: Color get() = Color(accentHex)
}

enum class QuestionFormat(
    val title: String,
    val subtitle: String,
    val badgeText: String
) {
    MULTIPLE_CHOICE(
        title = "Çoktan Seçmeli",
        subtitle = "4 şıklı yeni nesil MEB tarzı kazanım sorusu",
        badgeText = "Şıklı"
    ),
    OPEN_ENDED(
        title = "Açık Uçlu (Yazılı Tarzı)",
        subtitle = "Kendi cümlelerinle yazılı yanıt oluştur (en az 10 karakter)",
        badgeText = "Yazılı"
    ),
    FILL_BLANK(
        title = "Boşluk Doldurma",
        subtitle = "Cümle içindeki eksik kavrama uygun kelimeyi yaz",
        badgeText = "Kavram"
    ),
    TRUE_FALSE(
        title = "Doğru / Yanlış",
        subtitle = "Verilen bilimsel veya edebi ifadeyi değerlendir",
        badgeText = "D / Y"
    ),
    FLASHCARD(
        title = "Çevirmeli Bilgi Kartı",
        subtitle = "3D çevirme animasyonuyla kavramın açıklamasını öğren",
        badgeText = "3D Kart"
    ),
    WORD_CHOICE(
        title = "Doğru Kelimeyi Seç",
        subtitle = "Okuma modunda cümle içi iki alternatiften doğrusuna dokun",
        badgeText = "Okuma"
    )
}

sealed class QuizQuestion {
    abstract val id: String
    abstract val topic: String
    abstract val format: QuestionFormat
    abstract val points: Int
    abstract val explanation: String

    data class MultipleChoice(
        override val id: String,
        override val topic: String,
        val prompt: String,
        val options: List<String>,
        val correctIndex: Int,
        override val explanation: String,
        override val points: Int = 20
    ) : QuizQuestion() {
        override val format: QuestionFormat = QuestionFormat.MULTIPLE_CHOICE
    }

    data class OpenEnded(
        override val id: String,
        override val topic: String,
        val prompt: String,
        val hint: String,
        val sampleAnswer: String,
        override val explanation: String,
        override val points: Int = 25
    ) : QuizQuestion() {
        override val format: QuestionFormat = QuestionFormat.OPEN_ENDED
    }

    data class FillBlank(
        override val id: String,
        override val topic: String,
        val sentenceBefore: String,
        val sentenceAfter: String,
        val correctAnswer: String,
        val acceptedVariants: List<String> = emptyList(),
        val hint: String,
        override val explanation: String,
        override val points: Int = 20
    ) : QuizQuestion() {
        override val format: QuestionFormat = QuestionFormat.FILL_BLANK
    }

    data class TrueFalse(
        override val id: String,
        override val topic: String,
        val statement: String,
        val isTrue: Boolean,
        override val explanation: String,
        override val points: Int = 15
    ) : QuizQuestion() {
        override val format: QuestionFormat = QuestionFormat.TRUE_FALSE
    }

    data class Flashcard(
        override val id: String,
        override val topic: String,
        val frontTerm: String,
        val frontSubtitle: String,
        val backDefinition: String,
        val backExample: String,
        override val explanation: String,
        override val points: Int = 15
    ) : QuizQuestion() {
        override val format: QuestionFormat = QuestionFormat.FLASHCARD
    }

    data class WordChoice(
        override val id: String,
        override val topic: String,
        val sentenceBefore: String,
        val optionA: String,
        val optionB: String,
        val correctOptionIndex: Int, // 0 for A, 1 for B
        val sentenceAfter: String,
        override val explanation: String,
        override val points: Int = 20
    ) : QuizQuestion() {
        override val format: QuestionFormat = QuestionFormat.WORD_CHOICE
    }
}

data class QuizAnswerState(
    val selectedOptionIndex: Int? = null,
    val textInput: String = "",
    val trueFalseChoice: Boolean? = null,
    val flashcardFlipped: Boolean = false
)

enum class PotionRarity(
    val label: String,
    val multiplier: Long,
    val probabilityPercent: Double,
    val accentHex: Long
) {
    COMMON("Yaygın İksir", 2L, 50.0, 0xFF2563EB),
    RARE("Nadir İksir", 5L, 30.0, 0xFF059669),
    EPIC("Epik İksir", 10L, 12.0, 0xFF7C3AED),
    ULTRA_RARE("Çok Nadir İksir", 50L, 6.0, 0xFFD97706),
    MYTHIC("Mitik İksir", 500L, 1.5, 0xFFDB2777),
    LEGENDARY("EFSANEVİ İKSİR", 1_000_000L, 0.5, 0xFFC8102E)
}

data class PotionDrop(
    val rarity: PotionRarity,
    val durationMillis: Long = 3_600_000L,
    val replacedPrevious: Boolean = true
)

data class QuizSession(
    val subject: SubjectItem,
    val selectedTopics: List<String>,
    val selectedFormats: Set<QuestionFormat>,
    val examModeLabel: String? = null, // "LGS Çıkmış Sorular Modu" / "YKS Çıkmış Sorular Modu"
    val questions: List<QuizQuestion>,
    val answers: Map<String, QuizAnswerState> = emptyMap(),
    val isSubmitted: Boolean = false,
    val evaluationMap: Map<String, Boolean> = emptyMap()
)

data class QuizResultSummary(
    val subjectName: String,
    val grade: Int,
    val examModeLabel: String?,
    val correctCount: Int,
    val wrongCount: Int,
    val totalCount: Int,
    val basePoints: Long,
    val appliedMultiplier: Long,
    val totalEarnedPoints: Long,
    val newTotalPoints: Long,
    val streakCount: Int,
    val isPerfect: Boolean,
    val potionDrop: PotionDrop?
)

enum class GoalPeriod(val displayName: String, val badge: String) {
    DAILY("Günlük Hedef", "Günlük"),
    WEEKLY("Haftalık Hedef", "Haftalık")
}

data class RecommendedQuiz(
    val id: String,
    val subject: SubjectItem,
    val focusTopics: List<String>,
    val suggestedFormats: Set<QuestionFormat>,
    val reasonTag: String,
    val reasonDetail: String,
    val priorityAccentHex: Long,
    val questionCount: Int = 6
)

data class SubjectMasteryInfo(
    val subjectId: String,
    val subjectName: String,
    val grade: Int,
    val attempts: Int,
    val averageAccuracy: Int,
    val bestAccuracy: Int,
    val totalPointsEarned: Long
)

enum class BadgeIconType {
    FIRST_STEP,
    QUIZ_MASTER,
    PERFECT_SCORE,
    STREAK_FLAME,
    STAR_POINTS,
    POTION_BOLT,
    EXPLORER,
    EXAM_CHAMPION,
    PLANNER
}

data class AchievementBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconType: BadgeIconType,
    val accentHex: Long,
    val currentProgress: Int,
    val targetProgress: Int,
    val isUnlocked: Boolean
) {
    val progressFraction: Float
        get() = (currentProgress.toFloat() / targetProgress.coerceAtLeast(1)).coerceIn(0f, 1f)
}

data class StudentRankInfo(
    val title: String,
    val level: Int,
    val currentPoints: Long,
    val nextRankPoints: Long?,
    val progressFraction: Float
)
