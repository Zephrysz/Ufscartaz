package com.ufscar.ufscartaz.data

import com.ufscar.ufscartaz.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton class to manage the current user's session
 */
object UserSession {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    // Login the user
    fun login(user: User) {
        _currentUser.value = user
    }
    
    // Logout the user
    fun logout() {
        _currentUser.value = null
    }
    
    // Update user's avatar
    fun updateAvatar(avatarId: Int) {
        _currentUser.value = _currentUser.value?.copy(avatarId = avatarId)
    }
    
    // Check if user is logged in
    val isLoggedIn: Boolean
        get() = _currentUser.value != null
} 