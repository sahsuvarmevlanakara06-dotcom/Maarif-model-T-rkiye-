package com.example.data.curriculum

import com.example.data.model.QuestionFormat
import com.example.data.model.QuizAnswerState
import com.example.data.model.QuizQuestion
import com.example.data.model.SubjectDomain
import com.example.data.model.SubjectItem
import java.util.ArrayDeque
import java.util.Locale
import kotlin.random.Random

object QuestionGeneratorEngine {

    private const val MAX_RECENT_MEMORY = 150
    private val recentPromptMemory = ArrayDeque<String>(MAX_RECENT_MEMORY)
    private val memoryLock = Any()

    /**
     * Clears cross-quiz prompt deduplication history (useful for deterministic unit tests).
     */
    fun clearRecentHistory() {
        synchronized(memoryLock) {
            recentPromptMemory.clear()
        }
    }

    /**
     * Generates a diverse, contextually tailored, non-repeating list of questions based on:
     * - Subject & grade level (5-12)
     * - Specific topics chosen by the student
     * - Selected question formats (Mix & Match)
     *
     * Quantitative subjects (especially Math) dynamically synthesize endless randomized numerical
     * problems; verbal & conceptual subjects synthesize multi-angle topic-specific questions so
     * students never encounter repeated question text within or across consecutive quizzes.
     */
    fun generateQuiz(
        subject: SubjectItem,
        selectedTopics: List<String>,
        selectedFormats: Set<QuestionFormat>,
        questionCount: Int = 6,
        examModeLabel: String? = null
    ): List<QuizQuestion> {
        val rng = Random(System.nanoTime())
        val topics = if (selectedTopics.isEmpty()) subject.topics else selectedTopics
        val formats = if (selectedFormats.isEmpty()) {
            listOf(QuestionFormat.MULTIPLE_CHOICE)
        } else {
            selectedFormats.toList().shuffled(rng)
        }

        val allDomainFacts = SubjectKnowledgeBank.factsFor(subject.domain).shuffled(rng)
        val useQuantitative = MathProblemGenerator.canGenerateQuantitative(subject.domain)

        val result = mutableListOf<QuizQuestion>()
        val usedPromptsInQuiz = mutableSetOf<String>()
        val usedFactAngles = mutableSetOf<String>()
        val recentSnapshot = synchronized(memoryLock) { recentPromptMemory.toSet() }

        for (i in 0 until questionCount) {
            val topic = topics[i % topics.size]
            val format = formats[i % formats.size]

            // Math generates primarily procedural randomized problems (~85%+); other quantitative
            // domains (Physics, Chemistry, Mind Games, Economy) alternate dynamic calculations & concepts
            val preferQuantitative = when {
                subject.domain == SubjectDomain.MATH -> (i % 5 != 4) || allDomainFacts.isEmpty()
                useQuantitative -> (i % 2 == 0) || allDomainFacts.isEmpty()
                else -> false
            }

            val question = if (preferQuantitative) {
                generateUniqueQuantitativeQuestion(
                    subject = subject,
                    topic = topic,
                    format = format,
                    index = i,
                    examModeLabel = examModeLabel,
                    usedInQuiz = usedPromptsInQuiz,
                    recentHistory = recentSnapshot,
                    rng = rng
                )
            } else {
                generateUniqueVerbalQuestion(
                    subject = subject,
                    topic = topic,
                    allFacts = allDomainFacts,
                    format = format,
                    index = i,
                    examModeLabel = examModeLabel,
                    usedInQuiz = usedPromptsInQuiz,
                    usedFactAngles = usedFactAngles,
                    recentHistory = recentSnapshot,
                    rng = rng
                )
            }

            val sig = promptSignature(question)
            usedPromptsInQuiz.add(sig)
            rememberSignature(sig)
            result.add(question)
        }

        return result
    }

    /**
     * Generates a special LGS (Grade 8) or YKS (Grade 12) Çıkmış Sorular Modu exam.
     */
    fun generateExamModeQuiz(subject: SubjectItem, examModeLabel: String): List<QuizQuestion> {
        val mixedFormats = setOf(
            QuestionFormat.MULTIPLE_CHOICE,
            QuestionFormat.TRUE_FALSE,
            QuestionFormat.WORD_CHOICE,
            QuestionFormat.FILL_BLANK
        )
        return generateQuiz(
            subject = subject,
            selectedTopics = subject.topics,
            selectedFormats = mixedFormats,
            questionCount = 8,
            examModeLabel = examModeLabel
        )
    }

