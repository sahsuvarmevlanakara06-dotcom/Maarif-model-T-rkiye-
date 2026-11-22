package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Flip
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Rule
import androidx.compose.material.icons.rounded.ShortText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.QuestionFormat
import com.example.data.model.SubjectItem
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.LocalMaarifColors
import com.example.viewmodel.QuizSetupModalState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSetupModal(
    state: QuizSetupModalState,
    onDismiss: () -> Unit,
    onToggleTopic: (String) -> Unit,
    onSelectAllTopics: () -> Unit,
    onClearAllTopics: () -> Unit,
    onGoToStep: (Int) -> Unit,
    onToggleFormat: (QuestionFormat) -> Unit,
    onSelectQuestionCount: (Int) -> Unit,
    onStartQuiz: () -> Unit,
    onStartSpecialExamMode: (SubjectItem) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = LocalMaarifColors.current
    val strings = LocalAppStrings.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            // Modal Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.headerNavy)
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${state.subject.grade}${strings.leaderboardGradeSuffix} • ${strings.categoryShortName(state.subject.category)}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = state.subject.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("setup_modal_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = strings.actionClose,
                        tint = Color.White
                    )
                }
            }

            // 2-Step Progress Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.subtleSurface)
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StepBadge(
                    number = 1,
                    label = strings.setupStepTopics,
                    isActive = state.step == 1,
                    isCompleted = state.step > 1,
                    modifier = Modifier.weight(1f)
                )
                StepBadge(
                    number = 2,
                    label = strings.setupStepFormats,
                    isActive = state.step == 2,
                    isCompleted = false,
                    modifier = Modifier.weight(1f)
                )
            }

            AnimatedContent(
                targetState = state.step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "setup_modal_step"
            ) { step ->
                if (step == 1) {
                    StepOneTopicSelection(
                        state = state,
                        onToggleTopic = onToggleTopic,
                        onSelectAllTopics = onSelectAllTopics,
                        onClearAllTopics = onClearAllTopics,
                        onNext = { onGoToStep(2) },
                        onStartSpecialExamMode = { onStartSpecialExamMode(state.subject) }
                    )
                } else {
                    StepTwoFormatSelection(
                        state = state,
                        onToggleFormat = onToggleFormat,
                        onSelectQuestionCount = onSelectQuestionCount,
                        onBack = { onGoToStep(1) },
                        onStartQuiz = onStartQuiz
                    )
                }
            }
        }
    }
}

@Composable
private fun StepBadge(
    number: Int,
    label: String,
    isActive: Boolean,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = LocalMaarifColors.current
    val bgColor = when {
        isCompleted -> colors.emerald
        isActive -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            } else {
                Text(
                    text = "$number",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = if (isActive || isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
            )
        )
    }
}

