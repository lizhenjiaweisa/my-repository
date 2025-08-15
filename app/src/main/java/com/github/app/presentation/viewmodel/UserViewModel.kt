package com.github.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.app.data.model.Repository
import com.github.app.data.model.User
import com.github.app.data.repository.GitHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UserState {
    object Idle : UserState()
    object Loading : UserState()
    data class Success(val user: User) : UserState()
    data class Error(val message: String) : UserState()
}

sealed class UserRepositoriesState {
    object Idle : UserRepositoriesState()
    object Loading : UserRepositoriesState()
    data class Success(val repositories: List<Repository>) : UserRepositoriesState()
    data class Error(val message: String) : UserRepositoriesState()
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: GitHubRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserState>(UserState.Idle)
    val userProfile: StateFlow<UserState> = _userProfile.asStateFlow()

    private val _userRepositories = MutableStateFlow<UserRepositoriesState>(UserRepositoriesState.Idle)
    val userRepositories: StateFlow<UserRepositoriesState> = _userRepositories.asStateFlow()

    fun loadUserProfile(username: String? = null) {
        viewModelScope.launch {
            _userProfile.value = UserState.Loading
            
            repository.getUserProfile(username)
                .onSuccess { user ->
                    _userProfile.value = UserState.Success(user)
                }
                .onFailure { error ->
                    _userProfile.value = UserState.Error(error.message ?: "Failed to load user profile")
                }
        }
    }

    fun loadUserRepositories(username: String? = null) {
        viewModelScope.launch {
            _userRepositories.value = UserRepositoriesState.Loading
            
            repository.getUserRepositories(username)
                .onSuccess { repositories ->
                    _userRepositories.value = UserRepositoriesState.Success(repositories)
                }
                .onFailure { error ->
                    _userRepositories.value = UserRepositoriesState.Error(error.message ?: "Failed to load repositories")
                }
        }
    }

    fun refreshUserData(username: String? = null) {
        loadUserProfile(username)
        loadUserRepositories(username)
    }

    fun clearUserData() {
        _userProfile.value = UserState.Idle
        _userRepositories.value = UserRepositoriesState.Idle
    }
}