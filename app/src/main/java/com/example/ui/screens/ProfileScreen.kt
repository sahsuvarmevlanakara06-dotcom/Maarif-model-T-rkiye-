package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.QuizHistoryEntity
import com.example.data.local.UserProgressData
import com.example.data.model.AchievementBadge
import com.example.data.model.BadgeIconType
import com.example.data.model.StudentRankInfo
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.LocalMaarifColors
import com.example.ui.util.FormatUtils

private val AvatarPalette = listOf(
    Color(0xFFC8102E),
    Color(0xFF2563EB),
    Color(0xFF059669),
    Color(0xFF7C3AED),
    Color(0xFFD97706),
    Color(0xFF0D9488)
)

private enum class BadgeFilter(val label: String) {
    ALL("Tümü"),
    UNLOCKED("Kazanılanlar"),
    IN_PROGRESS("Devam Edenler")
}

@Composable
fun ProfileScreen(
    progress: UserProgressData,
    activeMultiplier: Long,
    multiplierRemainingMillis: Long,
    studentRank: StudentRankInfo,
    achievements: List<AchievementBadge>,
    history: List<QuizHistoryEntity>,
    onUpdateProfile: (String, Int) -> Unit,
    onOpenPotionSheet: () -> Unit,
    onNavigateToCourses: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalMaarifColors.current
    val strings = LocalAppStrings.current
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedBadgeFilter by remember { mutableStateOf(BadgeFilter.ALL) }
    var inspectedBadge by remember { mutableStateOf<AchievementBadge?>(null) }
    var showAllHistory by remember { mutableStateOf(false) }

    val unlockedCount = achievements.count { it.isUnlocked }
    val filteredBadges = remember(achievements, selectedBadgeFilter) {
        when (selectedBadgeFilter) {
            BadgeFilter.ALL -> achievements.sortedByDescending { it.isUnlocked }
            BadgeFilter.UNLOCKED -> achievements.filter { it.isUnlocked }
            BadgeFilter.IN_PROGRESS -> achievements.filter { !it.isUnlocked }
        }
    }
    val hasActivePotion = activeMultiplier > 1L && multiplierRemainingMillis > 0L

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Student Profile Hero Banner (Name, Grade, Rank & Level Progress)
        item(key = "profile_hero") {
            val avatarColor = AvatarPalette[progress.avatarColorIndex.coerceIn(0, AvatarPalette.lastIndex)]
            val initials = progress.studentName
                .split(" ")
                .filter { it.isNotBlank() }
                .take(2)
                .joinToString("") { it.first().uppercase() }
                .ifEmpty { "MÖ" }

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
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Student Avatar Monogram
                        Box(
                            modifier = Modifier
                                .size(62.dp)
                                .clip(CircleShape)
                                .background(avatarColor)
                                .border(2.5.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = progress.studentName,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(colors.headerCrimsonBorder)
                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${progress.selectedGrade}${strings.leaderboardGradeSuffix}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.WorkspacePremium,
                                    contentDescription = null,
                                    tint = colors.goldColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${strings.profileLevelPrefix} ${studentRank.level} • ${studentRank.title}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = colors.goldColor,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                                .testTag("edit_profile_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = strings.profileEditDialogTitle,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Rank Progress Bar
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (studentRank.nextRankPoints != null) {
                                    "${strings.profileNextRankPrefix} ${FormatUtils.formatPointsFull(studentRank.nextRankPoints)} ${strings.leaderboardPointsUnit}"
                                } else {
                                    strings.profileTopRankReached
                                },
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                            )
                            Text(
                                text = "${(studentRank.progressFraction * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = JetBrainsMonoFamily,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        LinearProgressIndicator(
                            progress = { studentRank.progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = colors.goldColor,
                            trackColor = Color.White.copy(alpha = 0.14f)
                        )
                    }
                }
            }
        }

        // 2. Core Live Gamification Stats: Total Points & Current Streak
        item(key = "profile_core_stats") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total Points Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, colors.cardBorder),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("profile_total_points_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(colors.goldBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = stringResource(id = R.string.points_cd),
                                tint = colors.goldDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            text = FormatUtils.formatPointsFull(progress.totalPoints),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = JetBrainsMonoFamily,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = strings.profileTotalPoints,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                // Current Streak Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, colors.cardBorder),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("profile_streak_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(colors.streakBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocalFireDepartment,
                                contentDescription = stringResource(id = R.string.streak_cd),
                                tint = colors.streakColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            text = "${progress.streakCount} ${strings.profileDaysWord}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = JetBrainsMonoFamily,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                        Text(
                            text = strings.profileCurrentStreak,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }

        // 3. Active Multiplier Potion Status Card (with Live Countdown!)
        item(key = "profile_potion_card") {
            val potionBrush = if (hasActivePotion) {
                if (activeMultiplier >= 500L) {
                    Brush.horizontalGradient(listOf(Color(0xFF7C3AED), Color(0xFFC8102E), Color(0xFFD97706)))
                } else {
                    Brush.horizontalGradient(listOf(Color(0xFF4C1D95), Color(0xFF7C3AED)))
                }
            } else {
                Brush.horizontalGradient(listOf(Color(0xFF1E293B), Color(0xFF334155)))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(potionBrush)
                    .clickable { onOpenPotionSheet() }
                    .padding(16.dp)
                    .testTag("profile_active_potion_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Bolt,
                                contentDescription = stringResource(id = R.string.potion_cd),
                                tint = if (hasActivePotion) Color(0xFFFDE047) else Color(0xFFCBD5E1),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (hasActivePotion) {
                                    "${strings.profileActivePotionPrefix} ${FormatUtils.formatMultiplier(activeMultiplier)}"
                                } else {
                                    strings.profileNoActivePotion
                                },
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                            Text(
                                text = if (hasActivePotion) {
                                    strings.profileActivePotionDesc
                                } else {
                                    strings.profileNoActivePotionDesc
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.28f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (hasActivePotion) {
                                FormatUtils.formatCountdown(multiplierRemainingMillis)
                            } else {
                                strings.profileOddsButton
                            },
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = JetBrainsMonoFamily,
                                color = if (hasActivePotion) Color(0xFFFDE047) else Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // 4. Earned Badges & Achievements Section
        item(key = "badges_header") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            imageVector = Icons.Rounded.MilitaryTech,
                            contentDescription = null,
                            tint = colors.goldDark,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = strings.profileBadgesTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.emeraldBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "$unlockedCount / ${achievements.size}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = JetBrainsMonoFamily,
                                color = colors.emerald,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BadgeFilter.entries.forEach { filter ->
                        val filterLabel = when (filter) {
                            BadgeFilter.ALL -> strings.profileFilterAll
                            BadgeFilter.UNLOCKED -> strings.profileFilterUnlocked
                            BadgeFilter.IN_PROGRESS -> strings.profileFilterInProgress
                        }
                        FilterChip(
                            selected = selectedBadgeFilter == filter,
                            onClick = { selectedBadgeFilter = filter },
                            label = { Text(filterLabel) },
                            modifier = Modifier.testTag("badge_filter_${filter.name}")
                        )
                    }
                }
            }
        }

        if (filteredBadges.isEmpty()) {
            item(key = "empty_badges_filter") {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colors.subtleSurface,
                    border = BorderStroke(1.dp, colors.cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(30.dp)
                        )
                        Text(
                            text = if (selectedBadgeFilter == BadgeFilter.UNLOCKED) {
                                strings.profileNoUnlockedBadges
                            } else {
                                strings.profileAllBadgesEarned
                            },
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = strings.profileBadgesHint,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        } else {
            val badgeRows = filteredBadges.chunked(2)
            items(badgeRows, key = { row -> row.joinToString("_") { it.id } }) { rowBadges ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowBadges.forEach { badge ->
                        AchievementBadgeCard(
                            badge = badge,
                            onClick = { inspectedBadge = badge },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowBadges.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // 5. Completed Quizzes History Section
        item(key = "profile_history_header") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(21.dp)
                    )
                    Text(
                        text = "${strings.profileHistoryTitle} (${history.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                if (history.size > 6) {
                    TextButton(onClick = { showAllHistory = !showAllHistory }) {
                        Text(if (showAllHistory) strings.profileSeeLess else strings.profileSeeAll)
                    }
                }
            }
        }

        if (history.isEmpty()) {
            item(key = "empty_profile_history") {
                Surface(
                    shape = RoundedCornerShape(18.dp),
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
                            imageVector = Icons.Rounded.Quiz,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(34.dp)
                        )
                        Text(
                            text = strings.profileEmptyHistoryTitle,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = strings.profileEmptyHistoryDesc,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = onNavigateToCourses,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("profile_start_first_quiz_button")
                        ) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.profileGoToCourses)
                        }
                    }
                }
            }
        } else {
            val visibleHistory = if (showAllHistory) history else history.take(6)
            items(visibleHistory, key = { "profile_hist_${it.id}" }) { entry ->
                CompletedQuizHistoryCard(entry = entry)
            }
        }
    }

    // Edit Student Name & Avatar Dialog
    if (showEditDialog) {
        EditStudentProfileDialog(
            currentName = progress.studentName,
            currentAvatarIndex = progress.avatarColorIndex,
            onDismiss = { showEditDialog = false },
            onSave = { name, colorIdx ->
                onUpdateProfile(name, colorIdx)
                showEditDialog = false
            }
        )
    }

    // Inspected Badge Detail Dialog
    inspectedBadge?.let { badge ->
        val accent = Color(badge.accentHex)
        AlertDialog(
            onDismissRequest = { inspectedBadge = null },
            icon = {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (badge.isUnlocked) accent.copy(alpha = 0.16f) else colors.subtleSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = badgeIcon(badge.iconType),
                        contentDescription = badge.title,
                        tint = if (badge.isUnlocked) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            title = {
                Text(
                    text = badge.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = badge.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (badge.isUnlocked) colors.emeraldBg else colors.subtleSurface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (badge.isUnlocked) strings.profileBadgeUnlocked else strings.profileBadgeProgress,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = if (badge.isUnlocked) colors.emerald else MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "${badge.currentProgress} / ${badge.targetProgress}",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = JetBrainsMonoFamily,
                                    color = if (badge.isUnlocked) colors.emerald else accent,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { inspectedBadge = null }) {
                    Text(strings.actionOk)
                }
            }
        )
    }
}

