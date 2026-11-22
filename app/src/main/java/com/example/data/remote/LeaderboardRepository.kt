package com.example.data.remote

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class LeaderboardRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    /**
     * Real-time stream of public leaderboard entries using a security-rule-compliant
     * `whereEqualTo("publicVisible", true)` filter. Sorting for Global and Weekly boards
     * happens in memory so no manual composite indexes are required.
     */
    fun observeLeaderboard(): Flow<List<LeaderboardEntry>> =
        db.collection(COLLECTION_LEADERBOARD)
            .whereEqualTo("publicVisible", true)
            .limit(100)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    LeaderboardEntry::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }

    /**
     * Upserts the authenticated user's real-time leaderboard entry after finishing a quiz
     * or editing settings. Uses full 13-key payload on initial creation and partial `.update(...)`
     * on subsequent updates so immutable `createdAt` and `userId` are strictly preserved.
     */
    suspend fun upsertCurrentUserLeaderboard(
        userId: String,
        displayName: String,
        avatarColorIndex: Int,
        grade: Int,
        totalPoints: Long,
        weeklyPoints: Long,
        weekKey: String,
        streakCount: Int,
        perfectQuizCount: Int,
        activeMultiplier: Long
    ): Result<Unit> = try {
        val signedInUid = auth.currentUser?.uid
            ?: error("User must be signed in with Google before updating leaderboard.")
        check(signedInUid == userId) { "Signed-in user mismatch; refusing cross-user leaderboard write." }

        val sanitizedName = displayName.trim().take(30).ifBlank { "Maarif Öğrencisi" }
        val safeAvatar = avatarColorIndex.coerceIn(0, 5).toLong()
        val safeGrade = grade.coerceIn(5, 12).toLong()
        val safeTotal = totalPoints.coerceAtLeast(0L)
        val safeWeekly = weeklyPoints.coerceAtLeast(0L)
        val safeWeekKey = if (WEEK_KEY_REGEX.matches(weekKey)) weekKey else WeekKeyUtils.currentIsoWeekKey()
        val safeStreak = streakCount.coerceIn(0, 10_000).toLong()
        val safePerfect = perfectQuizCount.coerceIn(0, 100_000).toLong()
        val safeMult = activeMultiplier.coerceIn(1L, 1_000_000L)

        val docRef = db.collection(COLLECTION_LEADERBOARD).document(userId)
        val existingSnap = docRef.get().await()

        if (!existingSnap.exists()) {
            val createPayload = mapOf<String, Any>(
                "userId" to userId,
                "displayName" to sanitizedName,
                "avatarColorIndex" to safeAvatar,
                "grade" to safeGrade,
                "totalPoints" to safeTotal,
                "weeklyPoints" to safeWeekly,
                "weekKey" to safeWeekKey,
                "streakCount" to safeStreak,
                "perfectQuizCount" to safePerfect,
                "activeMultiplier" to safeMult,
                "publicVisible" to true,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(createPayload).await()
        } else {
            val updatePayload = mapOf<String, Any>(
                "displayName" to sanitizedName,
                "avatarColorIndex" to safeAvatar,
                "grade" to safeGrade,
                "totalPoints" to safeTotal,
                "weeklyPoints" to safeWeekly,
                "weekKey" to safeWeekKey,
                "streakCount" to safeStreak,
                "perfectQuizCount" to safePerfect,
                "activeMultiplier" to safeMult,
                "publicVisible" to true,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.update(updatePayload).await()
        }
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.WRITE, "$COLLECTION_LEADERBOARD/$userId")
        Result.failure(e)
    }

    private companion object {
        const val COLLECTION_LEADERBOARD = "leaderboard"
        val WEEK_KEY_REGEX = Regex("^[0-9]{4}-W[0-9]{2}$")
    }
}
