package com.ufscar.ufscartaz.data.repository

import com.ufscar.ufscartaz.data.local.MovieHistoryDao
import com.ufscar.ufscartaz.data.model.MovieHistoryEntry
import kotlinx.coroutines.flow.Flow

class MovieHistoryRepository(private val movieHistoryDao: MovieHistoryDao) {

    /**
     * Add a movie to the user's history.
     */
    suspend fun addMovieToHistory(userId: Long, movieId: Int) {
        val entry = MovieHistoryEntry(
            userId = userId,
            movieId = movieId
            // timestamp defaults to System.currentTimeMillis()
        )
        movieHistoryDao.insertEntry(entry)
    }

    /**
     * Get the movie history for a specific user.
     */
    fun getUserHistory(userId: Long): Flow<List<MovieHistoryEntry>> {
        return movieHistoryDao.getHistoryForUser(userId)
    }

    /**
     * Optional: Clear all history for a user.
     */
    suspend fun clearUserHistory(userId: Long) {
        movieHistoryDao.clearHistoryForUser(userId)
    }

}