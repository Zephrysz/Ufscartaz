package com.ufscar.ufscartaz.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ufscar.ufscartaz.data.UserSession
import com.ufscar.ufscartaz.data.local.AppDatabase
import com.ufscar.ufscartaz.data.remote.ApiResponse
import com.ufscar.ufscartaz.data.remote.Avatar
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

    private val _avatarList = MutableStateFlow<List<Avatar>>(emptyList())
    val avatarList: StateFlow<List<Avatar>> = _avatarList.asStateFlow()

    private val _isLoadingAvatars = MutableStateFlow(false)
    val isLoadingAvatars: StateFlow<Boolean> = _isLoadingAvatars.asStateFlow()

    private val _fetchAvatarsError = MutableStateFlow<String?>(null)
    val fetchAvatarsError: StateFlow<String?> = _fetchAvatarsError.asStateFlow()

    // State for the currently selected avatar (using Pexels ID for identification)
    private val _selectedAvatarPexelsId = MutableStateFlow<Int?>(null)
    val selectedAvatarPexelsId: StateFlow<Int?> = _selectedAvatarPexelsId.asStateFlow()

    // State for the selected avatar URL (useful for displaying selection indicator)
    private val _selectedAvatarUrl = MutableStateFlow<String?>(null)
    val selectedAvatarUrl: StateFlow<String?> = _selectedAvatarUrl.asStateFlow()

    private val _saveAvatarState = MutableStateFlow<SaveAvatarState>(SaveAvatarState.Idle)
    val saveAvatarState: StateFlow<SaveAvatarState> = _saveAvatarState.asStateFlow()

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        val apiService = NetworkModule.provideApiService() // Get your existing API service
        val pexelsApiService = NetworkModule.providePexelsApiService() // Get Pexels service
        userRepository = UserRepository(userDao, apiService, pexelsApiService) // Pass both services

        // Restore selected avatar if user already has one from UserSession
        UserSession.currentUser.value?.let {
            // Read from the new fields
            _selectedAvatarPexelsId.value = it.avatarPexelsId
            _selectedAvatarUrl.value = it.avatarUrl
        }

        // Fetch avatars when ViewModel is created
        fetchAvatars()
    }

    private fun fetchAvatars() {
        _isLoadingAvatars.value = true
        _fetchAvatarsError.value = null

        viewModelScope.launch {
            when (val response = userRepository.fetchAvatars()) {
                is ApiResponse.Success -> {
                    _avatarList.value = response.data
                    _isLoadingAvatars.value = false
                }
                is ApiResponse.Error -> {
                    _avatarList.value = emptyList()
                    _isLoadingAvatars.value = false
                    _fetchAvatarsError.value = response.exception.message ?: "Failed to fetch avatars"
                }
            }
        }
    }

    fun selectAvatar(avatar: Avatar) {
        _selectedAvatarPexelsId.value = avatar.pexelsId
        _selectedAvatarUrl.value = avatar.url
    }

    fun deselectAvatar() {
        _selectedAvatarPexelsId.value = null
        _selectedAvatarUrl.value = null
    }

    fun saveAvatar() {
        val selectedPexelsId = _selectedAvatarPexelsId.value
        val selectedUrl = _selectedAvatarUrl.value

        // User can technically save with null (i.e., deselecting)
        // But if they clicked 'Continue' after fetching, something should be selected.
        // The UI button should be disabled if nothing is selected.
        // Let's allow saving nulls to clear the avatar if needed, but maybe the UI logic prevents this button state.
        // Let's update the check to allow saving nulls, which effectively removes the avatar.
        // if (selectedPexelsId == null || selectedUrl == null) { ... error logic ... }
        // The check in the UI button enabled state is important: selectedAvatarPexelsId != null

        _saveAvatarState.value = SaveAvatarState.Loading

        viewModelScope.launch {
            val currentUser = UserSession.currentUser.value
            if (currentUser != null) {
                // Call the correct updateAvatar method with nullable ID and URL
                when (val response = userRepository.updateAvatar(currentUser.id, selectedPexelsId, selectedUrl)) {
                    is ApiResponse.Success -> {
                        // Update UserSession with the new avatar info (can be null)
                        UserSession.updateAvatar(selectedPexelsId, selectedUrl)
                        _saveAvatarState.value = SaveAvatarState.Success
                    }
                    is ApiResponse.Error -> {
                        _saveAvatarState.value = SaveAvatarState.Error(response.exception.message ?: "Unknown error saving avatar")
                    }
                }
            } else {
                _saveAvatarState.value = SaveAvatarState.Error("No user logged in")
            }
        }
    }

    fun resetSaveAvatarState() {
        _saveAvatarState.value = SaveAvatarState.Idle
    }

    sealed class SaveAvatarState {
        object Idle : SaveAvatarState()
        object Loading : SaveAvatarState()
        object Success : SaveAvatarState()
        data class Error(val message: String) : SaveAvatarState()
    }
}