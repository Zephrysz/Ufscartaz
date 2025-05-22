package com.ufscar.ufscartaz.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ufscar.ufscartaz.data.UserSession
import com.ufscar.ufscartaz.data.local.AppDatabase
import com.ufscar.ufscartaz.data.remote.ApiResponse
import com.ufscar.ufscartaz.data.repository.UserRepository
import com.ufscar.ufscartaz.di.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for avatar selection
 */
class AvatarViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository
    
    private val _avatarState = MutableStateFlow<AvatarState>(AvatarState.Idle)
    val avatarState: StateFlow<AvatarState> = _avatarState.asStateFlow()
    
    private val _selectedAvatarId = MutableStateFlow(0)
    val selectedAvatarId: StateFlow<Int> = _selectedAvatarId.asStateFlow()
    
    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        val apiService = NetworkModule.provideApiService()
        userRepository = UserRepository(userDao, apiService)
        
        UserSession.currentUser.value?.let {
            _selectedAvatarId.value = it.avatarId
        }
    }
    
    /**
     * Select an avatar
     */
    fun selectAvatar(avatarId: Int) {
        _selectedAvatarId.value = avatarId
    }
    
    /**
     * Save the selected avatar
     */
    fun saveAvatar() {
        _avatarState.value = AvatarState.Loading
        
        viewModelScope.launch {
            val currentUser = UserSession.currentUser.value
            if (currentUser != null) {
                when (val response = userRepository.updateAvatar(currentUser.id, _selectedAvatarId.value)) {
                    is ApiResponse.Success -> {
                        UserSession.updateAvatar(_selectedAvatarId.value)
                        _avatarState.value = AvatarState.Success
                    }
                    is ApiResponse.Error -> {
                        _avatarState.value = AvatarState.Error(response.exception.message ?: "Unknown error")
                    }
                }
            } else {
                _avatarState.value = AvatarState.Error("No user logged in")
            }
        }
    }
    
    /**
     * Reset avatar state
     */
    fun resetAvatarState() {
        _avatarState.value = AvatarState.Idle
    }
    
    /**
     * Avatar state
     */
    sealed class AvatarState {
        object Idle : AvatarState()
        object Loading : AvatarState()
        object Success : AvatarState()
        data class Error(val message: String) : AvatarState()
    }
} 