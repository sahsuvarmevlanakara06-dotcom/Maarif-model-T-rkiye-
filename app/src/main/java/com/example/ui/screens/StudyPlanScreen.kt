package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.curriculum.CurriculumCatalog
import com.example.data.local.StudyGoalEntity
import com.example.data.model.GoalPeriod
import com.example.data.model.RecommendedQuiz
import com.example.data.model.SubjectItem
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.LocalMaarifColors
import com.example.viewmodel.GoalWithProgress

@Composable
fun StudyPlanScreen(
    selectedGrade: Int,
    goalsWithProgress: List<GoalWithProgress>,
    recommendations: List<RecommendedQuiz>,
    last7DaysCounts: List<Pair<String, Int>>,
    onAddGoal: (SubjectItem, List<String>, GoalPeriod, Int, Int) -> Unit,
    onAdjustGoalTarget: (StudyGoalEntity, Int) -> Unit,
    onToggleGoalActive: (StudyGoalEntity) -> Unit,
    onDeleteGoal: (Long) -> Unit,
    onLaunchRecommendation: (RecommendedQuiz) -> Unit,
    onPracticeGoal: (SubjectItem, Set<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalMaarifColors.current
    val strings = LocalAppStrings.current
    var showAddGoalSheet by remember { mutableStateOf(false) }

    val activeGoals = goalsWithProgress.filter { it.goal.isActive }
    val overallProgress = if (activeGoals.isEmpty()) {
        0f
    } else {
        activeGoals.map { it.progressFraction }.average().toFloat().coerceIn(0f, 1f)
    }
    val completedGoalsCount = activeGoals.count { it.isCompleted }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Visual Study Plan Hero Card (Ring + 7-Day Activity Bar Chart)
        item(key = "study_plan_hero") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                        )
                    )
                    .padding(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.planTitle,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (activeGoals.isEmpty()) {
                                    strings.planEmptySubtitle
                                } else {
                                    "$completedGoalsCount / ${activeGoals.size} ${strings.planActiveProgressSuffix}"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1))
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { showAddGoalSheet = true },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("add_study_goal_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(strings.planAddGoalButton, style = MaterialTheme.typography.labelLarge)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Custom Canvas Circular Goal Progress Ring
                        Box(
                            modifier = Modifier.size(86.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val stroke = 9.dp.toPx()
                                val inset = stroke / 2f
                                val arcSize = Size(size.width - stroke, size.height - stroke)
                                drawArc(
                                    color = Color.White.copy(alpha = 0.14f),
                                    startAngle = -90f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    topLeft = Offset(inset, inset),
                                    size = arcSize,
                                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                                )
                                drawArc(
                                    color = Color(0xFF10B981),
                                    startAngle = -90f,
                                    sweepAngle = 360f * overallProgress,
                                    useCenter = false,
                                    topLeft = Offset(inset, inset),
                                    size = arcSize,
                                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "%${(overallProgress * 100).toInt()}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = JetBrainsMonoFamily,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                                Text(
                                    text = strings.navPlan,
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                                )
                            }
                        }
                    }

                    // 7-Day Activity Mini Chart
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = strings.planLast7DaysTitle,
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                        )
                        val maxCount = (last7DaysCounts.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            last7DaysCounts.forEach { (dayLabel, count) ->
                                val barRatio = (count.toFloat() / maxCount).coerceIn(0.15f, 1f)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = if (count > 0) "$count" else "",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = JetBrainsMonoFamily,
                                            color = Color(0xFFFDE047)
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(18.dp)
                                            .height((34.dp * barRatio).coerceAtLeast(6.dp))
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (count > 0) colors.headerCrimsonBorder else Color.White.copy(alpha = 0.14f)
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = dayLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFCBD5E1))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Personalized Quiz Recommendations Section
        item(key = "recs_header") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = colors.goldDark,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = strings.planRecommendedTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        items(recommendations, key = { it.id }) { rec ->
            val accent = Color(rec.priorityAccentHex)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, colors.cardBorder),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recommendation_card_${rec.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(accent.copy(alpha = 0.13f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = rec.reasonTag,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = accent,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${rec.subject.grade}${strings.leaderboardGradeSuffix} • ${rec.subject.name}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = rec.reasonDetail,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${strings.planTopicsPrefix} ${rec.focusTopics.take(2).joinToString(", ")}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.primary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { onLaunchRecommendation(rec) },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                        modifier = Modifier.testTag("launch_rec_${rec.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = strings.planStartButton,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(strings.planStartButton, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // User's Custom Adaptable Study Goals Section
        item(key = "goals_header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Flag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${strings.planMyGoalsTitle} (${goalsWithProgress.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                OutlinedButton(
                    onClick = { showAddGoalSheet = true },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.planAddShort, style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        if (goalsWithProgress.isEmpty()) {
            item(key = "empty_goals") {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colors.subtleSurface,
                    border = BorderStroke(1.dp, colors.cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Flag,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(34.dp)
                        )
                        Text(
                            text = strings.planEmptyGoalsTitle,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = strings.planEmptyGoalsDesc,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        } else {
            items(goalsWithProgress, key = { it.goal.id }) { item ->
                StudyGoalCard(
                    item = item,
                    onAdjustTarget = { delta -> onAdjustGoalTarget(item.goal, delta) },
                    onToggleActive = { onToggleGoalActive(item.goal) },
                    onDelete = { onDeleteGoal(item.goal.id) },
                    onPractice = {
                        item.subject?.let { subj ->
                            onPracticeGoal(subj, item.goal.focusTopicsList().toSet())
                        }
                    }
                )
            }
        }
    }

    if (showAddGoalSheet) {
        AddStudyGoalSheet(
            initialGrade = selectedGrade,
            onDismiss = { showAddGoalSheet = false },
            onSave = { subject, topics, period, targetQuizzes, targetAccuracy ->
                onAddGoal(subject, topics, period, targetQuizzes, targetAccuracy)
                showAddGoalSheet = false
            }
        )
    }
}

@Composable
private fun StudyGoalCard(
    item: GoalWithProgress,
    onAdjustTarget: (Int) -> Unit,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit,
    onPractice: () -> Unit
) {
    val colors = LocalMaarifColors.current
    val strings = LocalAppStrings.current
    val goal = item.goal
    val periodLabel = if (goal.periodType == GoalPeriod.DAILY.name) strings.planDailyGoal else strings.planWeeklyGoal

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (item.isCompleted) 1.5.dp else 1.dp,
            color = if (item.isCompleted) colors.emerald else colors.cardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("study_goal_card_${goal.id}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (item.isCompleted) colors.emeraldBg else colors.subtleSurface)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (item.isCompleted) strings.planCompletedBadge else periodLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (item.isCompleted) colors.emerald else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Text(
                            text = "${strings.planTargetAccuracyPrefix} ${goal.targetAccuracy}% ${strings.planAccuracyWord}",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${goal.grade}${strings.leaderboardGradeSuffix} • ${goal.subjectName}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Switch(
                    checked = goal.isActive,
                    onCheckedChange = { onToggleActive() }
                )
            }

            // Focus Topics
            val topics = goal.focusTopicsList()
            if (topics.isNotEmpty()) {
                Text(
                    text = "${strings.planFocusTopicsPrefix} ${topics.joinToString(" • ")}",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Visual Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${item.completedCount} / ${goal.targetQuizzes} ${strings.planQuizzesCompletedSuffix}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = JetBrainsMonoFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "${(item.progressFraction * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = JetBrainsMonoFamily,
                            color = if (item.isCompleted) colors.emerald else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                LinearProgressIndicator(
                    progress = { item.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (item.isCompleted) colors.emerald else MaterialTheme.colorScheme.primary,
                    trackColor = colors.subtleSurface
                )
            }

            // Adaptable Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = strings.planTargetLabel,
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    IconButton(
                        onClick = { onAdjustTarget(-1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Rounded.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                    Text(
                        text = "${goal.targetQuizzes}",
                        style = MaterialTheme.typography.labelLarge.copy(fontFamily = JetBrainsMonoFamily)
                    )
                    IconButton(
                        onClick = { onAdjustTarget(1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Button(
                        onClick = onPractice,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(strings.planPracticeButton, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AddStudyGoalSheet(
    initialGrade: Int,
    onDismiss: () -> Unit,
    onSave: (SubjectItem, List<String>, GoalPeriod, Int, Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val strings = LocalAppStrings.current
    var chosenGrade by remember { mutableIntStateOf(initialGrade) }
    val subjectsInGrade = remember(chosenGrade) { CurriculumCatalog.getSubjects(chosenGrade) }
    var chosenSubject by remember(chosenGrade) { mutableStateOf(subjectsInGrade.first()) }
    var chosenTopics by remember(chosenSubject) { mutableStateOf(chosenSubject.topics.take(2).toSet()) }
    var chosenPeriod by remember { mutableStateOf(GoalPeriod.DAILY) }
    var targetQuizzes by remember { mutableIntStateOf(2) }
    var targetAccuracy by remember { mutableIntStateOf(80) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .heightIn(max = 600.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.planCreateGoalTitle,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = strings.actionClose)
                    }
                }
            }

            item {
                Text(strings.planStep1Grade, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(CurriculumCatalog.allGrades) { g ->
                        FilterChip(
                            selected = chosenGrade == g,
                            onClick = { chosenGrade = g },
                            label = { Text("$g${strings.leaderboardGradeSuffix}") }
                        )
                    }
                }
            }

            item {
                Text(strings.planStep2Subject, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(subjectsInGrade, key = { it.id }) { subject ->
                        FilterChip(
                            selected = chosenSubject.id == subject.id,
                            onClick = { chosenSubject = subject },
                            label = { Text(subject.name) }
                        )
                    }
                }
            }

            item {
                Text("${strings.planStep3Topics} (${chosenTopics.size})", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    chosenSubject.topics.forEach { topic ->
                        val selected = topic in chosenTopics
                        FilterChip(
                            selected = selected,
                            onClick = {
                                chosenTopics = if (selected) chosenTopics - topic else chosenTopics + topic
                            },
                            leadingIcon = if (selected) {
                                { Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            label = { Text(topic, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        )
                    }
                }
            }

            item {
                Text(strings.planStep4Frequency, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GoalPeriod.entries.forEach { period ->
                        FilterChip(
                            selected = chosenPeriod == period,
                            onClick = {
                                chosenPeriod = period
                                targetQuizzes = if (period == GoalPeriod.DAILY) 2 else 5
                            },
                            label = { Text(strings.goalPeriodName(period)) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (chosenPeriod == GoalPeriod.DAILY) strings.planDailyTargetLabel else strings.planWeeklyTargetLabel,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { targetQuizzes = (targetQuizzes - 1).coerceAtLeast(1) }) {
                            Icon(Icons.Rounded.Remove, contentDescription = null)
                        }
                        Text(
                            text = "$targetQuizzes ${strings.planQuizUnit}",
                            style = MaterialTheme.typography.titleSmall.copy(fontFamily = JetBrainsMonoFamily)
                        )
                        IconButton(onClick = { targetQuizzes = (targetQuizzes + 1).coerceAtMost(20) }) {
                            Icon(Icons.Rounded.Add, contentDescription = null)
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(strings.planTargetAccuracyLabel, style = MaterialTheme.typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(70, 80, 90, 100).forEach { acc ->
                            FilterChip(
                                selected = targetAccuracy == acc,
                                onClick = { targetAccuracy = acc },
                                label = { Text("$acc%") }
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        onSave(
                            chosenSubject,
                            chosenTopics.toList().ifEmpty { chosenSubject.topics.take(2) },
                            chosenPeriod,
                            targetQuizzes,
                            targetAccuracy
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(bottom = 8.dp)
                        .testTag("save_study_goal_button")
                ) {
                    Text(strings.planSaveGoalButton, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
