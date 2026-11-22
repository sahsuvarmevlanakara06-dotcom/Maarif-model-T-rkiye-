package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.maarifDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "maarif_portal_progress"
)

data class UserProgressData(
    val totalPoints: Long = 0L,
    val weeklyPoints: Long = 0L,
    val weeklyPointsWeekKey: String = "",
    val streakCount: Int = 0,
    val lastActiveEpochDay: Long = -1L,
    val activeMultiplier: Long = 1L,
    val multiplierExpiresAtMillis: Long = 0L,
    val selectedGrade: Int = 8,
    val highestPotionWon: Long = 1L,
    val totalPotionsWon: Int = 0,
    val perfectQuizCount: Int = 0,
    val studentName: String = "Maarif Öğrencisi",
    val avatarColorIndex: Int = 0,
    val appLanguage: String = "TR",
    val darkTheme: Boolean = false
)

class UserProgressStore(private val context: Context) {

    private object Keys {
        val TOTAL_POINTS = longPreferencesKey("total_points")
        val WEEKLY_POINTS = longPreferencesKey("weekly_points")
        val WEEKLY_POINTS_WEEK_KEY = stringPreferencesKey("weekly_points_week_key")
        val STREAK_COUNT = intPreferencesKey("streak_count")
        val LAST_ACTIVE_DAY = longPreferencesKey("last_active_epoch_day")
        val ACTIVE_MULTIPLIER = longPreferencesKey("active_multiplier")
        val MULTIPLIER_EXPIRES_AT = longPreferencesKey("multiplier_expires_at")
        val SELECTED_GRADE = intPreferencesKey("selected_grade")
        val HIGHEST_POTION_WON = longPreferencesKey("highest_potion_won")
        val TOTAL_POTIONS_WON = intPreferencesKey("total_potions_won")
        val PERFECT_QUIZ_COUNT = intPreferencesKey("perfect_quiz_count")
        val STUDENT_NAME = stringPreferencesKey("student_name")
        val AVATAR_COLOR_INDEX = intPreferencesKey("avatar_color_index")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
    }

    val progressFlow: Flow<UserProgressData> = context.maarifDataStore.data.map { prefs ->
        UserProgressData(
            totalPoints = prefs[Keys.TOTAL_POINTS] ?: 0L,
            weeklyPoints = prefs[Keys.WEEKLY_POINTS] ?: 0L,
            weeklyPointsWeekKey = prefs[Keys.WEEKLY_POINTS_WEEK_KEY] ?: "",
            streakCount = prefs[Keys.STREAK_COUNT] ?: 0,
            lastActiveEpochDay = prefs[Keys.LAST_ACTIVE_DAY] ?: -1L,
            activeMultiplier = prefs[Keys.ACTIVE_MULTIPLIER] ?: 1L,
            multiplierExpiresAtMillis = prefs[Keys.MULTIPLIER_EXPIRES_AT] ?: 0L,
            selectedGrade = prefs[Keys.SELECTED_GRADE] ?: 8,
            highestPotionWon = prefs[Keys.HIGHEST_POTION_WON] ?: 1L,
            totalPotionsWon = prefs[Keys.TOTAL_POTIONS_WON] ?: 0,
            perfectQuizCount = prefs[Keys.PERFECT_QUIZ_COUNT] ?: 0,
            studentName = prefs[Keys.STUDENT_NAME]?.takeIf { it.isNotBlank() } ?: "Maarif Öğrencisi",
            avatarColorIndex = prefs[Keys.AVATAR_COLOR_INDEX] ?: 0,
            appLanguage = prefs[Keys.APP_LANGUAGE] ?: "TR",
            darkTheme = prefs[Keys.DARK_THEME] ?: false
        )
    }

    suspend fun getSnapshot(): UserProgressData = progressFlow.first()

    suspend fun setSelectedGrade(grade: Int) {
        context.maarifDataStore.edit { prefs ->
            prefs[Keys.SELECTED_GRADE] = grade.coerceIn(5, 12)
        }
    }

    suspend fun updateStudentProfile(name: String, avatarColorIndex: Int) {
        context.maarifDataStore.edit { prefs ->
            val sanitized = name.trim().take(30).ifBlank { "Maarif Öğrencisi" }
            prefs[Keys.STUDENT_NAME] = sanitized
            prefs[Keys.AVATAR_COLOR_INDEX] = avatarColorIndex.coerceIn(0, 5)
        }
    }

    suspend fun seedDisplayNameIfDefault(googleDisplayName: String?) {
        val candidate = googleDisplayName?.trim()?.take(30) ?: return
        if (candidate.isBlank()) return
        context.maarifDataStore.edit { prefs ->
            val existing = prefs[Keys.STUDENT_NAME]
            if (existing.isNullOrBlank() || existing == "Maarif Öğrencisi") {
                prefs[Keys.STUDENT_NAME] = candidate
            }
        }
    }

