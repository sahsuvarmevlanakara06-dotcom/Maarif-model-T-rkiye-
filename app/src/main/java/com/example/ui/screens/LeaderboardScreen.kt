package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.LeaderboardPeriod
import com.example.data.remote.LeaderboardUiState
import com.example.data.remote.RankedStudent
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.LocalMaarifColors
import com.example.ui.util.FormatUtils

private val LeaderboardAvatarColors = listOf(
    Color(0xFFC8102E),
    Color(0xFF2563EB),
    Color(0xFF059669),
    Color(0xFF7C3AED),
    Color(0xFFD97706),
    Color(0xFF0891B2)
)

@Composable
fun LeaderboardScreen(
    state: LeaderboardUiState,
    onSyncNow: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val colors = LocalMaarifColors.current
    var selectedPeriod by remember { mutableStateOf(LeaderboardPeriod.GLOBAL) }

    val pulseTransition = rememberInfiniteTransition(label = "live_dot")
    val liveAlpha by pulseTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "live_alpha"
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 760.dp)
                .testTag("leaderboard_screen"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Banner
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = colors.headerNavy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(colors.headerNavy, Color(0xFF1E293B))
                                )
                            )
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(colors.goldColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.EmojiEvents,
                                        contentDescription = null,
                                        tint = colors.goldColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = strings.leaderboardTitle,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )
                                    if (state is LeaderboardUiState.Success) {
                                        Text(
                                            text = state.currentWeekKey,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = JetBrainsMonoFamily,
                                                color = Color(0xFF94A3B8)
                                            )
                                        )
                                    }
                                }
                            }

                            // Live indicator pill
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(colors.emerald.copy(alpha = 0.18f))
                                    .border(1.dp, colors.emerald.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .graphicsLayer { alpha = liveAlpha }
                                        .clip(CircleShape)
                                        .background(colors.emerald)
                                )
                                Text(
                                    text = strings.leaderboardLiveBadge,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = colors.emerald,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                            }
                        }

                        Text(
                            text = strings.leaderboardSubtitle,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1))
                        )

                        // Global vs Weekly Segmented Switcher
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PeriodToggleChip(
                                selected = selectedPeriod == LeaderboardPeriod.GLOBAL,
                                label = strings.leaderboardGlobalTab,
                                icon = Icons.Rounded.Public,
                                onClick = { selectedPeriod = LeaderboardPeriod.GLOBAL },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("leaderboard_tab_global")
                            )
                            PeriodToggleChip(
                                selected = selectedPeriod == LeaderboardPeriod.WEEKLY,
                                label = strings.leaderboardWeeklyTab,
                                icon = Icons.Rounded.CalendarMonth,
                                onClick = { selectedPeriod = LeaderboardPeriod.WEEKLY },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("leaderboard_tab_weekly")
                            )
                        }
                    }
                }
            }

            when (state) {
                is LeaderboardUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is LeaderboardUiState.Error -> {
                    item {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = state.message,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    ),
                                    textAlign = TextAlign.Center
                                )
                                OutlinedButton(onClick = onRetry) {
                                    Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(strings.leaderboardRetry)
                                }
                            }
                        }
                    }
                }

                is LeaderboardUiState.Success -> {
                    val activeList = if (selectedPeriod == LeaderboardPeriod.GLOBAL) {
                        state.globalRanking
                    } else {
                        state.weeklyRanking
                    }
                    val myRank = if (selectedPeriod == LeaderboardPeriod.GLOBAL) {
                        state.myGlobalRank
                    } else {
                        state.myWeeklyRank
                    }
                    val myEntry = activeList.firstOrNull { it.isCurrentUser }

                    // Current User Rank Banner
                    if (myEntry != null && myRank != null) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "#$myRank",
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontFamily = JetBrainsMonoFamily,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = strings.leaderboardYourRank,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                            Text(
                                                text = myEntry.entry.displayName,
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${FormatUtils.formatPointsCompact(myEntry.displayPoints)} ${strings.leaderboardPointsUnit}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = JetBrainsMonoFamily,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )
                                }
                            }
                        }
                    }

                    if (activeList.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, colors.cardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.WorkspacePremium,
                                        contentDescription = null,
                                        tint = colors.goldColor,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        text = strings.leaderboardEmptyTitle,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = strings.leaderboardEmptyDesc,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Button(
                                        onClick = onSyncNow,
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.testTag("leaderboard_sync_button")
                                    ) {
                                        Icon(Icons.Rounded.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(strings.leaderboardSyncNow)
                                    }
                                }
                            }
                        }
                    } else {
                        // Top 3 Podium Showcase
                        if (activeList.size >= 2) {
                            item {
                                TopPodiumRow(
                                    topThree = activeList.take(3),
                                    pointsUnit = strings.leaderboardPointsUnit
                                )
                            }
                        }

                        // Full Ranked List
                        items(activeList, key = { "${selectedPeriod.name}_${it.entry.userId}_${it.rank}" }) { ranked ->
                            LeaderboardStudentRow(
                                student = ranked,
                                gradeSuffix = strings.leaderboardGradeSuffix,
                                youBadge = strings.leaderboardYouBadge,
                                pointsUnit = strings.leaderboardPointsUnit
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PeriodToggleChip(
    selected: Boolean,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(11.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) Color(0xFF0F172A) else Color(0xFFCBD5E1),
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                color = if (selected) Color(0xFF0F172A) else Color(0xFFCBD5E1),
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TopPodiumRow(
    topThree: List<RankedStudent>,
    pointsUnit: String
) {
    val colors = LocalMaarifColors.current
    val ordered = when (topThree.size) {
        2 -> listOf(topThree[1], topThree[0])
        else -> listOf(topThree[1], topThree[0], topThree[2])
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, colors.cardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            ordered.forEach { student ->
                val isFirst = student.rank == 1
                val medalColor = when (student.rank) {
                    1 -> Color(0xFFF59E0B)
                    2 -> Color(0xFF94A3B8)
                    else -> Color(0xFFB45309)
                }
                val avatarColor = LeaderboardAvatarColors[
                    student.entry.avatarColorIndex.toInt().coerceIn(0, LeaderboardAvatarColors.lastIndex)
                ]
                val initials = student.entry.displayName
                    .split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .joinToString("") { it.first().uppercase() }
                    .ifBlank { "M" }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isFirst) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = null,
                            tint = medalColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(if (isFirst) 58.dp else 46.dp)
                            .clip(CircleShape)
                            .background(avatarColor)
                            .border(2.5.dp, medalColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            style = (if (isFirst) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall).copy(
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = student.entry.displayName,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${FormatUtils.formatPointsCompact(student.displayPoints)} $pointsUnit",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFamily,
                            color = medalColor,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isFirst) 42.dp else if (student.rank == 2) 30.dp else 24.dp)
                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                            .background(medalColor.copy(alpha = 0.2f))
                            .border(1.dp, medalColor.copy(alpha = 0.55f), RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${student.rank}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = JetBrainsMonoFamily,
                                color = medalColor,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardStudentRow(
    student: RankedStudent,
    gradeSuffix: String,
    youBadge: String,
    pointsUnit: String
) {
    val colors = LocalMaarifColors.current
    val avatarColor = LeaderboardAvatarColors[
        student.entry.avatarColorIndex.toInt().coerceIn(0, LeaderboardAvatarColors.lastIndex)
    ]
    val initials = student.entry.displayName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "M" }

    val rankColor = when (student.rank) {
        1 -> Color(0xFFF59E0B)
        2 -> Color(0xFF64748B)
        3 -> Color(0xFFB45309)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (student.isCurrentUser) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            width = if (student.isCurrentUser) 1.5.dp else 1.dp,
            color = if (student.isCurrentUser) MaterialTheme.colorScheme.primary else colors.cardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("leaderboard_row_${student.rank}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Rank Number
            Box(
                modifier = Modifier.width(30.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${student.rank}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = JetBrainsMonoFamily,
                        color = rankColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }

            // Avatar
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Name & Badges
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = student.entry.displayName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (student.isCurrentUser) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = youBadge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${student.entry.grade}$gradeSuffix",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    if (student.entry.streakCount > 0L) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.LocalFireDepartment,
                                contentDescription = null,
                                tint = colors.streakColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "${student.entry.streakCount}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = JetBrainsMonoFamily,
                                    color = colors.streakColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    if (student.entry.activeMultiplier > 1L) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.potionVioletBg)
                                .padding(horizontal = 5.dp, vertical = 1.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Bolt,
                                contentDescription = null,
                                tint = colors.potionViolet,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = FormatUtils.formatMultiplier(student.entry.activeMultiplier),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = JetBrainsMonoFamily,
                                    color = colors.potionViolet,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            // Score
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = colors.goldColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = FormatUtils.formatPointsCompact(student.displayPoints),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = JetBrainsMonoFamily,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Text(
                        text = pointsUnit,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}
