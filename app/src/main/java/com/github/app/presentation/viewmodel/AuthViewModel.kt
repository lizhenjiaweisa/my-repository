package com.github.app.presentation.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.app.data.model.AuthenticatedUser
import com.github.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationRequest
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: AuthenticatedUser) : AuthState()
    data class Error(val message: String) : AuthState()
    object LoggedOut : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            val token = authRepository.authState.first()
            _isLoggedIn.value = token != null
            
            if (token != null) {
                loadAuthenticatedUser()
            }
        }
    }

    fun getAuthorizationRequest(): AuthorizationRequest {
        return authRepository.getAuthorizationRequest()
    }

    fun exchangeCodeForToken(code: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            authRepository.exchangeCodeForToken(code)
                .onSuccess { token ->
                    Log.d("lzj", "exchangeCodeForToken: $token")
                    authRepository.saveAuthToken(token)
                    _isLoggedIn.value = true
                    loadAuthenticatedUser()
                    Log.d("lzj", "exchangeCodeForToken: 2222222222222222")
                }
                .onFailure { error ->
                    Log.d("lzj", "exchangeCodeForToken: ${error.message}")
                    _authState.value = AuthState.Error(error.message ?: "Authentication failed")
                }
        }
    }

    fun handleAuthCallback(code: String) {
        exchangeCodeForToken(code)
    }

    private suspend fun loadAuthenticatedUser() {
        authRepository.getAuthenticatedUser()
            .onSuccess { user ->
                Log.d("lzj", "loadAuthenticatedUser: "+user.toString())
                _authState.value = AuthState.Authenticated(user)
            }
            .onFailure { error ->
                Log.d("lzj", "loadAuthenticatedUser: ${error.message}")
                _authState.value = AuthState.Error(error.message ?: "Failed to load user")
            }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.clearAuthToken()
            _isLoggedIn.value = false
            _authState.value = AuthState.LoggedOut
        }
    }

    fun retryAuthentication() {
        _authState.value = AuthState.Idle
        checkAuthStatus()
    }

    fun setAuthError(message: String) {
        _authState.value = AuthState.Error(message)
    }

    fun startAuthorization(context: Context) {
        _authState.value = AuthState.Loading
    }

    fun getAuthorizationIntent(context: Context): Intent {
        return authRepository.getAuthorizationIntent(context)
    }
}