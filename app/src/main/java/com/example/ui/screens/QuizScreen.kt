package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Flip
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.QuizAnswerState
import com.example.data.model.QuizQuestion
import com.example.data.model.QuizSession
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LocalMaarifColors
import com.example.ui.util.FormatUtils

@Composable
fun QuizScreen(
    session: QuizSession,
    activeMultiplier: Long,
    onSelectOption: (String, Int) -> Unit,
    onTextChange: (String, String) -> Unit,
    onTrueFalseSelect: (String, Boolean) -> Unit,
    onFlipFlashcard: (String) -> Unit,
    onSubmitQuiz: () -> Unit,
    onExitQuiz: () -> Unit
) {
    val colors = LocalMaarifColors.current
    var showExitConfirm by remember { mutableStateOf(false) }

    val handleBack = {
        if (session.isSubmitted) {
            onExitQuiz()
        } else {
            showExitConfirm = true
        }
    }

    BackHandler(onBack = handleBack)

    val answeredCount = session.questions.count { q ->
        val a = session.answers[q.id]
        when (q) {
            is QuizQuestion.MultipleChoice -> a?.selectedOptionIndex != null
            is QuizQuestion.TrueFalse -> a?.trueFalseChoice != null
            is QuizQuestion.WordChoice -> a?.selectedOptionIndex != null
            is QuizQuestion.Flashcard -> a?.flashcardFlipped == true
            is QuizQuestion.FillBlank -> !a?.textInput.isNullOrBlank()
            is QuizQuestion.OpenEnded -> (a?.textInput?.trim()?.length ?: 0) >= 10
        }
    }
    val progress = if (session.questions.isNotEmpty()) {
        answeredCount.toFloat() / session.questions.size
    } else 0f

    if (showExitConfirm) {
        AlertDialog(
            onDismissRequest = { showExitConfirm = false },
            title = { Text("Sınavdan Çıkmak İstiyor Musun?") },
            text = { Text("Henüz sınavı bitirmedin. Çıkarsan mevcut yanıtların kaydedilmeyecek.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitConfirm = false
                        onExitQuiz()
                    }
                ) {
                    Text("Çık", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirm = false }) {
                    Text("Devam Et")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.headerNavy)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = handleBack,
                        modifier = Modifier.testTag("quiz_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${session.subject.grade}. Sınıf • ${session.subject.name}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = session.examModeLabel ?: "$answeredCount / ${session.questions.size} etkinlik tamamlandı",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (session.examModeLabel != null) colors.goldColor else Color(0xFF94A3B8)
                            )
                        )
                    }
                    if (activeMultiplier > 1L) {
                        Box(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.potionViolet)
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Bolt,
                                    contentDescription = null,
                                    tint = Color(0xFFFDE047),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = FormatUtils.formatMultiplier(activeMultiplier),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = JetBrainsMonoFamily,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = colors.headerCrimsonBorder,
                    trackColor = Color(0xFF1E293B)
                )
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (!session.isSubmitted) {
                        Button(
                            onClick = onSubmitQuiz,
                            enabled = !session.isSubmitted,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("finish_quiz_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(id = R.string.btn_finish_quiz),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    } else {
                        Button(
                            onClick = onExitQuiz,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.headerNavy
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("quiz_locked_return_home_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Puanlar Kaydedildi • Ana Sayfaya Dön",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Rounded.Home,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            itemsIndexed(session.questions, key = { _, q -> q.id }) { idx, question ->
                val answer = session.answers[question.id] ?: QuizAnswerState()
                val evalResult = if (session.isSubmitted) session.evaluationMap[question.id] else null

                QuestionCardWrapper(
                    index = idx + 1,
                    total = session.questions.size,
                    question = question,
                    isSubmitted = session.isSubmitted,
                    isCorrect = evalResult
                ) {
                    when (question) {
                        is QuizQuestion.MultipleChoice -> MultipleChoiceContent(
                            question = question,
                            selectedIndex = answer.selectedOptionIndex,
                            isSubmitted = session.isSubmitted,
                            onSelect = { opt -> onSelectOption(question.id, opt) }
                        )

                        is QuizQuestion.OpenEnded -> OpenEndedContent(
                            question = question,
                            text = answer.textInput,
                            isSubmitted = session.isSubmitted,
                            onTextChange = { txt -> onTextChange(question.id, txt) }
                        )

                        is QuizQuestion.FillBlank -> FillBlankContent(
                            question = question,
                            text = answer.textInput,
                            isSubmitted = session.isSubmitted,
                            onTextChange = { txt -> onTextChange(question.id, txt) }
                        )

                        is QuizQuestion.TrueFalse -> TrueFalseContent(
                            question = question,
                            selectedChoice = answer.trueFalseChoice,
                            isSubmitted = session.isSubmitted,
                            onSelect = { choice -> onTrueFalseSelect(question.id, choice) }
                        )

                        is QuizQuestion.Flashcard -> Flashcard3DContent(
                            question = question,
                            isFlipped = answer.flashcardFlipped,
                            onFlip = { onFlipFlashcard(question.id) }
                        )

                        is QuizQuestion.WordChoice -> WordChoiceContent(
                            question = question,
                            selectedIndex = answer.selectedOptionIndex,
                            isSubmitted = session.isSubmitted,
                            onSelect = { opt -> onSelectOption(question.id, opt) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionCardWrapper(
    index: Int,
    total: Int,
    question: QuizQuestion,
    isSubmitted: Boolean,
    isCorrect: Boolean?,
    content: @Composable () -> Unit
) {
    val colors = LocalMaarifColors.current
    val borderColor = when {
        !isSubmitted -> colors.cardBorder
        isCorrect == true -> colors.emerald
        else -> MaterialTheme.colorScheme.error
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(if (isSubmitted) 1.5.dp else 1.dp, borderColor),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.headerNavy)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Soru $index/$total",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = JetBrainsMonoFamily,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.subtleSurface)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = question.format.badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                if (isSubmitted && isCorrect != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                            contentDescription = if (isCorrect) "Doğru" else "Yanlış",
                            tint = if (isCorrect) colors.emerald else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isCorrect) "+${question.points} Puan" else "0 Puan",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = JetBrainsMonoFamily,
                                color = if (isCorrect) colors.emerald else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                } else {
                    Text(
                        text = "+${question.points} P",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFamily,
                            color = colors.goldDark,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Text(
                text = question.topic,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            )

            content()

            if (isSubmitted) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCorrect == true) colors.emeraldBg.copy(alpha = 0.45f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isCorrect == true) "Doğru Yanıt!" else "Çözüm ve Açıklama",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect == true) colors.emerald else MaterialTheme.colorScheme.error
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = question.explanation,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MultipleChoiceContent(
    question: QuizQuestion.MultipleChoice,
    selectedIndex: Int?,
    isSubmitted: Boolean,
    onSelect: (Int) -> Unit
) {
    val colors = LocalMaarifColors.current
    val letters = listOf("A", "B", "C", "D")

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = question.prompt,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
        )
        question.options.forEachIndexed { optIdx, optionText ->
            val isSelected = selectedIndex == optIdx
            val isCorrectOption = optIdx == question.correctIndex
            val bgColor = when {
                isSubmitted && isCorrectOption -> colors.emeraldBg.copy(alpha = 0.65f)
                isSubmitted && isSelected && !isCorrectOption -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                else -> MaterialTheme.colorScheme.surface
            }
            val borderColor = when {
                isSubmitted && isCorrectOption -> colors.emerald
                isSubmitted && isSelected && !isCorrectOption -> MaterialTheme.colorScheme.error
                isSelected -> MaterialTheme.colorScheme.primary
                else -> colors.cardBorder
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = bgColor,
                border = BorderStroke(if (isSelected || (isSubmitted && isCorrectOption)) 1.5.dp else 1.dp, borderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(enabled = !isSubmitted) { onSelect(optIdx) }
                    .testTag("mcq_${question.id}_opt_$optIdx")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else colors.subtleSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letters.getOrElse(optIdx) { "${optIdx + 1}" },
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = optionText,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun OpenEndedContent(
    question: QuizQuestion.OpenEnded,
    text: String,
    isSubmitted: Boolean,
    onTextChange: (String) -> Unit
) {
    val colors = LocalMaarifColors.current
    val trimmedLen = text.trim().length
    val meetsMin = trimmedLen >= 10

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = question.prompt,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
        )
        Text(
            text = question.hint,
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            enabled = !isSubmitted,
            minLines = 3,
            maxLines = 5,
            placeholder = { Text("Kendi cümlelerinle cevabını buraya yaz (en az 10 karakter)...") },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("open_input_${question.id}")
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (meetsMin) "✓ Yeterli uzunlukta yanıt" else "Puan için en az 10 karakter yazmalısın",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (meetsMin) colors.emerald else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = "$trimmedLen / 10+",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = JetBrainsMonoFamily,
                    color = if (meetsMin) colors.emerald else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun FillBlankContent(
    question: QuizQuestion.FillBlank,
    text: String,
    isSubmitted: Boolean,
    onTextChange: (String) -> Unit
) {
    val colors = LocalMaarifColors.current

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.subtleSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "${question.sentenceBefore}  [ ______ ]  ${question.sentenceAfter}",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(14.dp)
            )
        }
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            enabled = !isSubmitted,
            singleLine = true,
            label = { Text("Eksik kavramı yaz") },
            supportingText = { Text(question.hint) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("blank_input_${question.id}")
        )
    }
}

@Composable
private fun TrueFalseContent(
    question: QuizQuestion.TrueFalse,
    selectedChoice: Boolean?,
    isSubmitted: Boolean,
    onSelect: (Boolean) -> Unit
) {
    val colors = LocalMaarifColors.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = question.statement,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(true to "Doğru", false to "Yanlış").forEach { (value, label) ->
                val isSelected = selectedChoice == value
                val accent = if (value) colors.emerald else MaterialTheme.colorScheme.error
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) accent.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) accent else colors.cardBorder
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(enabled = !isSubmitted) { onSelect(value) }
                        .testTag("tf_${question.id}_$value")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = if (value) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                            contentDescription = label,
                            tint = if (isSelected) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = if (isSelected) accent else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Flashcard3DContent(
    question: QuizQuestion.Flashcard,
    isFlipped: Boolean,
    onFlip: () -> Unit
) {
    val colors = LocalMaarifColors.current
    val density = LocalDensity.current.density
    var showingBack by remember(question.id) { mutableStateOf(isFlipped) }

    val rotation by animateFloatAsState(
        targetValue = if (showingBack) 180f else 0f,
        animationSpec = tween(durationMillis = 480, easing = FastOutSlowInEasing),
        label = "flashcard_3d_rotation"
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(178.dp)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    showingBack = !showingBack
                    onFlip()
                }
                .testTag("flashcard_${question.id}")
        ) {
            if (rotation <= 90f) {
                // Front Face
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0F172A), Color(0xFF1E3A8A))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Flip,
                                contentDescription = null,
                                tint = colors.goldColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ÇEVİRMELİ BİLGİ KARTI • ÖN YÜZ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = colors.goldColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Text(
                            text = question.frontTerm,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            textAlign = TextAlign.Center
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.14f))
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.TouchApp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Açıklamayı görmek için dokun",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
                            )
                        }
                    }
                }
            } else {
                // Back Face (un-mirrored with rotationY = 180f)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF065F46), Color(0xFF047857))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "KAVRAM AÇIKLAMASI • ARKA YÜZ",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFA7F3D0),
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = question.backDefinition,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = question.backExample,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFD1FAE5)
                            ),
                            maxLines = 2
                        )
                    }
                }
            }
        }

        Text(
            text = if (isFlipped) "✓ Kart çevrildi ve öğrenildi (+${question.points} puan hazır)" else "Puan almak için bilgi kartına dokunarak çevir.",
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isFlipped) colors.emerald else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WordChoiceContent(
    question: QuizQuestion.WordChoice,
    selectedIndex: Int?,
    isSubmitted: Boolean,
    onSelect: (Int) -> Unit
) {
    val colors = LocalMaarifColors.current

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Cümleyi doğru tamamlayan kelimeye dokun:",
            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = colors.subtleSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = question.sentenceBefore,
                    style = MaterialTheme.typography.bodyLarge
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(question.optionA, question.optionB).forEachIndexed { idx, word ->
                        val isSelected = selectedIndex == idx
                        val isRight = idx == question.correctOptionIndex
                        val pillBg = when {
                            isSubmitted && isRight -> colors.emerald
                            isSubmitted && isSelected && !isRight -> MaterialTheme.colorScheme.error
                            isSelected -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.surface
                        }
                        val textColor = if (isSelected || (isSubmitted && isRight)) {
                            Color.White
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = pillBg,
                            border = BorderStroke(
                                width = 1.5.dp,
                                color = if (isSelected || (isSubmitted && isRight)) pillBg else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(enabled = !isSubmitted) { onSelect(idx) }
                                .testTag("wordchoice_${question.id}_$idx")
                        ) {
                            Text(
                                text = word,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = textColor,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
                if (question.sentenceAfter.isNotBlank()) {
                    Text(
                        text = question.sentenceAfter,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