@Composable
private fun AchievementBadgeCard(
    badge: AchievementBadge,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalMaarifColors.current
    val strings = LocalAppStrings.current
    val accent = Color(badge.accentHex)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (badge.isUnlocked) MaterialTheme.colorScheme.surface else colors.subtleSurface.copy(alpha = 0.7f),
        border = BorderStroke(
            width = if (badge.isUnlocked) 1.5.dp else 1.dp,
            color = if (badge.isUnlocked) accent.copy(alpha = 0.65f) else colors.cardBorder
        ),
        shadowElevation = if (badge.isUnlocked) 2.dp else 0.dp,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("achievement_badge_${badge.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp)
                .alpha(if (badge.isUnlocked) 1f else 0.78f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (badge.isUnlocked) accent.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = badgeIcon(badge.iconType),
                        contentDescription = badge.title,
                        tint = if (badge.isUnlocked) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Icon(
                    imageVector = if (badge.isUnlocked) Icons.Rounded.CheckCircle else Icons.Rounded.Lock,
                    contentDescription = if (badge.isUnlocked) strings.profileUnlockedShort else strings.profileLockedShort,
                    tint = if (badge.isUnlocked) colors.emerald else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = badge.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                LinearProgressIndicator(
                    progress = { badge.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (badge.isUnlocked) colors.emerald else accent,
                    trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                Text(
                    text = if (badge.isUnlocked) strings.profileUnlockedShort else "${badge.currentProgress}/${badge.targetProgress}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFamily,
                        color = if (badge.isUnlocked) colors.emerald else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
private fun CompletedQuizHistoryCard(entry: QuizHistoryEntity) {
    val colors = LocalMaarifColors.current
    val strings = LocalAppStrings.current
    val accuracyColor = when {
        entry.accuracyPercent == 100 -> colors.emerald
        entry.accuracyPercent >= 70 -> colors.goldDark
        else -> MaterialTheme.colorScheme.error
    }
    val accuracyBg = when {
        entry.accuracyPercent == 100 -> colors.emeraldBg
        entry.accuracyPercent >= 70 -> colors.goldBg
        else -> MaterialTheme.colorScheme.errorContainer
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, colors.cardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("profile_history_item_${entry.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${entry.grade}${strings.leaderboardGradeSuffix} • ${entry.subjectName}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        entry.examModeLabel?.let { mode ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(colors.goldBg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (mode.contains("LGS")) "LGS" else "YKS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = colors.goldDark,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }
                        }
                    }
                    if (entry.topicsSummary.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = entry.topicsSummary,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accuracyBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "%${entry.accuracyPercent}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = JetBrainsMonoFamily,
                            color = accuracyColor,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${entry.correctCount}/${entry.totalCount} ${strings.profileCorrectWord}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFamily,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "• ${FormatUtils.formatShortDate(entry.completedAtMillis)}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AnimatedVisibility(visible = entry.potionWonMultiplier > 1L) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.potionVioletBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = null,
                                    tint = colors.potionViolet,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${FormatUtils.formatMultiplier(entry.potionWonMultiplier)} ${strings.profilePotionWord}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = JetBrainsMonoFamily,
                                        color = colors.potionViolet,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Text(
                        text = "+${FormatUtils.formatPointsCompact(entry.earnedPoints)} ${strings.leaderboardPointsUnit}",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = JetBrainsMonoFamily,
                            color = colors.goldDark,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun EditStudentProfileDialog(
    currentName: String,
    currentAvatarIndex: Int,
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit
) {
    val strings = LocalAppStrings.current
    var nameInput by remember { mutableStateOf(currentName) }
    var selectedColor by remember { mutableIntStateOf(currentAvatarIndex) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = strings.profileEditDialogTitle,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { if (it.length <= 30) nameInput = it },
                    singleLine = true,
                    label = { Text(strings.profileStudentNameLabel) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_profile_name_input")
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(strings.profileChooseAvatarColor, style = MaterialTheme.typography.labelLarge)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AvatarPalette.forEachIndexed { idx, color ->
                            val isSelected = idx == selectedColor
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColor = idx },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(nameInput, selectedColor) },
                modifier = Modifier.testTag("save_profile_button")
            ) {
                Text(strings.actionSave)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.actionCancel)
            }
        }
    )
}

private fun badgeIcon(type: BadgeIconType): ImageVector = when (type) {
    BadgeIconType.FIRST_STEP -> Icons.Rounded.Flag
    BadgeIconType.QUIZ_MASTER -> Icons.Rounded.School
    BadgeIconType.PERFECT_SCORE -> Icons.Rounded.EmojiEvents
    BadgeIconType.STREAK_FLAME -> Icons.Rounded.LocalFireDepartment
    BadgeIconType.STAR_POINTS -> Icons.Rounded.Star
    BadgeIconType.POTION_BOLT -> Icons.Rounded.Bolt
    BadgeIconType.EXPLORER -> Icons.Rounded.Explore
    BadgeIconType.EXAM_CHAMPION -> Icons.Rounded.WorkspacePremium
    BadgeIconType.PLANNER -> Icons.Rounded.CalendarMonth
}
