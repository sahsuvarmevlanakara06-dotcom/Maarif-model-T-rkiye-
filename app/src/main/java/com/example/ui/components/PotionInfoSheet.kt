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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.PotionRarity
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.LocalMaarifColors
import com.example.ui.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PotionInfoSheet(
    totalPoints: Long,
    activeMultiplier: Long,
    remainingMillis: Long,
    highestPotionWon: Long,
    perfectQuizCount: Int,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = LocalMaarifColors.current
    val strings = LocalAppStrings.current
    val hasActive = activeMultiplier > 1L && remainingMillis > 0L

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Bolt,
                        contentDescription = null,
                        tint = colors.potionViolet,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.potionSheetTitle,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = strings.actionClose)
                }
            }

            // Active Status Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (hasActive) colors.potionVioletBg else colors.subtleSurface,
                border = BorderStroke(1.dp, if (hasActive) colors.potionViolet else colors.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (hasActive) {
                            "${strings.potionActiveLabel} ${FormatUtils.formatMultiplier(activeMultiplier)} (${strings.potionRemainingLabel} ${FormatUtils.formatCountdown(remainingMillis)})"
                        } else {
                            strings.potionNoneActive
                        },
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = if (hasActive) colors.potionViolet else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = strings.potionHowItWorks,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${strings.profileTotalPoints}: ${FormatUtils.formatPointsFull(totalPoints)}  •  ${strings.potionPerfectCountLabel} $perfectQuizCount  •  ${strings.potionHighestLabel} ${FormatUtils.formatMultiplier(highestPotionWon)}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFamily,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            Text(
                text = strings.potionTableTitle,
                style = MaterialTheme.typography.titleSmall
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                PotionRarity.entries.forEach { rarity ->
                    val accent = Color(rarity.accentHex)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.subtleSurface)
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(accent)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = rarity.label,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "%${rarity.probabilityPercent}",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(accent.copy(alpha = 0.14f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (rarity == PotionRarity.LEGENDARY) {
                                        Icon(
                                            imageVector = Icons.Rounded.AutoAwesome,
                                            contentDescription = null,
                                            tint = accent,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                    }
                                    Text(
                                        text = FormatUtils.formatMultiplier(rarity.multiplier),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontFamily = JetBrainsMonoFamily,
                                            color = accent,
                                            fontWeight = FontWeight.Bold
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
}
