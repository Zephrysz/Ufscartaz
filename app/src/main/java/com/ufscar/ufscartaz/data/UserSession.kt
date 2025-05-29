package com.ufscar.ufscartaz.data

import com.ufscar.ufscartaz.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


object UserSession {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    fun login(user: User) {
        _currentUser.value = user
    }
    
    fun logout() {
        _currentUser.value = null
    }
    
    fun updateAvatar(avatarPexelsId: Int?, avatarUrl: String?) {
        _currentUser.value = _currentUser.value?.copy(
            avatarPexelsId = avatarPexelsId,
            avatarUrl = avatarUrl
        )
    }
    
    val isLoggedIn: Boolean
        get() = _currentUser.value != null
} 