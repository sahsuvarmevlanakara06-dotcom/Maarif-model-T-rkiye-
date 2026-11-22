package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.QuizHistoryEntity
import com.example.data.local.UserProgressData
import com.example.data.model.SubjectMasteryInfo
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LocalMaarifColors
import com.example.ui.util.FormatUtils

@Composable
fun StatsScreen(
    progress: UserProgressData,
    history: List<QuizHistoryEntity>,
    subjectMastery: List<SubjectMasteryInfo>,
    onOpenPotionSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalMaarifColors.current
    val averageAccuracy = if (history.isEmpty()) 0 else history.map { it.accuracyPercent }.average().toInt()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "stats_kpis") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Performans Özeti",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatKpiCard(
                        icon = Icons.Rounded.Quiz,
                        label = "Çözülen Sınav",
                        value = "${history.size}",
                        accent = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                    StatKpiCard(
                        icon = Icons.Rounded.Insights,
                        label = "Ortalama Başarı",
                        value = "%$averageAccuracy",
                        accent = colors.emerald,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatKpiCard(
                        icon = Icons.Rounded.EmojiEvents,
                        label = "Kusursuz (%100)",
                        value = "${progress.perfectQuizCount}",
                        accent = colors.goldDark,
                        modifier = Modifier.weight(1f)
                    )
                    StatKpiCard(
                        icon = Icons.Rounded.Bolt,
                        label = "En Yüksek İksir",
                        value = FormatUtils.formatMultiplier(progress.highestPotionWon),
                        accent = colors.potionViolet,
                        modifier = Modifier.weight(1f),
                        onClick = onOpenPotionSheet
                    )
                }
            }
        }

        // Subject Mastery Section
        item(key = "mastery_header") {
            Text(
                text = "Ders Bazlı Ustalık Analizi",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        if (subjectMastery.isEmpty()) {
            item(key = "empty_mastery") {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colors.subtleSurface,
                    border = BorderStroke(1.dp, colors.cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Insights,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Henüz tamamlanmış sınav yok",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "İlk sınavını çözdüğünde ders bazlı başarı yüzdelerin ve çalışma önerilerin burada görünecek.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        } else {
            items(subjectMastery, key = { it.subjectId }) { item ->
                val barColor = when {
                    item.averageAccuracy >= 85 -> colors.emerald
                    item.averageAccuracy >= 65 -> colors.goldColor
                    else -> MaterialTheme.colorScheme.primary
                }
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, colors.cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${item.grade}. Sınıf • ${item.subjectName}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Ort. %${item.averageAccuracy}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = JetBrainsMonoFamily,
                                    color = barColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        LinearProgressIndicator(
                            progress = { item.averageAccuracy / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = barColor,
                            trackColor = colors.subtleSurface
                        )
                        Text(
                            text = "${item.attempts} sınav • En iyi: %${item.bestAccuracy} • Toplam +${FormatUtils.formatPointsCompact(item.totalPointsEarned)} puan",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        }

        // Recent Quiz History
        if (history.isNotEmpty()) {
            item(key = "history_header") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Son Sınav Geçmişi",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            items(history.take(25), key = { it.id }) { entry ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, colors.cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "${entry.grade}. Sınıf • ${entry.subjectName}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                if (entry.potionWonMultiplier > 1L) {
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
                                                text = FormatUtils.formatMultiplier(entry.potionWonMultiplier),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = JetBrainsMonoFamily,
                                                    color = colors.potionViolet,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            Text(
                                text = "${entry.correctCount}/${entry.totalCount} Doğru (%${entry.accuracyPercent}) • ${FormatUtils.formatShortDate(entry.completedAtMillis)}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "+${FormatUtils.formatPointsCompact(entry.earnedPoints)}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = JetBrainsMonoFamily,
                                    color = colors.goldDark,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                            if (entry.multiplierApplied > 1L) {
                                Text(
                                    text = "×${FormatUtils.formatMultiplier(entry.multiplierApplied)}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = JetBrainsMonoFamily,
                                        color = colors.potionViolet
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatKpiCard(
    icon: ImageVector,
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val colors = LocalMaarifColors.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, colors.cardBorder),
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accent.copy(alpha = 0.13f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = accent,
                    modifier = Modifier.size(19.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = JetBrainsMonoFamily,
                    fontWeight = FontWeight.ExtraBold
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}
