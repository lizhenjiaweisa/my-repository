package com.github.app.data.repository

import com.github.app.data.model.*
import com.github.app.data.remote.GitHubApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubRepository @Inject constructor(
    private val apiService: GitHubApiService,
    private val authRepository: AuthRepository
) {

    suspend fun searchRepositories(
        query: String,
        language: String? = null,
        sort: String = "stars",
        page: Int = 1
    ): Result<RepositorySearchResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val searchQuery = if (language != null) {
                    "$query language:$language"
                } else {
                    query
                }

                val response = apiService.searchRepositories(
                    query = searchQuery,
                    sort = sort,
                    page = page
                )

                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getTrendingRepositories(page: Int = 1): Result<RepositorySearchResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTrendingRepositories()
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getRepositoryDetails(owner: String, repo: String): Result<Repository> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRepositoryDetails(owner, repo)
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getUserRepositories(username: String? = null): Result<List<Repository>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = if (username == null) {
                    // Get authenticated user's repositories
                    apiService.getUserPublicRepositories(username ?: "")
                } else {
                    // Get specific user's repositories
                    apiService.getUserPublicRepositories(username)
                }

                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getRepositoryIssues(owner: String, repo: String): Result<List<Issue>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRepositoryIssues(owner, repo)
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun createIssue(
        owner: String,
        repo: String,
        title: String,
        body: String? = null,
        labels: List<String>? = null
    ): Result<Issue> {
        return withContext(Dispatchers.IO) {
            try {
                val request = CreateIssueRequest(title, body, labels)
                val response = apiService.createIssue(owner, repo, request)
                
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getUserProfile(username: String? = null): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = if (username == null) {
                    apiService.getAuthenticatedUser()
                } else {
                    apiService.getUser(username)
                }

                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}