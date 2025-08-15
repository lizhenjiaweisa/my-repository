package com.github.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.app.data.model.Issue
import com.github.app.data.repository.GitHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class IssuesState {
    object Idle : IssuesState()
    object Loading : IssuesState()
    data class Success(val issues: List<Issue>) : IssuesState()
    data class Error(val message: String) : IssuesState()
}

sealed class CreateIssueState {
    object Idle : CreateIssueState()
    object Loading : CreateIssueState()
    data class Success(val issue: Issue) : CreateIssueState()
    data class Error(val message: String) : CreateIssueState()
}

@HiltViewModel
class IssueViewModel @Inject constructor(
    private val repository: GitHubRepository
) : ViewModel() {

    private val _issues = MutableStateFlow<IssuesState>(IssuesState.Idle)
    val issues: StateFlow<IssuesState> = _issues.asStateFlow()

    private val _createIssueState = MutableStateFlow<CreateIssueState>(CreateIssueState.Idle)
    val createIssueState: StateFlow<CreateIssueState> = _createIssueState.asStateFlow()

    fun loadRepositoryIssues(owner: String, repo: String) {
        viewModelScope.launch {
            _issues.value = IssuesState.Loading
            
            repository.getRepositoryIssues(owner, repo)
                .onSuccess { issues ->
                    _issues.value = IssuesState.Success(issues)
                }
                .onFailure { error ->
                    _issues.value = IssuesState.Error(error.message ?: "Failed to load issues")
                }
        }
    }

    fun createIssue(
        owner: String,
        repo: String,
        title: String,
        body: String? = null,
        labels: List<String>? = null
    ) {
        viewModelScope.launch {
            _createIssueState.value = CreateIssueState.Loading
            
            repository.createIssue(owner, repo, title, body, labels)
                .onSuccess { issue ->
                    _createIssueState.value = CreateIssueState.Success(issue)
                    // Refresh issues list
                    loadRepositoryIssues(owner, repo)
                }
                .onFailure { error ->
                    _createIssueState.value = CreateIssueState.Error(error.message ?: "Failed to create issue")
                }
        }
    }

    fun resetCreateIssueState() {
        _createIssueState.value = CreateIssueState.Idle
    }

    fun refreshIssues(owner: String, repo: String) {
        loadRepositoryIssues(owner, repo)
    }
}