package com.github.app.data.remote

import com.github.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface GitHubApiService {

    // Repository endpoints
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): Response<RepositorySearchResponse>

    @GET("repositories")
    suspend fun getPublicRepositories(
        @Query("since") since: Long? = null,
        @Query("per_page") perPage: Int = 30
    ): Response<List<Repository>>

    @GET("repos/{owner}/{repo}")
    suspend fun getRepositoryDetails(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<Repository>

    // User endpoints
    @GET("user")
    suspend fun getAuthenticatedUser(): Response<User>

    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): Response<User>



    @GET("user/repos")
    suspend fun getUserRepositories(
        @Query("type") type: String = "owner",
        @Query("sort") sort: String = "updated",
        @Query("direction") direction: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): Response<List<Repository>>

    @GET("users/{username}/repos")
    suspend fun getUserPublicRepositories(
        @Path("username") username: String,
        @Query("type") type: String = "owner",
        @Query("sort") sort: String = "updated",
        @Query("direction") direction: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): Response<List<Repository>>

    // Issue endpoints
    @GET("repos/{owner}/{repo}/issues")
    suspend fun getRepositoryIssues(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("state") state: String = "open",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): Response<List<Issue>>

    @POST("repos/{owner}/{repo}/issues")
    suspend fun createIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body request: CreateIssueRequest
    ): Response<Issue>

    // Trending repositories (using search with date filter)
    @GET("search/repositories")
    suspend fun getTrendingRepositories(
        @Query("q") query: String = "created:>2024-01-01",
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("per_page") perPage: Int = 30
    ): Response<RepositorySearchResponse>
}