package com.example.data.remote

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.Calendar
import java.util.Locale

@Keep
@IgnoreExtraProperties
data class LeaderboardEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Maarif Öğrencisi",
    val avatarColorIndex: Long = 0L,
    val grade: Long = 8L,
    val totalPoints: Long = 0L,
    val weeklyPoints: Long = 0L,
    val weekKey: String = "",
    val streakCount: Long = 0L,
    val perfectQuizCount: Long = 0L,
    val activeMultiplier: Long = 1L,
    val publicVisible: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

enum class LeaderboardPeriod {
    GLOBAL,
    WEEKLY
}

data class RankedStudent(
    val rank: Int,
    val entry: LeaderboardEntry,
    val isCurrentUser: Boolean,
    val displayPoints: Long
)

sealed interface LeaderboardUiState {
    data object Loading : LeaderboardUiState
    data class Success(
        val globalRanking: List<RankedStudent>,
        val weeklyRanking: List<RankedStudent>,
        val currentWeekKey: String,
        val myGlobalRank: Int?,
        val myWeeklyRank: Int?
    ) : LeaderboardUiState
    data class Error(val message: String) : LeaderboardUiState
}

object WeekKeyUtils {
    /**
     * Produces an ISO-8601 week key strictly matching `^[0-9]{4}-W[0-9]{2}$` (e.g., "2026-W47").
     */
    fun currentIsoWeekKey(nowMillis: Long = System.currentTimeMillis()): String {
        val cal = Calendar.getInstance(Locale.US).apply {
            firstDayOfWeek = Calendar.MONDAY
            minimalDaysInFirstWeek = 4
            timeInMillis = nowMillis
        }
        val weekOfYear = cal.get(Calendar.WEEK_OF_YEAR).coerceIn(1, 53)
        val weekYear = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            cal.weekYear
        } else {
            val month = cal.get(Calendar.MONTH)
            val year = cal.get(Calendar.YEAR)
            when {
                month == Calendar.JANUARY && weekOfYear >= 52 -> year - 1
                month == Calendar.DECEMBER && weekOfYear == 1 -> year + 1
                else -> year
            }
        }
        return String.format(Locale.US, "%04d-W%02d", weekYear, weekOfYear)
    }
}
