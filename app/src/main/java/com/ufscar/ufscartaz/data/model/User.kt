package com.ufscar.ufscartaz.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User entity for Room database
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val password: String,
//    val avatarId: Int = 0,

    val avatarPexelsId: Int? = null,
    val avatarUrl: String? = null
) 