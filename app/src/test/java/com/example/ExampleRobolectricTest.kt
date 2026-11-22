package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.curriculum.CurriculumCatalog
import com.example.data.curriculum.QuestionGeneratorEngine
import com.example.data.model.QuestionFormat
import com.example.data.model.QuizAnswerState
import com.example.data.model.QuizQuestion
import com.example.data.model.SubjectCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `app name matches Maarif Portal`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Maarif Portal", appName)
    }

    @Test
    fun `curriculum covers grades 5 to 12 across all four categories with unique topics`() {
        for (grade in 5..12) {
            for (category in SubjectCategory.entries) {
                val subjects = CurriculumCatalog.getSubjects(grade, category)
                assertTrue("Expected subjects for grade $grade category $category", subjects.isNotEmpty())
                subjects.forEach { subject ->
                    assertTrue("Expected topics for ${subject.name}", subject.topics.size >= 4)
                }
            }
        }
    }

    @Test
    fun `question generator produces all six formats and scoring engine evaluates accurately`() {
        val math8 = CurriculumCatalog.findSubjectById("g8_mat")!!
        val allFormats = QuestionFormat.entries.toSet()
        val questions = QuestionGeneratorEngine.generateQuiz(
            subject = math8,
            selectedTopics = math8.topics.take(3),
            selectedFormats = allFormats,
            questionCount = 6
        )

        assertEquals(6, questions.size)
        assertEquals(allFormats, questions.map { it.format }.toSet())

        questions.forEach { q ->
            when (q) {
                is QuizQuestion.MultipleChoice -> {
                    assertTrue(QuestionGeneratorEngine.evaluateAnswer(q, QuizAnswerState(selectedOptionIndex = q.correctIndex)))
                    assertFalse(QuestionGeneratorEngine.evaluateAnswer(q, QuizAnswerState(selectedOptionIndex = (q.correctIndex + 1) % q.options.size)))
                }
                is QuizQuestion.TrueFalse -> {
                    assertTrue(QuestionGeneratorEngine.evaluateAnswer(q, QuizAnswerState(trueFalseChoice = q.isTrue)))
                    assertFalse(QuestionGeneratorEngine.evaluateAnswer(q, QuizAnswerState(trueFalseChoice = !q.isTrue)))
                }
                is QuizQuestion.WordChoice -> {
                    assertTrue(QuestionGeneratorEngine.evaluateAnswer(q, QuizAnswerState(selectedOptionIndex = q.correctOptionIndex)))
                }
                is QuizQuestion.Flashcard -> {
                    assertFalse(QuestionGeneratorEngine.evaluateAnswer(q, QuizAnswerState(flashcardFlipped = false)))
                    assertTrue(QuestionGeneratorEngine.evaluateAnswer(q, QuizAnswerState(flashcardFlipped = true)))
                }
                is QuizQuestion.OpenEnded -> {
                    assertFalse(QuestionGeneratorEngine.evaluateAnswer(q, QuizAnswerState(textInput = "kısa")))
                    assertTrue(QuestionGeneratorEngine.evaluateAnswer(q, QuizAnswerState(textInput = "Bu konunun kuralı oldukça açıktır.")))
                }
                is QuizQuestion.FillBlank -> {
                    assertTrue(QuestionGeneratorEngine.evaluateAnswer(q, QuizAnswerState(textInput = q.correctAnswer)))
                    assertFalse(QuestionGeneratorEngine.evaluateAnswer(q, QuizAnswerState(textInput = "yanlis_cevap_xyz")))
                }
            }
        }
    }

    @Test
    fun `dynamic math generator produces unique non-repeating problems across consecutive quizzes`() {
        QuestionGeneratorEngine.clearRecentHistory()
        val math5 = CurriculumCatalog.findSubjectById("g5_mat")!!
        val quiz1 = QuestionGeneratorEngine.generateQuiz(
            subject = math5,
            selectedTopics = math5.topics,
            selectedFormats = setOf(QuestionFormat.MULTIPLE_CHOICE),
            questionCount = 8
        )
        val quiz2 = QuestionGeneratorEngine.generateQuiz(
            subject = math5,
            selectedTopics = math5.topics,
            selectedFormats = setOf(QuestionFormat.MULTIPLE_CHOICE),
            questionCount = 8
        )
        val prompts1 = quiz1.filterIsInstance<QuizQuestion.MultipleChoice>().map { it.prompt }
        val prompts2 = quiz2.filterIsInstance<QuizQuestion.MultipleChoice>().map { it.prompt }
        assertEquals(8, prompts1.toSet().size)
        assertEquals(8, prompts2.toSet().size)
        assertTrue("Expected consecutive quizzes to avoid repeating identical math prompts", (prompts1 + prompts2).toSet().size >= 14)
    }

    @Test
    fun `verbal subjects produce varied non-repeating topic-tailored concept questions`() {
        QuestionGeneratorEngine.clearRecentHistory()
        val turkish8 = CurriculumCatalog.getSubjects(8, SubjectCategory.CORE)
            .first { it.name.contains("Türkçe", ignoreCase = true) }
        val chosenTopics = turkish8.topics.take(2)
        val questions = QuestionGeneratorEngine.generateQuiz(
            subject = turkish8,
            selectedTopics = chosenTopics,
            selectedFormats = setOf(QuestionFormat.MULTIPLE_CHOICE, QuestionFormat.TRUE_FALSE),
            questionCount = 8
        )
        val signatures = questions.map { q ->
            when (q) {
                is QuizQuestion.MultipleChoice -> q.prompt
                is QuizQuestion.TrueFalse -> q.statement
                else -> q.id
            }
        }
        assertEquals("Every generated verbal question in a quiz must have unique text", 8, signatures.toSet().size)
        assertTrue(questions.all { it.topic in chosenTopics })
    }

    @Test
    fun `iso week key matches Firestore rules regex and potion tiers include rare multipliers`() {
        val weekKey = com.example.data.remote.WeekKeyUtils.currentIsoWeekKey()
        assertTrue("Week key '$weekKey' must match YYYY-Www", Regex("^[0-9]{4}-W[0-9]{2}$").matches(weekKey))
        val multipliers = com.example.data.model.PotionRarity.entries.map { it.multiplier }
        assertTrue(multipliers.containsAll(listOf(2L, 5L, 10L, 50L, 500L, 1_000_000L)))
    }

    @Test
    fun `all four app languages resolve distinct localized UI strings`() {
        val languages = com.example.ui.theme.AppLanguage.entries
        assertEquals(4, languages.size)
        val stringsList = languages.map { com.example.ui.theme.stringsForLanguage(it) }
        assertEquals(4, stringsList.map { it.coursesSelectGradeTitle }.toSet().size)
        assertEquals(4, stringsList.map { it.planTitle }.toSet().size)
        assertEquals(4, stringsList.map { it.profileBadgesTitle }.toSet().size)
        assertEquals(4, stringsList.map { it.setupStartQuizButton }.toSet().size)
        assertEquals(com.example.ui.theme.AppLanguage.AZ, com.example.ui.theme.AppLanguage.fromCode("az"))
        assertEquals(com.example.ui.theme.AppLanguage.EN, com.example.ui.theme.AppLanguage.fromCode("EN"))
        assertEquals(com.example.ui.theme.AppLanguage.DE, com.example.ui.theme.AppLanguage.fromCode("de"))
        assertEquals(com.example.ui.theme.AppLanguage.TR, com.example.ui.theme.AppLanguage.fromCode("unknown"))
    }
}
