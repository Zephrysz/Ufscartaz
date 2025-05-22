package com.ufscar.ufscartaz.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AvatarSelectionScreen : ViewModel() {



    fun navigateToWelcome(onNavigate: () -> Unit) {
        viewModelScope.launch {
            onNavigate()
        }
    }
}