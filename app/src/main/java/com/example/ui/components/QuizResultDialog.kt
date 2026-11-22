package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.QuizResultSummary
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.LocalMaarifColors
import com.example.ui.util.FormatUtils

@Composable
fun QuizResultDialog(
    summary: QuizResultSummary,
    onReturnHome: () -> Unit,
    onReviewAnswers: () -> Unit
) {
    val colors = LocalMaarifColors.current
    val strings = LocalAppStrings.current
    val accuracy = if (summary.totalCount > 0) (summary.correctCount * 100) / summary.totalCount else 0

    Dialog(
        onDismissRequest = onReviewAnswers,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .testTag("quiz_result_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Celebratory Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                if (summary.isPerfect) {
                                    listOf(Color(0xFF0F172A), Color(0xFF1E1B4B))
                                } else {
                                    listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                                }
                            )
                        )
                        .padding(22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    if (summary.isPerfect) colors.goldColor else colors.emerald
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (summary.isPerfect) Icons.Rounded.EmojiEvents else Icons.Rounded.CheckCircle,
                                contentDescription = "Sınav Tamamlandı",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (summary.isPerfect) strings.resultPerfectTitle else strings.resultCompletedTitle,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = buildString {
                                append("${summary.grade}${strings.leaderboardGradeSuffix} • ${summary.subjectName}")
                                if (summary.examModeLabel != null) append(" (${summary.examModeLabel})")
                            },
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1)),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Score KPI Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ResultStatChip(
                            label = strings.resultCorrectLabel,
                            value = "${summary.correctCount}",
                            accent = colors.emerald,
                            bg = colors.emeraldBg.copy(alpha = 0.5f),
                            modifier = Modifier.weight(1f)
                        )
                        ResultStatChip(
                            label = strings.resultWrongLabel,
                            value = "${summary.wrongCount}",
                            accent = MaterialTheme.colorScheme.error,
                            bg = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f),
                            modifier = Modifier.weight(1f)
                        )
                        ResultStatChip(
                            label = strings.resultAccuracyLabel,
                            value = "$accuracy%",
                            accent = MaterialTheme.colorScheme.tertiary,
                            bg = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Points Calculation Breakdown
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = colors.subtleSurface,
                        border = BorderStroke(1.dp, colors.cardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(strings.resultBasePoints, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "+${FormatUtils.formatPointsFull(summary.basePoints)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = JetBrainsMonoFamily,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            if (summary.appliedMultiplier > 1L) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        strings.resultActiveMultiplier,
                                        style = MaterialTheme.typography.bodyMedium.copy(color = colors.potionViolet)
                                    )
                                    Text(
                                        "× ${FormatUtils.formatMultiplier(summary.appliedMultiplier)}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = JetBrainsMonoFamily,
                                            color = colors.potionViolet,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            HorizontalDivider(color = colors.cardBorder)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.Star,
                                        contentDescription = null,
                                        tint = colors.goldColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        strings.resultEarnedPoints,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Text(
                                    "+${FormatUtils.formatPointsFull(summary.totalEarnedPoints)}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = JetBrainsMonoFamily,
                                        color = colors.goldDark,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = colors.streakColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "${strings.resultDailyStreakPrefix} ${summary.streakCount} ${strings.profileDaysWord}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }
                                Text(
                                    "${strings.resultTotalPrefix} ${FormatUtils.formatPointsCompact(summary.newTotalPoints)} ${strings.leaderboardPointsUnit}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = JetBrainsMonoFamily,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }

                    // RARE MULTIPLIER POTION LOOT DROP CARD (on 100% Perfect Score!)
                    summary.potionDrop?.let { drop ->
                        val rarityColor = Color(drop.rarity.accentHex)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(rarityColor, Color(0xFF1E1B4B))
                                    )
                                )
                                .padding(16.dp)
                                .testTag("loot_drop_potion_card")
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFFFDE047),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "${strings.resultRarePotionDropped} • ${drop.rarity.label.uppercase()}",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = Color(0xFFFDE047),
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.Bolt,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${FormatUtils.formatMultiplier(drop.rarity.multiplier)} ${strings.resultPointMultiplierBanner}",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontFamily = JetBrainsMonoFamily,
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Black.copy(alpha = 0.28f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = strings.resultDurationLabel,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = JetBrainsMonoFamily,
                                                color = Color(0xFFFDE047),
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White.copy(alpha = 0.16f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "${strings.resultDropChancePrefix} ${drop.rarity.probabilityPercent}%",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (drop.replacedPrevious) {
                                        strings.resultPotionReplacedDesc
                                    } else {
                                        strings.resultPotionKeptDesc
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.92f)
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onReviewAnswers,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("result_review_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.resultReviewAnswers, style = MaterialTheme.typography.labelLarge)
                        }
                        Button(
                            onClick = onReturnHome,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("result_return_home_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Home,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.resultReturnHome,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultStatChip(
    label: String,
    value: String,
    accent: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = JetBrainsMonoFamily,
                color = accent,
                fontWeight = FontWeight.ExtraBold
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }
}
