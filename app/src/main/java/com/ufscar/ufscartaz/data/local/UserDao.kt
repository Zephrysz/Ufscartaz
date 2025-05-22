package com.ufscar.ufscartaz.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ufscar.ufscartaz.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    /**
     * Insert a user in the database. If the user already exists, replace it.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    /**
     * Get a user by email.
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?
    
    /**
     * Get a user by email and password for login validation.
     */
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, password: String): User?
    
    /**
     * Get all users as a Flow.
     */
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>
    
    /**
     * Update user's avatar.
     */
    @Query("UPDATE users SET avatarId = :avatarId WHERE id = :userId")
    suspend fun updateUserAvatar(userId: Long, avatarId: Int)
    
    /**
     * Check if email exists.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email LIMIT 1)")
    suspend fun emailExists(email: String): Boolean
}

