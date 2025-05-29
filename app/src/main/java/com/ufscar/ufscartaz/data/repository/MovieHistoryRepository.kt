package com.ufscar.ufscartaz.data.repository

import com.ufscar.ufscartaz.data.local.MovieHistoryDao
import com.ufscar.ufscartaz.data.model.MovieHistoryEntry
import kotlinx.coroutines.flow.Flow

class MovieHistoryRepository(private val movieHistoryDao: MovieHistoryDao) {

    suspend fun addMovieToHistory(userId: Long, movieId: Int) {
        val entry = MovieHistoryEntry(
            userId = userId,
            movieId = movieId
            // timestamp defaults to System.currentTimeMillis()
        )
        movieHistoryDao.insertEntry(entry)
    }

    fun getUserHistory(userId: Long): Flow<List<MovieHistoryEntry>> {
        return movieHistoryDao.getHistoryForUser(userId)
    }

    suspend fun clearUserHistory(userId: Long) {
        movieHistoryDao.clearHistoryForUser(userId)
    }

}