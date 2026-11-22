package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_history")
data class QuizHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val grade: Int,
    val subjectId: String,
    val subjectName: String,
    val topicsSummary: String,
    val examModeLabel: String? = null,
    val correctCount: Int,
    val totalCount: Int,
    val accuracyPercent: Int,
    val basePoints: Long,
    val multiplierApplied: Long,
    val earnedPoints: Long,
    val isPerfect: Boolean,
    val potionWonMultiplier: Long = 0L,
    val completedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_goals")
data class StudyGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val grade: Int,
    val subjectId: String,
    val subjectName: String,
    val focusTopicsCsv: String,
    val periodType: String, // "DAILY" or "WEEKLY"
    val targetQuizzes: Int,
    val targetAccuracy: Int = 80,
    val isActive: Boolean = true,
    val createdAtMillis: Long = System.currentTimeMillis()
) {
    fun focusTopicsList(): List<String> =
        focusTopicsCsv.split("||").map { it.trim() }.filter { it.isNotEmpty() }
}