@Composable
private fun StepOneTopicSelection(
    state: QuizSetupModalState,
    onToggleTopic: (String) -> Unit,
    onSelectAllTopics: () -> Unit,
    onClearAllTopics: () -> Unit,
    onNext: () -> Unit,
    onStartSpecialExamMode: () -> Unit
) {
    val colors = LocalMaarifColors.current
    val strings = LocalAppStrings.current
    val isLgsGrade = state.subject.grade == 8
    val isYksGrade = state.subject.grade == 12

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        // Golden Smart Button for Grade 8 (LGS) or Grade 12 (YKS)
        if (isLgsGrade || isYksGrade) {
            val modeTitle = if (isLgsGrade) {
                strings.setupLgsModeTitle
            } else {
                strings.setupYksModeTitle
            }
            val modeSubtitle = if (isLgsGrade) {
                strings.setupLgsModeSubtitle
            } else {
                strings.setupYksModeSubtitle
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFB45309), Color(0xFFF59E0B), Color(0xFFD97706))
                        )
                    )
                    .clickable { onStartSpecialExamMode() }
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .testTag("special_exam_mode_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = modeTitle,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = modeTitle,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                            Text(
                                text = modeSubtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.92f)
                                )
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Topic Selection Header & Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = strings.setupChooseTopicsTitle,
                style = MaterialTheme.typography.titleMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(
                    onClick = onSelectAllTopics,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("select_all_topics_button")
                ) {
                    Text(strings.setupSelectAll, style = MaterialTheme.typography.labelMedium)
                }
                if (state.selectedTopics.isNotEmpty()) {
                    TextButton(
                        onClick = onClearAllTopics,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(strings.setupClearAll, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 320.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.subject.topics, key = { it }) { topic ->
                val selected = topic in state.selectedTopics
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f) else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        width = if (selected) 1.5.dp else 1.dp,
                        color = if (selected) MaterialTheme.colorScheme.primary else colors.cardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onToggleTopic(topic) }
                        .testTag("topic_item_${topic.hashCode()}")
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                        Text(
                            text = topic,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(end = 30.dp)
                        )

                        // Top-right checkmark badge when selected!
                        if (selected) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Seçildi",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(22.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(20.dp)
                                    .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNext,
            enabled = state.selectedTopics.isNotEmpty(),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("setup_next_step_button")
        ) {
            Text(
                text = if (state.selectedTopics.isEmpty()) {
                    strings.setupSelectAtLeastOneTopic
                } else {
                    "${strings.setupNextTopicsPrefix}${state.selectedTopics.size}${strings.setupNextTopicsSuffix}"
                },
                style = MaterialTheme.typography.labelLarge
            )
            if (state.selectedTopics.isNotEmpty()) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun StepTwoFormatSelection(
    state: QuizSetupModalState,
    onToggleFormat: (QuestionFormat) -> Unit,
    onSelectQuestionCount: (Int) -> Unit,
    onBack: () -> Unit,
    onStartQuiz: () -> Unit
) {
    val colors = LocalMaarifColors.current
    val strings = LocalAppStrings.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Text(
            text = strings.setupChooseFormatsTitle,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = strings.setupChooseFormatsSubtitle,
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 310.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(QuestionFormat.entries, key = { it.name }) { format ->
                val selected = format in state.selectedFormats
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (selected) colors.emeraldBg.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        width = if (selected) 1.5.dp else 1.dp,
                        color = if (selected) colors.emerald else colors.cardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onToggleFormat(format) }
                        .testTag("format_item_${format.name}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selected) colors.emerald else colors.subtleSurface
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = formatIcon(format),
                                contentDescription = format.title,
                                tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.formatTitle(format),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = strings.formatSubtitle(format),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        if (selected) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = colors.emerald,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = colors.cardBorder)
        Spacer(modifier = Modifier.height(10.dp))

        // Question Count Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = strings.setupQuestionCountLabel,
                style = MaterialTheme.typography.titleSmall
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(6, 8, 10).forEach { count ->
                    FilterChip(
                        selected = state.questionCount == count,
                        onClick = { onSelectQuestionCount(count) },
                        label = { Text("$count ${strings.setupQuestionUnit}") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .height(52.dp)
                    .testTag("setup_back_step_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = strings.setupBackButton,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(strings.setupBackButton)
            }

            Button(
                onClick = onStartQuiz,
                enabled = state.selectedFormats.isNotEmpty(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("start_quiz_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = strings.setupStartQuizButton,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

private fun formatIcon(format: QuestionFormat): ImageVector = when (format) {
    QuestionFormat.MULTIPLE_CHOICE -> Icons.Rounded.Quiz
    QuestionFormat.OPEN_ENDED -> Icons.Rounded.EditNote
    QuestionFormat.FILL_BLANK -> Icons.Rounded.ShortText
    QuestionFormat.TRUE_FALSE -> Icons.Rounded.Rule
    QuestionFormat.FLASHCARD -> Icons.Rounded.Flip
    QuestionFormat.WORD_CHOICE -> Icons.Rounded.MenuBook
}
