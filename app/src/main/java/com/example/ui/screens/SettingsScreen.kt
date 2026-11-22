package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.UserProgressData
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.LocalMaarifColors
import kotlinx.coroutines.delay

private val SettingsAvatarColors = listOf(
    Color(0xFFC8102E),
    Color(0xFF2563EB),
    Color(0xFF059669),
    Color(0xFF7C3AED),
    Color(0xFFD97706),
    Color(0xFF0891B2)
)

@Composable
fun SettingsScreen(
    progress: UserProgressData,
    userEmail: String?,
    onSaveProfile: (name: String, avatarColorIndex: Int, grade: Int) -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onToggleDarkTheme: (Boolean) -> Unit,
    onSyncLeaderboard: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val colors = LocalMaarifColors.current

    var draftName by remember(progress.studentName) { mutableStateOf(progress.studentName) }
    var draftAvatar by remember(progress.avatarColorIndex) { mutableIntStateOf(progress.avatarColorIndex) }
    var draftGrade by remember(progress.selectedGrade) { mutableIntStateOf(progress.selectedGrade) }
    var showSavedBanner by remember { mutableStateOf(false) }

    val currentLanguage = AppLanguage.fromCode(progress.appLanguage)

    LaunchedEffect(showSavedBanner) {
        if (showSavedBanner) {
            delay(2500L)
            showSavedBanner = false
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 760.dp)
                .testTag("settings_screen"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = strings.settingsTitle,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                        )
                        Text(
                            text = strings.settingsSubtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            // 1. App Language Selector (Prominent Top Card with 2x2 Grid for 4 Languages)
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, colors.cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Language,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.settingsLanguageSection,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = strings.settingsLanguageDesc,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }

                        val langRows = AppLanguage.entries.chunked(2)
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            langRows.forEach { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    pair.forEach { lang ->
                                        val selected = lang == currentLanguage
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = if (selected) {
                                                MaterialTheme.colorScheme.tertiaryContainer
                                            } else {
                                                colors.subtleSurface
                                            },
                                            border = BorderStroke(
                                                width = if (selected) 1.8.dp else 1.dp,
                                                color = if (selected) MaterialTheme.colorScheme.tertiary else colors.cardBorder
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(14.dp))
                                                .clickable { onSelectLanguage(lang) }
                                                .testTag("settings_lang_${lang.code}")
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(
                                                                if (selected) {
                                                                    MaterialTheme.colorScheme.tertiary
                                                                } else {
                                                                    MaterialTheme.colorScheme.surface
                                                                }
                                                            )
                                                            .padding(horizontal = 7.dp, vertical = 4.dp)
                                                    ) {
                                                        Text(
                                                            text = lang.flagBadge,
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                                fontWeight = FontWeight.ExtraBold
                                                            )
                                                        )
                                                    }
                                                    Text(
                                                        text = lang.nativeLabel,
                                                        style = MaterialTheme.typography.labelLarge.copy(
                                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                                        )
                                                    )
                                                }
                                                if (selected) {
                                                    Icon(
                                                        imageVector = Icons.Rounded.CheckCircle,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.tertiary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    if (pair.size == 1) {
                                        Spacer(Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. Student Identity & Profile Section (Username, Avatar, Grade)
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, colors.cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = strings.settingsProfileSection,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        OutlinedTextField(
                            value = draftName,
                            onValueChange = { if (it.length <= 30) draftName = it },
                            label = { Text(strings.settingsUsernameLabel) },
                            placeholder = { Text(strings.settingsUsernamePlaceholder) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_username_input")
                        )

                        // Avatar Palette
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = strings.settingsAvatarLabel,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SettingsAvatarColors.forEachIndexed { idx, color ->
                                    val selected = idx == draftAvatar
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .border(
                                                width = if (selected) 3.dp else 1.dp,
                                                color = if (selected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .clickable { draftAvatar = idx }
                                            .testTag("settings_avatar_$idx"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (selected) {
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

                        // Grade Selector (5-12)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = strings.settingsGradeLabel,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                (5..12).forEach { grade ->
                                    FilterChip(
                                        selected = draftGrade == grade,
                                        onClick = { draftGrade = grade },
                                        label = { Text("$grade${strings.leaderboardGradeSuffix}") },
                                        modifier = Modifier.testTag("settings_grade_$grade")
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(visible = showSavedBanner) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = colors.emeraldBg,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = colors.emerald,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = strings.settingsSavedFeedback,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = colors.emerald,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                onSaveProfile(draftName, draftAvatar, draftGrade)
                                showSavedBanner = true
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("settings_save_profile_button")
                        ) {
                            Icon(Icons.Rounded.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(strings.settingsSaveProfile, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 3. Appearance (Dark Mode Toggle)
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, colors.cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DarkMode,
                                contentDescription = null,
                                tint = colors.potionViolet,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = strings.settingsDarkModeLabel,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = strings.settingsDarkModeDesc,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Switch(
                            checked = progress.darkTheme,
                            onCheckedChange = onToggleDarkTheme,
                            modifier = Modifier.testTag("settings_dark_mode_switch")
                        )
                    }
                }
            }

            // 4. Google Account & Cloud Sync
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, colors.cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.AccountCircle,
                                contentDescription = null,
                                tint = colors.emerald,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = strings.settingsAccountSection,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        if (!userEmail.isNullOrBlank()) {
                            Text(
                                text = "${strings.settingsSignedInAs}: $userEmail",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        OutlinedButton(
                            onClick = onSyncLeaderboard,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("settings_sync_leaderboard_button")
                        ) {
                            Icon(Icons.Rounded.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(strings.settingsSyncLeaderboard)
                        }

                        Button(
                            onClick = onSignOut,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("settings_sign_out_button")
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(strings.settingsSignOut, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