    private fun generateUniqueQuantitativeQuestion(
        subject: SubjectItem,
        topic: String,
        format: QuestionFormat,
        index: Int,
        examModeLabel: String?,
        usedInQuiz: Set<String>,
        recentHistory: Set<String>,
        rng: Random
    ): QuizQuestion {
        var bestCandidate: QuizQuestion? = null
        for (attempt in 0 until 14) {
            val candidate = MathProblemGenerator.generateQuestion(
                subject = subject,
                topic = topic,
                format = format,
                index = index + attempt * 19,
                rng = rng,
                examModeLabel = examModeLabel
            )
            val sig = promptSignature(candidate)
            if (sig !in usedInQuiz && sig !in recentHistory) {
                return candidate
            }
            if (sig !in usedInQuiz && bestCandidate == null) {
                bestCandidate = candidate
            }
        }
        return bestCandidate ?: MathProblemGenerator.generateQuestion(
            subject = subject,
            topic = topic,
            format = format,
            index = index + 999,
            rng = rng,
            examModeLabel = examModeLabel
        )
    }

    private fun generateUniqueVerbalQuestion(
        subject: SubjectItem,
        topic: String,
        allFacts: List<ConceptFact>,
        format: QuestionFormat,
        index: Int,
        examModeLabel: String?,
        usedInQuiz: Set<String>,
        usedFactAngles: MutableSet<String>,
        recentHistory: Set<String>,
        rng: Random
    ): QuizQuestion {
        val rankedFacts = rankFactsForTopic(topic, allFacts)
        val angles = listOf(0, 1, 2, 3).shuffled(rng)

        var fallbackNonDuplicateInQuiz: QuizQuestion? = null

        for (factIdx in rankedFacts.indices) {
            val fact = rankedFacts[(factIdx + index) % rankedFacts.size]
            for (angle in angles) {
                val pairKey = "${fact.term}_$angle"
                if (pairKey in usedFactAngles) continue

                val candidate = buildContextualVerbalQuestion(
                    subject = subject,
                    topic = topic,
                    fact = fact,
                    peerFacts = allFacts,
                    format = format,
                    angle = angle,
                    index = index,
                    examModeLabel = examModeLabel,
                    rng = rng
                )
                val sig = promptSignature(candidate)
                if (sig !in usedInQuiz && sig !in recentHistory) {
                    usedFactAngles.add(pairKey)
                    return candidate
                }
                if (sig !in usedInQuiz && fallbackNonDuplicateInQuiz == null) {
                    fallbackNonDuplicateInQuiz = candidate
                }
            }
        }

        if (fallbackNonDuplicateInQuiz != null) {
            return fallbackNonDuplicateInQuiz
        }

        // Guaranteed unique fallback incorporating topic & question ordinal context
        val fact = rankedFacts[index % rankedFacts.size]
        return buildContextualVerbalQuestion(
            subject = subject,
            topic = topic,
            fact = fact,
            peerFacts = allFacts,
            format = format,
            angle = (index % 4),
            index = index,
            examModeLabel = examModeLabel,
            rng = rng,
            forceContextQualifier = "${subject.grade}. Sınıf • $topic (Soru #${index + 1})"
        )
    }

    private fun rankFactsForTopic(topic: String, facts: List<ConceptFact>): List<ConceptFact> {
        val lowerTopic = normalizeTurkish(topic)
        val topicWords = lowerTopic.split(Regex("[\\s,;:()\\-/]+")).filter { it.length >= 3 }

        val scored = facts.map { fact ->
            val kw = normalizeTurkish(fact.keyword)
            val term = normalizeTurkish(fact.term)
            val def = normalizeTurkish(fact.definition)
            var score = 0
            if (lowerTopic.contains(kw) || kw.contains(lowerTopic)) score += 10
            if (lowerTopic.contains(term)) score += 8
            for (word in topicWords) {
                if (kw.contains(word)) score += 4
                if (term.contains(word)) score += 3
                if (def.contains(word)) score += 1
            }
            fact to score
        }
        return scored.sortedByDescending { it.second }.map { it.first }
    }

