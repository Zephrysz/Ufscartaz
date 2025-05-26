package com.ufscar.ufscartaz.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "movie_history",
    // Define a foreign key constraint linking history entries to users
    foreignKeys = [ForeignKey(
        entity = User::class, // Reference the User entity
        parentColumns = ["id"], // Reference the User's primary key
        childColumns = ["userId"], // The column in this table that holds the user ID
        onDelete = ForeignKey.CASCADE // Optional: if a user is deleted, delete their history
    )],
    // Add indices for efficient querying, especially by userId
    indices = [Index(value = ["userId", "timestamp"], unique = false)]
)
data class MovieHistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // Unique ID for this history entry
    val userId: Long, // ID of the user who clicked the movie
    val movieId: Int, // ID of the movie that was clicked (TMDB ID)
    val timestamp: Long = System.currentTimeMillis() // Timestamp of the click
)