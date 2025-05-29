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


class AvatarViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository

    private val avatarCategoriesConfig = listOf(
        AvatarCategoryConfig(label = "Pessoas Masculinas", query = "male face"),
        AvatarCategoryConfig(label = "Pessoas Femininas", query = "female face"),
        AvatarCategoryConfig(label = "Desenhos Animados", query = "cartoon avatar"),
        AvatarCategoryConfig(label = "Gatos", query = "cat face"),
        AvatarCategoryConfig(label = "Cachorros", query = "dog face"),
        AvatarCategoryConfig(label = "Fantasia", query = "fantasy character"),
        AvatarCategoryConfig(label = "Robôs", query = "robot")
    )

    private val _categorizedAvatarList = MutableStateFlow<List<AvatarCategory>>(emptyList())
    val categorizedAvatarList: StateFlow<List<AvatarCategory>> = _categorizedAvatarList.asStateFlow()

    private val _isLoadingAvatars = MutableStateFlow(false)
    val isLoadingAvatars: StateFlow<Boolean> = _isLoadingAvatars.asStateFlow()

    private val _fetchAvatarsError = MutableStateFlow<String?>(null)
    val fetchAvatarsError: StateFlow<String?> = _fetchAvatarsError.asStateFlow()

    private val _selectedAvatarPexelsId = MutableStateFlow<Int?>(null)
    val selectedAvatarPexelsId: StateFlow<Int?> = _selectedAvatarPexelsId.asStateFlow()

    private val _selectedAvatarUrl = MutableStateFlow<String?>(null)
    val selectedAvatarUrl: StateFlow<String?> = _selectedAvatarUrl.asStateFlow()

    private val _saveAvatarState = MutableStateFlow<SaveAvatarState>(SaveAvatarState.Idle)
    val saveAvatarState: StateFlow<SaveAvatarState> = _saveAvatarState.asStateFlow()

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        val apiService = NetworkModule.provideApiService()
        val pexelsApiService = NetworkModule.providePexelsApiService()
        userRepository = UserRepository(userDao, apiService, pexelsApiService)

        UserSession.currentUser.value?.let {
            _selectedAvatarPexelsId.value = it.avatarPexelsId
            _selectedAvatarUrl.value = it.avatarUrl
        }

        fetchAvatars()
    }


    private fun fetchAvatars() {
        _isLoadingAvatars.value = true
        _fetchAvatarsError.value = null

        viewModelScope.launch {
            val deferredResults = avatarCategoriesConfig.map { categoryConfig ->
                async {
                    categoryConfig.label to userRepository.fetchAvatars(categoryConfig.query)
                }
            }

            val results = deferredResults.awaitAll() // Wait for all API calls to complete

            val fetchedCategories = mutableListOf<AvatarCategory>()
            var hasError = false
            val errorMessages = mutableListOf<String>()

            results.forEach { (label, response) ->
                when (response) {
                    is ApiResponse.Success -> {
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