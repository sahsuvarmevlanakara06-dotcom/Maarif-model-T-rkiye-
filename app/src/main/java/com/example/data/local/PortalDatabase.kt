package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizHistoryDao {
    @Query("SELECT * FROM quiz_history ORDER BY completedAtMillis DESC")
    fun observeAllHistory(): Flow<List<QuizHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(entry: QuizHistoryEntity)
}

@Dao
interface StudyGoalDao {
    @Query("SELECT * FROM study_goals ORDER BY isActive DESC, createdAtMillis DESC")
    fun observeAllGoals(): Flow<List<StudyGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: StudyGoalEntity)

    @Update
    suspend fun updateGoal(goal: StudyGoalEntity)

    @Query("DELETE FROM study_goals WHERE id = :id")
    suspend fun deleteGoalById(id: Long)
}

@Database(
    entities = [QuizHistoryEntity::class, StudyGoalEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PortalDatabase : RoomDatabase() {
    abstract fun quizHistoryDao(): QuizHistoryDao
    abstract fun studyGoalDao(): StudyGoalDao

    companion object {
        @Volatile
        private var INSTANCE: PortalDatabase? = null

        fun getInstance(context: Context): PortalDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PortalDatabase::class.java,
                    "maarif_portal_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
