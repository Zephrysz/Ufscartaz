package com.ufscar.ufscartaz.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ufscar.ufscartaz.data.model.MovieHistoryEntry
import kotlinx.coroutines.flow.Flow // Import Flow

@Dao
interface MovieHistoryDao {

    /**
     * Insert a movie history entry.
     * Using OnConflictStrategy.IGNORE to avoid inserting the same entry multiple times if clicked rapidly,
     * or you could use OnConflictStrategy.REPLACE if you only want the *latest* timestamp for a user/movie pair.
     * For a full history, simply use @Insert without conflict strategy. Let's just use @Insert for now.
     */
    @Insert
    suspend fun insertEntry(entry: MovieHistoryEntry)

    /**
     * Get all movie history entries for a specific user, ordered by timestamp (most recent first).
     */
    @Query("SELECT * FROM movie_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun getHistoryForUser(userId: Long): Flow<List<MovieHistoryEntry>> // Return as Flow for observing changes

    /**
     * Optional: Delete old history entries (e.g., older than a month)
     */
    @Query("DELETE FROM movie_history WHERE timestamp < :cutoffTimestamp")
    suspend fun deleteOldEntries(cutoffTimestamp: Long): Int // Returns number of rows deleted

    /**
     * Optional: Clear history for a specific user
     */
    @Query("DELETE FROM movie_history WHERE userId = :userId")
    suspend fun clearHistoryForUser(userId: Long)
}