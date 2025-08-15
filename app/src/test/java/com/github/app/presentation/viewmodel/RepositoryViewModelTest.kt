package com.github.app.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
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
    fun `searchRepositories should update state to Loading then Success`() = runTest {
        // Given
        val query = "android"
        val mockRepositories = listOf(
            Repository(
                id = 1,
                name = "test-repo",
                fullName = "user/test-repo",
                description = "Test repository",
                owner = Repository.Owner("user", "https://example.com/avatar.png"),
                stars = 100,
                forks = 50,
                language = "Kotlin",
                topics = listOf("android", "kotlin"),
                htmlUrl = "https://github.com/user/test-repo"
            )
        )

        coEvery { gitHubRepository.searchRepositories(query, null, 1) } returns Result.success(mockRepositories)

        // When
        viewModel.searchRepositories(query)

        // Then
        viewModel.repositoriesState.test {
            val initialState = awaitItem()
            assertTrue(initialState is RepositoryViewModel.RepositoriesState.Loading)
            
            val successState = awaitItem()
            assertTrue(successState is RepositoryViewModel.RepositoriesState.Success)
            assertEquals(mockRepositories, (successState as RepositoryViewModel.RepositoriesState.Success).repositories)
        }
    }

    @Test
    fun `searchRepositories should update state to Error when repository fails`() = runTest {
        // Given
        val query = "android"
        val error = Exception("Network error")

        coEvery { gitHubRepository.searchRepositories(query, null, 1) } returns Result.failure(error)

        // When
        viewModel.searchRepositories(query)

        // Then
        viewModel.repositoriesState.test {
            val initialState = awaitItem()
            assertTrue(initialState is RepositoryViewModel.RepositoriesState.Loading)
            
            val errorState = awaitItem()
            assertTrue(errorState is RepositoryViewModel.RepositoriesState.Error)
            assertEquals(error.message, (errorState as RepositoryViewModel.RepositoriesState.Error).message)
        }
    }

    @Test
    fun `getTrendingRepositories should update state to Success`() = runTest {
        // Given
        val mockRepositories = listOf(
            Repository(
                id = 1,
                name = "trending-repo",
                fullName = "user/trending-repo",
                description = "Trending repository",
                owner = Repository.Owner("user", "https://example.com/avatar.png"),
                stars = 1000,
                forks = 500,
                language = "Kotlin",
                topics = listOf("trending", "kotlin"),
                htmlUrl = "https://github.com/user/trending-repo"
            )
        )

        coEvery { gitHubRepository.getTrendingRepositories(1) } returns Result.success(mockRepositories)

        // When
        viewModel.getTrendingRepositories()

        // Then
        viewModel.repositoriesState.test {
            val initialState = awaitItem()
            assertTrue(initialState is RepositoryViewModel.RepositoriesState.Loading)
            
            val successState = awaitItem()
            assertTrue(successState is RepositoryViewModel.RepositoriesState.Success)
            assertEquals(mockRepositories, (successState as RepositoryViewModel.RepositoriesState.Success).repositories)
        }
    }

    @Test
    fun `selectRepository should update selectedRepository`() {
        // Given
        val repository = Repository(
            id = 1,
            name = "selected-repo",
            fullName = "user/selected-repo",
            description = "Selected repository",
            owner = Repository.Owner("user", "https://example.com/avatar.png"),
            stars = 50,
            forks = 10,
            language = "Java",
            topics = listOf("java"),
            htmlUrl = "https://github.com/user/selected-repo"
        )

        // When
        viewModel.selectRepository(repository)

        // Then
        assertEquals(repository, viewModel.selectedRepository.value)
    }

    @Test
    fun `clearSelection should clear selectedRepository`() {
        // Given
        val repository = Repository(
            id = 1,
            name = "selected-repo",
            fullName = "user/selected-repo",
            description = "Selected repository",
            owner = Repository.Owner("user", "https://example.com/avatar.png"),
            stars = 50,
            forks = 10,
            language = "Java",
            topics = listOf("java"),
            htmlUrl = "https://github.com/user/selected-repo"
        )
        viewModel.selectRepository(repository)

        // When
        viewModel.clearSelection()

        // Then
        assertNull(viewModel.selectedRepository.value)
    }

    @Test
    fun `loadMore should append repositories`() = runTest {
        // Given
        val initialRepositories = listOf(
            Repository(
                id = 1,
                name = "repo1",
                fullName = "user/repo1",
                description = "Repository 1",
                owner = Repository.Owner("user", "https://example.com/avatar.png"),
                stars = 100,
                forks = 50,
                language = "Kotlin",
                topics = listOf("android"),
                htmlUrl = "https://github.com/user/repo1"
            )
        )
        val additionalRepositories = listOf(
            Repository(
                id = 2,
                name = "repo2",
                fullName = "user/repo2",
                description = "Repository 2",
                owner = Repository.Owner("user", "https://example.com/avatar.png"),
                stars = 200,
                forks = 100,
                language = "Java",
                topics = listOf("java"),
                htmlUrl = "https://github.com/user/repo2"
            )
        )

        coEvery { gitHubRepository.searchRepositories("android", null, 1) } returns Result.success(initialRepositories)
        coEvery { gitHubRepository.searchRepositories("android", null, 2) } returns Result.success(additionalRepositories)

        // When
        viewModel.searchRepositories("android")
        viewModel.loadMore()

        // Then
        viewModel.repositoriesState.test {
            // Skip initial loading and success states
            repeat(2) { awaitItem() }
            
            val loadMoreState = awaitItem()
            assertTrue(loadMoreState is RepositoryViewModel.RepositoriesState.Success)
            assertEquals(2, (loadMoreState as RepositoryViewModel.RepositoriesState.Success).repositories.size)
        }
    }

    @Test
    fun `searchWithLanguage should filter by language`() = runTest {
        // Given
        val query = "android"
        val language = "Kotlin"
        val mockRepositories = listOf(
            Repository(
                id = 1,
                name = "kotlin-repo",
                fullName = "user/kotlin-repo",
                description = "Kotlin repository",
                owner = Repository.Owner("user", "https://example.com/avatar.png"),
                stars = 100,
                forks = 50,
                language = "Kotlin",
                topics = listOf("android", "kotlin"),
                htmlUrl = "https://github.com/user/kotlin-repo"
            )
        )

        coEvery { gitHubRepository.searchRepositories(query, language, 1) } returns Result.success(mockRepositories)

        // When
        viewModel.searchWithLanguage(query, language)

        // Then
        viewModel.repositoriesState.test {
            val initialState = awaitItem()
            assertTrue(initialState is RepositoryViewModel.RepositoriesState.Loading)
            
            val successState = awaitItem()
            assertTrue(successState is RepositoryViewModel.RepositoriesState.Success)
            assertEquals(mockRepositories, (successState as RepositoryViewModel.RepositoriesState.Success).repositories)
        }
    }
}