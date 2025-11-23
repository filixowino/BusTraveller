package com.example.bustraveller.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bustraveller.data.repository.TrackingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: TrackingRepository
) : ViewModel() {
    
    private val _isLoggedIn = MutableStateFlow(repository.isAdminLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginError.value = "Username and password are required"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _loginError.value = null
            
            repository.login(username, password)
                .onSuccess {
                    _isLoggedIn.value = true
                    _loginError.value = null
                }
                .onFailure { exception ->
                    _isLoggedIn.value = false
                    val errorMessage = when {
                        exception.message?.contains("Failed to connect") == true -> 
                            "Cannot connect to server. Make sure the backend is running on http://localhost:3000"
                        exception.message?.contains("timeout") == true -> 
                            "Connection timeout. Check your network and server status."
                        exception.message?.contains("Unable to resolve host") == true -> 
                            "Cannot reach server. For emulator use 10.0.2.2:3000, for physical device use your computer's IP address."
                        else -> exception.message ?: "Login failed. Check server connection."
                    }
                    _loginError.value = errorMessage
                }
            
            _isLoading.value = false
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _isLoggedIn.value = false
            _loginError.value = null
        }
    }
    
    fun clearError() {
        _loginError.value = null
    }
}

