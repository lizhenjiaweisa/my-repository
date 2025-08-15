package com.github.app.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.github.app.data.model.Repository
import com.github.app.data.model.User
import com.github.app.data.repository.GitHubRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class UserViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: UserViewModel
    private lateinit var gitHubRepository: GitHubRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        gitHubRepository = mockk()
        viewModel = UserViewModel(gitHubRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUserProfile should update state to Loading then Success`() = runTest {
        // Given
        val username = "testuser"
        val mockUser = User(
            login = "testuser",
            id = 123,
            avatarUrl = "https://example.com/avatar.png",
            name = "Test User",
            company = "Test Company",
            blog = "https://testuser.github.io",
            location = "Test Location",
            email = "test@example.com",
            bio = "Test bio",
            publicRepos = 10,
            followers = 100,
            following = 50,
            createdAt = "2020-01-01T00:00:00Z"
        )

        coEvery { gitHubRepository.getUserProfile(username) } returns Result.success(mockUser)

        // When
        viewModel.loadUserProfile(username)

        // Then
        viewModel.userState.test {
            val initialState = awaitItem()
            assertTrue(initialState is UserViewModel.UserState.Loading)
            
            val successState = awaitItem()
            assertTrue(successState is UserViewModel.UserState.Success)
            assertEquals(mockUser, (successState as UserViewModel.UserState.Success).user)
        }
    }

    @Test
    fun `loadUserProfile should update state to Error when repository fails`() = runTest {
        // Given
        val username = "testuser"
        val error = Exception("User not found")

        coEvery { gitHubRepository.getUserProfile(username) } returns Result.failure(error)

        // When
        viewModel.loadUserProfile(username)

        // Then
        viewModel.userState.test {
            val initialState = awaitItem()
            assertTrue(initialState is UserViewModel.UserState.Loading)
            
            val errorState = awaitItem()
            assertTrue(errorState is UserViewModel.UserState.Error)
            assertEquals(error.message, (errorState as UserViewModel.UserState.Error).message)
        }
    }

    @Test
    fun `loadUserRepositories should update state to Loading then Success`() = runTest {
        // Given
        val username = "testuser"
        val mockRepositories = listOf(
            Repository(
                id = 1,
                name = "user-repo",
                fullName = "testuser/user-repo",
                description = "User's repository",
                owner = Repository.Owner("testuser", "https://example.com/avatar.png"),
                stars = 100,
                forks = 50,
                language = "Kotlin",
                topics = listOf("android", "kotlin"),
                htmlUrl = "https://github.com/testuser/user-repo"
            )
        )

        coEvery { gitHubRepository.getUserRepositories(username, 1) } returns Result.success(mockRepositories)

        // When
        viewModel.loadUserRepositories(username)

        // Then
        viewModel.userRepositoriesState.test {
            val initialState = awaitItem()
            assertTrue(initialState is UserViewModel.UserRepositoriesState.Loading)
            
            val successState = awaitItem()
            assertTrue(successState is UserViewModel.UserRepositoriesState.Success)
            assertEquals(mockRepositories, (successState as UserViewModel.UserRepositoriesState.Success).repositories)
        }
    }

    @Test
    fun `loadUserRepositories should update state to Error when repository fails`() = runTest {
        // Given
        val username = "testuser"
        val error = Exception("Repositories not found")

        coEvery { gitHubRepository.getUserRepositories(username, 1) } returns Result.failure(error)

        // When
        viewModel.loadUserRepositories(username)

        // Then
        viewModel.userRepositoriesState.test {
            val initialState = awaitItem()
            assertTrue(initialState is UserViewModel.UserRepositoriesState.Loading)
            
            val errorState = awaitItem()
            assertTrue(errorState is UserViewModel.UserRepositoriesState.Error)
            assertEquals(error.message, (errorState as UserViewModel.UserRepositoriesState.Error).message)
        }
    }

    @Test
    fun `refreshUserData should reload both profile and repositories`() = runTest {
        // Given
        val username = "testuser"
        val mockUser = User(
            login = "testuser",
            id = 123,
            avatarUrl = "https://example.com/avatar.png",
            name = "Test User",
            publicRepos = 10,
            followers = 100,
            following = 50,
            createdAt = "2020-01-01T00:00:00Z"
        )
        val mockRepositories = listOf(
            Repository(
                id = 1,
                name = "user-repo",
                fullName = "testuser/user-repo",
                description = "User's repository",
                owner = Repository.Owner("testuser", "https://example.com/avatar.png"),
                stars = 100,
                forks = 50,
                language = "Kotlin",
                topics = listOf("android", "kotlin"),
                htmlUrl = "https://github.com/testuser/user-repo"
            )
        )

        coEvery { gitHubRepository.getUserProfile(username) } returns Result.success(mockUser)
        coEvery { gitHubRepository.getUserRepositories(username, 1) } returns Result.success(mockRepositories)

        // When
        viewModel.refreshUserData(username)

        // Then
        viewModel.userState.test {
            val initialState = awaitItem()
            assertTrue(initialState is UserViewModel.UserState.Loading)
            
            val successState = awaitItem()
            assertTrue(successState is UserViewModel.UserState.Success)
        }

        viewModel.userRepositoriesState.test {
            val initialState = awaitItem()
            assertTrue(initialState is UserViewModel.UserRepositoriesState.Loading)
            
            val successState = awaitItem()
            assertTrue(successState is UserViewModel.UserRepositoriesState.Success)
        }
    }

    @Test
    fun `clearUserData should reset states to Idle`() = runTest {
        // Given
        val username = "testuser"
        val mockUser = User(
            login = "testuser",
            id = 123,
            avatarUrl = "https://example.com/avatar.png",
            name = "Test User",
            publicRepos = 10,
            followers = 100,
            following = 50,
            createdAt = "2020-01-01T00:00:00Z"
        )
        val mockRepositories = listOf(
            Repository(
                id = 1,
                name = "user-repo",
                fullName = "testuser/user-repo",
                description = "User's repository",
                owner = Repository.Owner("testuser", "https://example.com/avatar.png"),
                stars = 100,
                forks = 50,
                language = "Kotlin",
                topics = listOf("android", "kotlin"),
                htmlUrl = "https://github.com/testuser/user-repo"
            )
        )

        coEvery { gitHubRepository.getUserProfile(username) } returns Result.success(mockUser)
        coEvery { gitHubRepository.getUserRepositories(username, 1) } returns Result.success(mockRepositories)

        viewModel.loadUserProfile(username)
        viewModel.loadUserRepositories(username)

        // When
        viewModel.clearUserData()

        // Then
        assertTrue(viewModel.userState.value is UserViewModel.UserState.Idle)
        assertTrue(viewModel.userRepositoriesState.value is UserViewModel.UserRepositoriesState.Idle)
    }

    @Test
    fun `loadMoreRepositories should append repositories`() = runTest {
        // Given
        val username = "testuser"
        val initialRepositories = listOf(
            Repository(
                id = 1,
                name = "repo1",
                fullName = "testuser/repo1",
                description = "Repository 1",
                owner = Repository.Owner("testuser", "https://example.com/avatar.png"),
                stars = 100,
                forks = 50,
                language = "Kotlin",
                topics = listOf("android"),
                htmlUrl = "https://github.com/testuser/repo1"
            )
        )
        val additionalRepositories = listOf(
            Repository(
                id = 2,
                name = "repo2",
                fullName = "testuser/repo2",
                description = "Repository 2",
                owner = Repository.Owner("testuser", "https://example.com/avatar.png"),
                stars = 200,
                forks = 100,
                language = "Java",
                topics = listOf("java"),
                htmlUrl = "https://github.com/testuser/repo2"
            )
        )

        coEvery { gitHubRepository.getUserRepositories(username, 1) } returns Result.success(initialRepositories)
        coEvery { gitHubRepository.getUserRepositories(username, 2) } returns Result.success(additionalRepositories)

        // When
        viewModel.loadUserRepositories(username)
        viewModel.loadMoreRepositories(username)

        // Then
        viewModel.userRepositoriesState.test {
            // Skip initial loading and success states
            repeat(2) { awaitItem() }
            
            val loadMoreState = awaitItem()
            assertTrue(loadMoreState is UserViewModel.UserRepositoriesState.Success)
            assertEquals(2, (loadMoreState as UserViewModel.UserRepositoriesState.Success).repositories.size)
        }
    }
}