    suspend fun setAppLanguage(languageCode: String) {
        context.maarifDataStore.edit { prefs ->
            prefs[Keys.APP_LANGUAGE] = languageCode.uppercase()
        }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.maarifDataStore.edit { prefs ->
            prefs[Keys.DARK_THEME] = enabled
        }
    }

    suspend fun clearExpiredMultiplier(nowMillis: Long) {
        context.maarifDataStore.edit { prefs ->
            val expires = prefs[Keys.MULTIPLIER_EXPIRES_AT] ?: 0L
            if (expires > 0L && nowMillis >= expires) {
                prefs[Keys.ACTIVE_MULTIPLIER] = 1L
                prefs[Keys.MULTIPLIER_EXPIRES_AT] = 0L
            }
        }
    }

    suspend fun recordQuizReward(
        earnedPoints: Long,
        todayEpochDay: Long,
        currentWeekKey: String,
        isPerfect: Boolean,
        newPotionMultiplier: Long?,
        newPotionExpiresAt: Long?
    ): UserProgressData {
        var updated = UserProgressData()
        context.maarifDataStore.edit { prefs ->
            val currentPoints = prefs[Keys.TOTAL_POINTS] ?: 0L
            val nextPoints = safeAddPoints(currentPoints, earnedPoints)
            prefs[Keys.TOTAL_POINTS] = nextPoints

            val storedWeekKey = prefs[Keys.WEEKLY_POINTS_WEEK_KEY] ?: ""
            val baseWeekly = if (storedWeekKey == currentWeekKey) {
                prefs[Keys.WEEKLY_POINTS] ?: 0L
            } else {
                0L
            }
            val nextWeekly = safeAddPoints(baseWeekly, earnedPoints)
            prefs[Keys.WEEKLY_POINTS] = nextWeekly
            prefs[Keys.WEEKLY_POINTS_WEEK_KEY] = currentWeekKey

            val lastDay = prefs[Keys.LAST_ACTIVE_DAY] ?: -1L
            val currentStreak = prefs[Keys.STREAK_COUNT] ?: 0
            val nextStreak = when {
                lastDay == todayEpochDay -> maxOf(1, currentStreak)
                lastDay == todayEpochDay - 1L -> currentStreak + 1
                else -> 1
            }
            prefs[Keys.STREAK_COUNT] = nextStreak
            prefs[Keys.LAST_ACTIVE_DAY] = todayEpochDay

            val nextPerfect = (prefs[Keys.PERFECT_QUIZ_COUNT] ?: 0) + if (isPerfect) 1 else 0
            prefs[Keys.PERFECT_QUIZ_COUNT] = nextPerfect

            var activeMult = prefs[Keys.ACTIVE_MULTIPLIER] ?: 1L
            var expiresAt = prefs[Keys.MULTIPLIER_EXPIRES_AT] ?: 0L
            val now = System.currentTimeMillis()
            if (expiresAt > 0L && now >= expiresAt) {
                activeMult = 1L
                expiresAt = 0L
            }

            var totalPotions = prefs[Keys.TOTAL_POTIONS_WON] ?: 0
            if (newPotionMultiplier != null && newPotionExpiresAt != null) {
                totalPotions += 1
                prefs[Keys.TOTAL_POTIONS_WON] = totalPotions
                if (newPotionMultiplier >= activeMult || activeMult <= 1L) {
                    activeMult = newPotionMultiplier
                    expiresAt = newPotionExpiresAt
                    prefs[Keys.ACTIVE_MULTIPLIER] = activeMult
                    prefs[Keys.MULTIPLIER_EXPIRES_AT] = expiresAt
                }
                val best = maxOf(prefs[Keys.HIGHEST_POTION_WON] ?: 1L, newPotionMultiplier)
                prefs[Keys.HIGHEST_POTION_WON] = best
            }

            updated = UserProgressData(
                totalPoints = nextPoints,
                weeklyPoints = nextWeekly,
                weeklyPointsWeekKey = currentWeekKey,
                streakCount = nextStreak,
                lastActiveEpochDay = todayEpochDay,
                activeMultiplier = activeMult,
                multiplierExpiresAtMillis = expiresAt,
                selectedGrade = prefs[Keys.SELECTED_GRADE] ?: 8,
                highestPotionWon = prefs[Keys.HIGHEST_POTION_WON] ?: 1L,
                totalPotionsWon = totalPotions,
                perfectQuizCount = nextPerfect,
                studentName = prefs[Keys.STUDENT_NAME]?.takeIf { it.isNotBlank() } ?: "Maarif Öğrencisi",
                avatarColorIndex = prefs[Keys.AVATAR_COLOR_INDEX] ?: 0,
                appLanguage = prefs[Keys.APP_LANGUAGE] ?: "TR",
                darkTheme = prefs[Keys.DARK_THEME] ?: false
            )
        }
        return updated
    }

    private fun safeAddPoints(current: Long, delta: Long): Long {
        if (delta <= 0L) return current
        return if (Long.MAX_VALUE - current < delta) Long.MAX_VALUE else current + delta
    }
}
