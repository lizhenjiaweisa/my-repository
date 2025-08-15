package com.github.app.data.repository

import com.github.app.data.model.IssueRequest
import com.github.app.data.remote.GithubApiService
import com.github.app.domain.model.Issue
import com.github.app.domain.model.Repository
import com.github.app.domain.model.User
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GithubRepositoryTest {
    private lateinit var githubRepository: GithubRepository
    private val apiService = mockk<GithubApiService>()

    @Before
    fun setup() {
        githubRepository = GithubRepository(apiService)
    }

    @Test
    fun `searchRepositories returns success`() = runTest {
        // Given
        val query = "android"
        val mockResponse = listOf(
            Repository(
                id = 1,
                name = "Test Repo",
                fullName = "user/Test Repo",
                description = "Test description",
                stars = 100,
                forks = 50,
                language = "Kotlin",
                owner = User(id = 1, login = "user", avatarUrl = "")
            )
        )
        coEvery { apiService.searchRepositories(query, any(), any()) } returns mockResponse

        // When
        val result = githubRepository.searchRepositories(query)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockResponse, result.getOrNull())
    }

    @Test
    fun `getRepositoryIssues returns success`() = runTest {
        // Given
        val owner = "test"
        val repo = "test-repo"
        val mockIssues = listOf(
            Issue(
                id = 1,
                title = "Test Issue",
                body = "Test body",
                state = "open",
                user = User(id = 1, login = "user", avatarUrl = "")
            )
        )
        coEvery { apiService.getRepositoryIssues(owner, repo, any(), any()) } returns mockIssues

        // When
        val result = githubRepository.getRepositoryIssues(owner, repo)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockIssues, result.getOrNull())
    }

    @Test
    fun `createIssue returns success`() = runTest {
        // Given
        val owner = "test"
        val repo = "test-repo"
        val request = IssueRequest(title = "New Issue", body = "Issue body")
        val mockIssue = Issue(
            id = 1,
            title = "New Issue",
            body = "Issue body",
            state = "open",
            user = User(id = 1, login = "user", avatarUrl = "")
        )
        coEvery { apiService.createIssue(owner, repo, request) } returns mockIssue

        // When
        val result = githubRepository.createIssue(owner, repo, request)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockIssue, result.getOrNull())
    }

    @Test
    fun `getUserProfile returns success`() = runTest {
        // Given
        val username = "testuser"
        val mockUser = User(
            id = 1,
            login = "testuser",
            name = "Test User",
            bio = "Test bio",
            followers = 100,
            following = 50,
            publicRepos = 10,
            avatarUrl = ""
        )
        coEvery { apiService.getUserProfile(username) } returns mockUser

        // When
        val result = githubRepository.getUserProfile(username)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockUser, result.getOrNull())
    }

    @Test
    fun `getUserRepositories returns success`() = runTest {
        // Given
        val username = "testuser"
        val mockRepos = listOf(
            Repository(
                id = 1,
                name = "User Repo",
                fullName = "testuser/User Repo",
                description = "User repo description",
                stars = 50,
                forks = 20,
                language = "Java",
                owner = User(id = 1, login = "testuser", avatarUrl = "")
            )
        )
        coEvery { apiService.getUserRepositories(username, any(), any()) } returns mockRepos

        // When
        val result = githubRepository.getUserRepositories(username)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockRepos, result.getOrNull())
    }
}