    private fun buildContextualVerbalQuestion(
        subject: SubjectItem,
        topic: String,
        fact: ConceptFact,
        peerFacts: List<ConceptFact>,
        format: QuestionFormat,
        angle: Int,
        index: Int,
        examModeLabel: String?,
        rng: Random,
        forceContextQualifier: String? = null
    ): QuizQuestion {
        val id = "q_${subject.id}_${index}_${rng.nextInt(100000)}"
        val prefix = buildString {
            if (examModeLabel != null) append("[$examModeLabel] ")
            if (forceContextQualifier != null) append("[$forceContextQualifier] ")
        }

        val peerTerms = peerFacts
            .asSequence()
            .filter { it.term != fact.term }
            .map { it.term }
            .distinct()
            .toList()
            .shuffled(rng)

        val peerFact = peerFacts
            .filter { it.term != fact.term }
            .randomOrNull(rng)

        return when (format) {
            QuestionFormat.MULTIPLE_CHOICE -> {
                when (angle % 3) {
                    // Angle 0: Direct bank question tailored with optional grade/topic frame
                    0 -> {
                        val options = (listOf(fact.mcqCorrect) + fact.mcqDistractors.take(3)).shuffled(rng)
                        QuizQuestion.MultipleChoice(
                            id = id,
                            topic = topic,
                            prompt = "$prefix${fact.mcqPrompt}",
                            options = options,
                            correctIndex = options.indexOf(fact.mcqCorrect),
                            explanation = "${fact.definition} ${fact.exampleUsage}"
                        )
                    }
                    // Angle 1: Definition -> Concept Term synthesis tailored to student's chosen topic & grade
                    1 -> {
                        val distractors = if (peerTerms.size >= 3) {
                            peerTerms.take(3)
                        } else {
                            (peerTerms + fact.mcqDistractors).filter { it != fact.term }.distinct().take(3)
                        }
                        val options = (listOf(fact.term) + distractors).shuffled(rng)
                        val leadIns = listOf(
                            "${subject.grade}. sınıf '$topic' konusunda; \"${fact.definition}\" şeklinde açıklanan kavram hangisidir?",
                            "$topic konusu kapsamında şu tanım hangi kavrama aittir?\n\"${fact.definition}\"",
                            "${subject.name} dersinde \"$topic\" başlığı altında geçen \"${fact.definition}\" açıklaması hangi kavramı ifade eder?"
                        )
                        QuizQuestion.MultipleChoice(
                            id = id,
                            topic = topic,
                            prompt = "$prefix${leadIns.random(rng)}",
                            options = options,
                            correctIndex = options.indexOf(fact.term),
                            explanation = "Doğru kavram: ${fact.term}. ${fact.exampleUsage}"
                        )
                    }
                    // Angle 2: Application / Example -> Concept synthesis
                    else -> {
                        val distractors = if (peerTerms.size >= 3) {
                            peerTerms.take(3)
                        } else {
                            (peerTerms + fact.mcqDistractors).filter { it != fact.term }.distinct().take(3)
                        }
                        val options = (listOf(fact.term) + distractors).shuffled(rng)
                        val leadIns = listOf(
                            "'$topic' ünitesinden verilen örneği inceleyiniz: \"${fact.exampleUsage}\" Bu örnek hangi kavramı doğrudan yansıtmaktadır?",
                            "${subject.grade}. sınıf ${subject.name} ($topic) bağlamında; \"${fact.exampleUsage}\" ifadesi hangi kavrama örnek oluşturur?"
                        )
                        QuizQuestion.MultipleChoice(
                            id = id,
                            topic = topic,
                            prompt = "$prefix${leadIns.random(rng)}",
                            options = options,
                            correctIndex = options.indexOf(fact.term),
                            explanation = "${fact.term}: ${fact.definition}"
                        )
                    }
                }
            }

            QuestionFormat.OPEN_ENDED -> {
                val prompts = listOf(
                    "$prefix${fact.openPrompt}",
                    "$prefix${subject.grade}. sınıf '$topic' konusu çerçevesinde '${fact.term}' kavramını tanımlayıp özgün bir örnekle açıklayınız.",
                    "$prefix${subject.name} ($topic) bağlamında '${fact.term}' ilkesinin neden önemli olduğunu kendi cümlelerinizle yorumlayınız."
                )
                QuizQuestion.OpenEnded(
                    id = id,
                    topic = topic,
                    prompt = prompts[angle % prompts.size],
                    hint = "Kavram: ${fact.term} — En az 10 karakterlik açıklayıcı bir yanıt yaz.",
                    sampleAnswer = "${fact.definition} ${fact.exampleUsage}",
                    explanation = "${fact.definition} ${fact.exampleUsage}"
                )
            }

            QuestionFormat.FILL_BLANK -> {
                val useReverseBlank = (angle % 2 == 1) && fact.term.length in 3..26 && !fact.term.contains("(")
                if (useReverseBlank) {
                    QuizQuestion.FillBlank(
                        id = id,
                        topic = topic,
                        sentenceBefore = "$prefix${subject.grade}. sınıf '$topic' konusunda \"${fact.definition}\" şeklinde açıklanan temel kavram:",
                        sentenceAfter = "olarak adlandırılır.",
                        correctAnswer = fact.term,
                        acceptedVariants = buildVariants(fact.term),
                        hint = "İpucu: '${fact.term.take(2)}...' (${fact.term.length} karakter)",
                        explanation = "Doğru kavram: ${fact.term}. ${fact.exampleUsage}"
                    )
                } else {
                    QuizQuestion.FillBlank(
                        id = id,
                        topic = topic,
                        sentenceBefore = "$prefix${fact.blankBefore}",
                        sentenceAfter = fact.blankAfter,
                        correctAnswer = fact.blankAnswer,
                        acceptedVariants = buildVariants(fact.blankAnswer),
                        hint = "İpucu: '${fact.blankAnswer.take(2)}...' (${fact.blankAnswer.length} harf)",
                        explanation = "Doğru kavram: ${fact.blankAnswer}. ${fact.definition}"
                    )
                }
            }

            QuestionFormat.TRUE_FALSE -> {
                when (angle % 3) {
                    0 -> QuizQuestion.TrueFalse(
                        id = id,
                        topic = topic,
                        statement = "$prefix${fact.tfStatement}",
                        isTrue = fact.tfIsTrue,
                        explanation = "${fact.definition} ${fact.exampleUsage}"
                    )
                    1 -> QuizQuestion.TrueFalse(
                        id = id,
                        topic = topic,
                        statement = "$prefix${subject.grade}. sınıf '$topic' konusu kapsamında '${fact.term}' kavramı şu şekilde tanımlanır: ${fact.definition}",
                        isTrue = true,
                        explanation = "Doğru! ${fact.term}: ${fact.definition}"
                    )
                    else -> {
                        if (peerFact != null) {
                            QuizQuestion.TrueFalse(
                                id = id,
                                topic = topic,
                                statement = "$prefix'$topic' ünitesindeki '${fact.term}' kavramı şu anlama gelir: ${peerFact.definition}",
                                isTrue = false,
                                explanation = "Yanlış! Verilen tanım '${peerFact.term}' kavramına aittir. '${fact.term}' ise: ${fact.definition}"
                            )
                        } else {
                            QuizQuestion.TrueFalse(
                                id = id,
                                topic = topic,
                                statement = "$prefix${fact.tfStatement}",
                                isTrue = fact.tfIsTrue,
                                explanation = "${fact.definition} ${fact.exampleUsage}"
                            )
                        }
                    }
                }
            }

            QuestionFormat.FLASHCARD -> {
                val subtitleVariants = listOf(
                    "${subject.grade}. Sınıf • ${subject.name} • $topic",
                    "$topic — Kavram Kartı",
                    "${subject.name} Temel Kavramı ($topic)"
                )
                QuizQuestion.Flashcard(
                    id = id,
                    topic = topic,
                    frontTerm = fact.term,
                    frontSubtitle = subtitleVariants[angle % subtitleVariants.size],
                    backDefinition = fact.definition,
                    backExample = fact.exampleUsage,
                    explanation = fact.definition
                )
            }

            QuestionFormat.WORD_CHOICE -> {
                val usePeerChoice = (angle % 2 == 1) && peerFact != null
                if (usePeerChoice && peerFact != null) {
                    val swap = rng.nextBoolean()
                    QuizQuestion.WordChoice(
                        id = id,
                        topic = topic,
                        sentenceBefore = "$prefix${subject.grade}. sınıf '$topic' dersinde \"${fact.definition}\" ifadesi",
                        optionA = if (swap) peerFact.term else fact.term,
                        optionB = if (swap) fact.term else peerFact.term,
                        correctOptionIndex = if (swap) 1 else 0,
                        sentenceAfter = "kavramını tanımlar.",
                        explanation = "Doğru kavram: '${fact.term}'. ${fact.exampleUsage}"
                    )
                } else {
                    val swap = rng.nextBoolean()
                    QuizQuestion.WordChoice(
                        id = id,
                        topic = topic,
                        sentenceBefore = "$prefix${fact.wordBefore}",
                        optionA = if (swap) fact.wordWrong else fact.wordCorrect,
                        optionB = if (swap) fact.wordCorrect else fact.wordWrong,
                        correctOptionIndex = if (swap) 1 else 0,
                        sentenceAfter = fact.wordAfter,
                        explanation = "Doğru kelime: '${fact.wordCorrect}'. ${fact.definition}"
                    )
                }
            }
        }
    }

