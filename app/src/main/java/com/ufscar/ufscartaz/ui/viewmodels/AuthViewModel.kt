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
 * ViewModel for authentication (login/registration)
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        val apiService = NetworkModule.provideApiService()
        val pexelsApiService = NetworkModule.providePexelsApiService() // Get the Pexels API service
        userRepository = UserRepository(userDao, apiService, pexelsApiService)
    }

    /**
     * Login with email and password
     */
    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            when (val response = userRepository.login(email, password)) {
                is ApiResponse.Success -> {
                    UserSession.login(response.data)
                    _loginState.value = LoginState.Success
                }
                is ApiResponse.Error -> {
                    _loginState.value = LoginState.Error(response.exception.message ?: "Unknown error")
                }
            }
        }
    }
    
    /**
     * Register with name, email, and password
     */
    fun register(name: String, email: String, password: String) {
        _registerState.value = RegisterState.Loading

        viewModelScope.launch {
            when (val response = userRepository.register(name, email, password)) {
                is ApiResponse.Success -> {
                    UserSession.login(response.data)
                    _registerState.value = RegisterState.Success
                }
                is ApiResponse.Error -> {
                    _registerState.value = RegisterState.Error(response.exception.message ?: "Unknown error")
                }
            }
        }
    }
    
    /**
     * Reset login state
     */
    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }
    
    /**
     * Reset register state
     */
    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }
    
    /**
     * Logout the current user
     */
    fun logout() {
        UserSession.logout()
    }
    
    /**
     * Login state
     */
    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }
    
    /**
     * Register state
     */
    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        object Success : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
} 