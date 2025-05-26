package com.ufscar.ufscartaz.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ufscar.ufscartaz.data.UserSession
import com.ufscar.ufscartaz.data.local.AppDatabase
import com.ufscar.ufscartaz.data.remote.ApiResponse
import com.ufscar.ufscartaz.data.remote.Avatar
import com.ufscar.ufscartaz.data.remote.AvatarCategory // Import AvatarCategory (the result class)
import com.ufscar.ufscartaz.data.remote.AvatarCategoryConfig // Import AvatarCategoryConfig
import com.ufscar.ufscartaz.data.repository.UserRepository
import com.ufscar.ufscartaz.di.NetworkModule

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for avatar selection
 */
class AvatarViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository

    // Define the category configurations with their labels and Pexels queries
    // Use the new AvatarCategoryConfig data class
    private val avatarCategoriesConfig = listOf(
        AvatarCategoryConfig(label = "Pessoas Masculinas", query = "male face"),
        AvatarCategoryConfig(label = "Pessoas Femininas", query = "female face"),
        AvatarCategoryConfig(label = "Desenhos Animados", query = "cartoon avatar"),
        AvatarCategoryConfig(label = "Gatos", query = "cat face"),
        AvatarCategoryConfig(label = "Cachorros", query = "dog face"),
        AvatarCategoryConfig(label = "Fantasia", query = "fantasy character"),
        AvatarCategoryConfig(label = "Robôs", query = "robot")
        // Add more categories as needed
    )

    // State for the categorized list of avatars (list of AvatarCategory results)
    private val _categorizedAvatarList = MutableStateFlow<List<AvatarCategory>>(emptyList())
    val categorizedAvatarList: StateFlow<List<AvatarCategory>> = _categorizedAvatarList.asStateFlow()

    // State for loading the avatar list (overall)
    private val _isLoadingAvatars = MutableStateFlow(false)
    val isLoadingAvatars: StateFlow<Boolean> = _isLoadingAvatars.asStateFlow()

    // State for error when fetching avatar list
    private val _fetchAvatarsError = MutableStateFlow<String?>(null)
    val fetchAvatarsError: StateFlow<String?> = _fetchAvatarsError.asStateFlow()

    // State for the currently selected avatar (using Pexels ID for identification)
    private val _selectedAvatarPexelsId = MutableStateFlow<Int?>(null)
    val selectedAvatarPexelsId: StateFlow<Int?> = _selectedAvatarPexelsId.asStateFlow()

    // State for the selected avatar URL (useful for displaying selection indicator)
    private val _selectedAvatarUrl = MutableStateFlow<String?>(null)
    val selectedAvatarUrl: StateFlow<String?> = _selectedAvatarUrl.asStateFlow()

    // State for the 'Save Avatar' action
    private val _saveAvatarState = MutableStateFlow<SaveAvatarState>(SaveAvatarState.Idle)
    val saveAvatarState: StateFlow<SaveAvatarState> = _saveAvatarState.asStateFlow()

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        val apiService = NetworkModule.provideApiService()
        val pexelsApiService = NetworkModule.providePexelsApiService()
        userRepository = UserRepository(userDao, apiService, pexelsApiService)

        // Restore selected avatar if user already has one
        UserSession.currentUser.value?.let {
            _selectedAvatarPexelsId.value = it.avatarPexelsId
            _selectedAvatarUrl.value = it.avatarUrl
        }

        // Fetch avatars for all categories when ViewModel is created
        fetchAvatars()
    }

    /**
     * Fetches avatars for all defined categories concurrently.
     */
    private fun fetchAvatars() {
        _isLoadingAvatars.value = true
        _fetchAvatarsError.value = null

        viewModelScope.launch {
            // Use async/awaitAll to fetch categories in parallel
            val deferredResults = avatarCategoriesConfig.map { categoryConfig ->
                async { // Each async block fetches one category based on its config
                    categoryConfig.label to userRepository.fetchAvatars(categoryConfig.query) // Pair label with response
                }
            }

            val results = deferredResults.awaitAll() // Wait for all API calls to complete

            val fetchedCategories = mutableListOf<AvatarCategory>()
            var hasError = false
            val errorMessages = mutableListOf<String>()

            results.forEach { (label, response) ->
                when (response) {
                    is ApiResponse.Success -> {
                        // Create the final AvatarCategory object here with the fetched avatars
                        if (response.data.isNotEmpty()) {
                            fetchedCategories.add(AvatarCategory(label = label, avatars = response.data)) // <-- Create AvatarCategory result here
                        }
                    }
                    is ApiResponse.Error -> {
                        hasError = true
                        errorMessages.add(response.exception.message ?: "Unknown error for category '$label'")
                    }
                }
            }

            _categorizedAvatarList.value = fetchedCategories.toList() // Convert mutable list to immutable
            _isLoadingAvatars.value = false

            if (hasError) {
                _fetchAvatarsError.value = "Falha ao carregar alguns avatares:\n" + errorMessages.joinToString("\n") // Portuguese message
            } else if (fetchedCategories.isEmpty() && avatarCategoriesConfig.isNotEmpty()) {
                // No categories returned any avatars, but we tried to fetch some
                _fetchAvatarsError.value = "Nenhum avatar encontrado para as categorias selecionadas." // Portuguese message
            } else if (avatarCategoriesConfig.isEmpty()) {
                // No categories configured
                _fetchAvatarsError.value = "Nenhuma categoria de avatar configurada." // Portuguese message
            }
        }
    }

    /**
     * Select an avatar from any category.
     */
    fun selectAvatar(avatar: Avatar) {
        _selectedAvatarPexelsId.value = avatar.pexelsId
        _selectedAvatarUrl.value = avatar.url
    }

    /**
     * Deselect the current avatar.
     */
    fun deselectAvatar() {
        _selectedAvatarPexelsId.value = null
        _selectedAvatarUrl.value = null
    }

    /**
     * Save the selected avatar to the user's profile.
     */
    fun saveAvatar() {
        val selectedPexelsId = _selectedAvatarPexelsId.value
        val selectedUrl = _selectedAvatarUrl.value

        // The button enabled state should prevent this case normally
        if (selectedPexelsId == null || selectedUrl == null) {
            _saveAvatarState.value = SaveAvatarState.Error("Por favor, selecione um avatar.") // Portuguese message
            return
        }

        _saveAvatarState.value = SaveAvatarState.Loading

        viewModelScope.launch {
            val currentUser = UserSession.currentUser.value
            if (currentUser != null) {
                when (val response = userRepository.updateAvatar(currentUser.id, selectedPexelsId, selectedUrl)) {
                    is ApiResponse.Success -> {
                        UserSession.updateAvatar(selectedPexelsId, selectedUrl)
                        _saveAvatarState.value = SaveAvatarState.Success
                    }
                    is ApiResponse.Error -> {
                        _saveAvatarState.value = SaveAvatarState.Error(response.exception.message ?: "Erro desconhecido ao salvar avatar") // Portuguese message
                    }
                }
            } else {
                _saveAvatarState.value = SaveAvatarState.Error("Nenhum usuário logado.") // Portuguese message
            }
        }
    }

    /**
     * Reset the save avatar state.
     */
    fun resetSaveAvatarState() {
        _saveAvatarState.value = SaveAvatarState.Idle
    }

    /**
     * States for the 'Save Avatar' action.
     */
    sealed class SaveAvatarState {
        object Idle : SaveAvatarState()
        object Loading : SaveAvatarState()
        object Success : SaveAvatarState()
        data class Error(val message: String) : SaveAvatarState()
    }
}