    private fun rememberSignature(signature: String) {
        synchronized(memoryLock) {
            if (recentPromptMemory.size >= MAX_RECENT_MEMORY) {
                recentPromptMemory.removeFirst()
            }
            recentPromptMemory.addLast(signature)
        }
    }

    private fun buildVariants(answer: String): List<String> {
        val trLower = normalizeTurkish(answer)
        val ascii = trLower
            .replace('ç', 'c')
            .replace('ğ', 'g')
            .replace('ı', 'i')
            .replace('ö', 'o')
            .replace('ş', 's')
            .replace('ü', 'u')
            .replace("â", "a")
            .replace("î", "i")
        return listOf(trLower, ascii).distinct()
    }

    private fun normalizeTurkish(input: String): String {
        return input.trim()
            .replace('İ', 'i')
            .replace('I', 'ı')
            .lowercase(Locale("tr"))
            .replace(Regex("\\s+"), " ")
    }

    private fun promptSignature(q: QuizQuestion): String = when (q) {
        is QuizQuestion.MultipleChoice -> q.prompt
        is QuizQuestion.OpenEnded -> q.prompt
        is QuizQuestion.FillBlank -> "${q.sentenceBefore}_${q.correctAnswer}"
        is QuizQuestion.TrueFalse -> q.statement
        is QuizQuestion.Flashcard -> "${q.frontTerm}_${q.frontSubtitle}"
        is QuizQuestion.WordChoice -> "${q.sentenceBefore}_${q.optionA}_${q.optionB}"
    }

