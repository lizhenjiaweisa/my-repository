package com.github.app.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.app.data.model.Owner
import com.github.app.data.model.Repository
import com.github.app.data.repository.GitHubRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class RepositoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RepositoryViewModel
    private lateinit var gitHubRepository: GitHubRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        gitHubRepository = mockk()
        viewModel = RepositoryViewModel(gitHubRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `selectRepository should update selectedRepository`() {
        // Given
        val repository = Repository(
            id = 1,
            name = "test-repo",
            fullName = "user/test-repo",
            description = "Test repository",
            owner = Owner(
                login = "user",
                id = 123,
                avatarUrl = "https://example.com/avatar.png",
                htmlUrl = "https://github.com/user/test-repo"
            ),
            stars = 100,
            forks = 50,
            language = "Kotlin",
            topics = listOf("android", "kotlin"),
            htmlUrl = "https://github.com/user/test-repo",
            updatedAt = "2024-01-01T00:00:00Z",
            private = false,
            fork = false,
            size = 1000,
            openIssues = 10,
            defaultBranch = "main"
        )

        // When
        viewModel.selectRepository(repository)

        // Then
        assertEquals(repository, viewModel.selectedRepository.value)
    }
}