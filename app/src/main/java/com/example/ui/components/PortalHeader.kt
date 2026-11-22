package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.LocalMaarifColors
import com.example.ui.util.FormatUtils

@Composable
fun PortalHeader(
    streakCount: Int,
    totalPoints: Long,
    activeMultiplier: Long,
    multiplierRemainingMillis: Long,
    onPotionPillClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalMaarifColors.current
    val strings = LocalAppStrings.current
    val hasActivePotion = activeMultiplier > 1L && multiplierRemainingMillis > 0L

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.headerNavy)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Brand Identity
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(10.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_maarif_emblem),
                        contentDescription = stringResource(id = R.string.app_name),
                        modifier = Modifier.size(38.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = strings.appSubtitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Duolingo-style Gamification HUD
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Streak Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, colors.streakColor.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 9.dp, vertical = 5.dp)
                        .testTag("header_streak_pill"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocalFireDepartment,
                        contentDescription = stringResource(id = R.string.streak_cd),
                        tint = colors.streakColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "$streakCount",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = JetBrainsMonoFamily,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Points Pill (clickable to open Potion / Gamification details)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, colors.goldColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .clickable { onPotionPillClick() }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("header_points_pill"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = stringResource(id = R.string.points_cd),
                        tint = colors.goldColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = FormatUtils.formatPointsCompact(totalPoints),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = JetBrainsMonoFamily,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Compact Active Multiplier Pill in Top HUD when active
                if (hasActivePotion) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF6D28D9))
                            .border(1.dp, Color(0xFFFDE047).copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                            .clickable { onPotionPillClick() }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("header_potion_compact_pill"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Bolt,
                            contentDescription = stringResource(id = R.string.potion_cd),
                            tint = Color(0xFFFDE047),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = FormatUtils.formatMultiplier(activeMultiplier),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = JetBrainsMonoFamily,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                    }
                }
            }
        }

        // Active Multiplier Potion Strip inside Header (with live MM:SS countdown & 1-hour progress bar)
        AnimatedVisibility(
            visible = hasActivePotion,
            enter = fadeIn() + scaleIn(initialScale = 0.96f)
        ) {
            ActivePotionHeaderBanner(
                prefixLabel = strings.activePotionPrefix,
                multiplier = activeMultiplier,
                remainingMillis = multiplierRemainingMillis,
                onClick = onPotionPillClick
            )
        }

        // Signature Institutional Crimson Red Accent Bar (#C8102E)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(colors.headerCrimsonBorder)
        )
    }
}

@Composable
private fun ActivePotionHeaderBanner(
    prefixLabel: String,
    multiplier: Long,
    remainingMillis: Long,
    onClick: () -> Unit
) {
    val pulseTransition = rememberInfiniteTransition(label = "potion_pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(850),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bolt_scale"
    )

    val isLegendary = multiplier >= 500L
    val gradientColors = if (isLegendary) {
        listOf(Color(0xFF7C3AED), Color(0xFFC8102E), Color(0xFFD97706))
    } else {
        listOf(Color(0xFF4C1D95), Color(0xFF7C3AED))
    }
    val remainingFraction = (remainingMillis.toFloat() / 3_600_000f).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 10.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.horizontalGradient(gradientColors))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
            .testTag("header_active_potion_banner"),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Bolt,
                        contentDescription = stringResource(id = R.string.potion_cd),
                        tint = Color(0xFFFDE047),
                        modifier = Modifier
                            .size(16.dp)
                            .graphicsLayer {
                                scaleX = pulseScale
                                scaleY = pulseScale
                            }
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$prefixLabel: ${FormatUtils.formatMultiplier(multiplier)}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.3f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = FormatUtils.formatCountdown(remainingMillis),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = JetBrainsMonoFamily,
                        color = Color(0xFFFDE047),
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Visual 1-hour remaining duration bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.22f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(remainingFraction)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFFDE047))
            )
        }
    }
}