    /**
     * Real scoring engine checking every question type accurately:
     * - MCQ / True-False / Word-Choice: exact choice verification
     * - Fill-in-the-Blank: Turkish-normalized trimmed matching
     * - Open-Ended: meaningful text with at least 10 characters & 8 non-whitespace characters
     * - Flashcard: flipped (studied) state
     */
    fun evaluateAnswer(question: QuizQuestion, answer: QuizAnswerState?): Boolean {
        if (answer == null) return false
        return when (question) {
            is QuizQuestion.MultipleChoice -> answer.selectedOptionIndex == question.correctIndex
            is QuizQuestion.TrueFalse -> answer.trueFalseChoice == question.isTrue
            is QuizQuestion.WordChoice -> answer.selectedOptionIndex == question.correctOptionIndex
            is QuizQuestion.Flashcard -> answer.flashcardFlipped
            is QuizQuestion.OpenEnded -> {
                val cleaned = answer.textInput.trim()
                val nonSpaceLen = cleaned.replace(Regex("\\s+"), "").length
                cleaned.length >= 10 && nonSpaceLen >= 8
            }
            is QuizQuestion.FillBlank -> {
                val userNorm = normalizeTurkish(answer.textInput)
                if (userNorm.isEmpty()) return false
                val userAscii = userNorm
                    .replace('ç', 'c').replace('ğ', 'g').replace('ı', 'i')
                    .replace('ö', 'o').replace('ş', 's').replace('ü', 'u')
                val targets = (listOf(question.correctAnswer) + question.acceptedVariants).map { normalizeTurkish(it) }
                val asciiTargets = targets.map {
                    it.replace('ç', 'c').replace('ğ', 'g').replace('ı', 'i')
                        .replace('ö', 'o').replace('ş', 's').replace('ü', 'u')
                }
                userNorm in targets || userAscii in asciiTargets
            }
        }
    }
}
