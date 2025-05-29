package com.ufscar.ufscartaz.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ufscar.ufscartaz.data.model.MovieHistoryEntry
import kotlinx.coroutines.flow.Flow // Import Flow

@Dao
interface MovieHistoryDao {

    @Insert
    suspend fun .insertEntry(entry: MovieHistoryEntry)

    @Query("SELECT * FROM movie_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun getHistoryForUser(userId: Long): Flow<List<MovieHistoryEntry>>

    @Query("DELETE FROM movie_history WHERE timestamp < :cutoffTimestamp")
    suspend fun deleteOldEntries(cutoffTimestamp: Long): Int

    @Query("DELETE FROM movie_history WHERE userId = :userId")
    suspend fun clearHistoryForUser(userId: Long)
}