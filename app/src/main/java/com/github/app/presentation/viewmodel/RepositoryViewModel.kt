package com.github.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.app.data.model.Repository
import com.github.app.data.model.RepositorySearchResponse
import com.github.app.data.repository.GitHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RepositoryState {
    object Idle : RepositoryState()
    object Loading : RepositoryState()
    data class Success(val data: List<Repository>, val totalCount: Int = 0) : RepositoryState()
    data class Error(val message: String) : RepositoryState()
}

@HiltViewModel
class RepositoryViewModel @Inject constructor(
    private val repository: GitHubRepository
) : ViewModel() {

    private val _repositories = MutableStateFlow<RepositoryState>(RepositoryState.Idle)
    val repositories: StateFlow<RepositoryState> = _repositories.asStateFlow()

    private val _trendingRepositories = MutableStateFlow<RepositoryState>(RepositoryState.Idle)
    val trendingRepositories: StateFlow<RepositoryState> = _trendingRepositories.asStateFlow()

    private val _selectedRepository = MutableStateFlow<Repository?>(null)
    val selectedRepository: StateFlow<Repository?> = _selectedRepository.asStateFlow()
    
    private val _repositoryDetails = MutableStateFlow<RepositoryState>(RepositoryState.Idle)
    val repositoryDetails: StateFlow<RepositoryState> = _repositoryDetails.asStateFlow()

    private var currentPage = 1
    private var isLoading = false
    private var hasMorePages = true

    fun searchRepositories(
        query: String,
        language: String? = null,
        refresh: Boolean = false
    ) {
        if (isLoading) return

        if (refresh) {
            currentPage = 1
            hasMorePages = true
        }

        viewModelScope.launch {
            isLoading = true
            _repositories.value = RepositoryState.Loading

            repository.searchRepositories(query, language, page = currentPage)
                .onSuccess { response ->
                    val newRepos = if (refresh) {
                        response.items
                    } else {
                        val current = (_repositories.value as? RepositoryState.Success)?.data ?: emptyList()
                        current + response.items
                    }

                    _repositories.value = RepositoryState.Success(newRepos, response.totalCount)
                    hasMorePages = newRepos.size < response.totalCount
                    if (!refresh) currentPage++
                }
                .onFailure { error ->
                    _repositories.value = RepositoryState.Error(error.message ?: "Unknown error")
                }
                .also {
                    isLoading = false
                }
        }
    }

    fun getTrendingRepositories(refresh: Boolean = false) {
        if (isLoading) return

        if (refresh) {
            currentPage = 1
            hasMorePages = true
        }

        viewModelScope.launch {
            isLoading = true
            _trendingRepositories.value = RepositoryState.Loading

            repository.getTrendingRepositories(page = currentPage)
                .onSuccess { response ->
                    val newRepos = if (refresh) {
                        response.items
                    } else {
                        val current = (_trendingRepositories.value as? RepositoryState.Success)?.data ?: emptyList()
                        current + response.items
                    }

                    _trendingRepositories.value = RepositoryState.Success(newRepos, response.totalCount)
                    hasMorePages = newRepos.size < response.totalCount
                    if (!refresh) currentPage++
                }
                .onFailure { error ->
                    _trendingRepositories.value = RepositoryState.Error(error.message ?: "Unknown error")
                }
                .also {
                    isLoading = false
                }
        }
    }

    fun loadMoreRepositories(query: String, language: String? = null) {
        if (isLoading || !hasMorePages) return
        searchRepositories(query, language, refresh = false)
    }

    fun loadMoreTrending() {
        if (isLoading || !hasMorePages) return
        getTrendingRepositories(refresh = false)
    }

    fun selectRepository(repository: Repository) {
        _selectedRepository.value = repository
    }

    fun clearSelection() {
        _selectedRepository.value = null
    }

    fun refreshRepositories(query: String, language: String? = null) {
        searchRepositories(query, language, refresh = true)
    }

    fun refreshTrending() {
        getTrendingRepositories(refresh = true)
    }

    fun getRepositoryDetails(owner: String, repoName: String) {
        viewModelScope.launch {
            _repositoryDetails.value = RepositoryState.Loading
            
            repository.getRepositoryDetails(owner, repoName)
                .onSuccess { repository ->
                    _repositoryDetails.value = RepositoryState.Success(listOf(repository), 1)
                    _selectedRepository.value = repository
                }
                .onFailure { error ->
                    _repositoryDetails.value = RepositoryState.Error(
                        error.message ?: "Failed to load repository details"
                    )
                }
        }
    }

    
}