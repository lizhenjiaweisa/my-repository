package com.github.app.presentation.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.app.data.model.Repository
import com.github.app.data.model.User
import com.github.app.presentation.viewmodel.UserViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: UserViewModel
    private lateinit var mockUserProfile: MutableStateFlow<UserViewModel.UserState>
    private lateinit var mockUserRepositories: MutableStateFlow<UserViewModel.RepositoriesState>

    @Before
    fun setup() {
        mockViewModel = mockk(relaxed = true)
        mockUserProfile = MutableStateFlow(UserViewModel.UserState.Loading)
        mockUserRepositories = MutableStateFlow(UserViewModel.RepositoriesState.Loading)
        every { mockViewModel.userProfile } returns mockUserProfile
        every { mockViewModel.userRepositories } returns mockUserRepositories
    }

    @Test
    fun userProfileScreen_displaysLoadingState() {
        // Given
        mockUserProfile.value = UserViewModel.UserState.Loading
        mockUserRepositories.value = UserViewModel.RepositoriesState.Loading

        // When
        composeTestRule.setContent {
            UserProfileScreen(
                username = "testuser",
                viewModel = mockViewModel,
                onBackClick = {},
                onRepositoryClick = {},
                onUserClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun userProfileScreen_displaysUserProfile() {
        // Given
        val mockUser = User(
            login = "testuser",
            name = "Test User",
            avatarUrl = "https://example.com/avatar.jpg",
            bio = "Android Developer",
            location = "San Francisco",
            company = "GitHub",
            blog = "https://testuser.dev",
            followers = 1500,
            following = 500,
            publicRepos = 25,
            publicGists = 10,
            createdAt = "2020-01-15T10:30:00Z"
        )
        mockUserProfile.value = UserViewModel.UserState.Success(mockUser)
        mockUserRepositories.value = UserViewModel.RepositoriesState.Success(emptyList(), false)

        // When
        composeTestRule.setContent {
            UserProfileScreen(
                username = "testuser",
                viewModel = mockViewModel,
                onBackClick = {},
                onRepositoryClick = {},
                onUserClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Test User").assertIsDisplayed()
        composeTestRule.onNodeWithText("Android Developer").assertIsDisplayed()
        composeTestRule.onNodeWithText("San Francisco").assertIsDisplayed()
        composeTestRule.onNodeWithText("GitHub").assertIsDisplayed()
    }

    @Test
    fun userProfileScreen_displaysUserStats() {
        // Given
        val mockUser = User(
            login = "testuser",
            name = "Test User",
            avatarUrl = "https://example.com/avatar.jpg",
            followers = 1500,
            following = 500,
            publicRepos = 25
        )
        mockUserProfile.value = UserViewModel.UserState.Success(mockUser)
        mockUserRepositories.value = UserViewModel.RepositoriesState.Success(emptyList(), false)

        // When
        composeTestRule.setContent {
            UserProfileScreen(
                username = "testuser",
                viewModel = mockViewModel,
                onBackClick = {},
                onRepositoryClick = {},
                onUserClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("1,500").assertIsDisplayed()
        composeTestRule.onNodeWithText("500").assertIsDisplayed()
        composeTestRule.onNodeWithText("25").assertIsDisplayed()
    }

    @Test
    fun userProfileScreen_displaysRepositories() {
        // Given
        val mockUser = User(
            login = "testuser",
            name = "Test User",
            avatarUrl = "https://example.com/avatar.jpg"
        )
        val mockRepositories = listOf(
            Repository(
                id = 1,
                name = "repo1",
                fullName = "testuser/repo1",
                description = "First repository",
                owner = Repository.Owner("testuser", "https://example.com/avatar.jpg"),
                stars = 100,
                forks = 20,
                language = "Kotlin",
                topics = listOf("android", "kotlin")
            ),
            Repository(
                id = 2,
                name = "repo2",
                fullName = "testuser/repo2",
                description = "Second repository",
                owner = Repository.Owner("testuser", "https://example.com/avatar.jpg"),
                stars = 200,
                forks = 30,
                language = "Java",
                topics = listOf("java", "spring")
            )
        )
        mockUserProfile.value = UserViewModel.UserState.Success(mockUser)
        mockUserRepositories.value = UserViewModel.RepositoriesState.Success(mockRepositories, false)

        // When
        composeTestRule.setContent {
            UserProfileScreen(
                username = "testuser",
                viewModel = mockViewModel,
                onBackClick = {},
                onRepositoryClick = {},
                onUserClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("repo1").assertIsDisplayed()
        composeTestRule.onNodeWithText("First repository").assertIsDisplayed()
        composeTestRule.onNodeWithText("repo2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Second repository").assertIsDisplayed()
    }

    @Test
    fun userProfileScreen_displaysErrorState() {
        // Given
        val errorMessage = "Failed to load user profile"
        mockUserProfile.value = UserViewModel.UserState.Error(errorMessage)
        mockUserRepositories.value = UserViewModel.RepositoriesState.Error("Repository error")

        // When
        composeTestRule.setContent {
            UserProfileScreen(
                username = "testuser",
                viewModel = mockViewModel,
                onBackClick = {},
                onRepositoryClick = {},
                onUserClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun backButton_triggersBackAction() {
        // Given
        val mockUser = User(
            login = "testuser",
            name = "Test User",
            avatarUrl = "https://example.com/avatar.jpg"
        )
        mockUserProfile.value = UserViewModel.UserState.Success(mockUser)
        mockUserRepositories.value = UserViewModel.RepositoriesState.Success(emptyList(), false)
        var backClicked = false

        // When
        composeTestRule.setContent {
            UserProfileScreen(
                username = "testuser",
                viewModel = mockViewModel,
                onBackClick = { backClicked = true },
                onRepositoryClick = {},
                onUserClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(backClicked)
    }

    @Test
    fun repositoryCard_triggersRepositoryClick() {
        // Given
        val mockUser = User(
            login = "testuser",
            name = "Test User",
            avatarUrl = "https://example.com/avatar.jpg"
        )
        val mockRepositories = listOf(
            Repository(
                id = 1,
                name = "test-repo",
                fullName = "testuser/test-repo",
                description = "Test repository",
                owner = Repository.Owner("testuser", "https://example.com/avatar.jpg"),
                stars = 100,
                forks = 20,
                language = "Kotlin",
                topics = listOf("test")
            )
        )
        mockUserProfile.value = UserViewModel.UserState.Success(mockUser)
        mockUserRepositories.value = UserViewModel.RepositoriesState.Success(mockRepositories, false)
        var repositoryClicked = false

        // When
        composeTestRule.setContent {
            UserProfileScreen(
                username = "testuser",
                viewModel = mockViewModel,
                onBackClick = {},
                onRepositoryClick = { repositoryClicked = true },
                onUserClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("test-repo").performClick()
        assert(repositoryClicked)
    }

    @Test
    fun userAvatar_triggersUserClick() {
        // Given
        val mockUser = User(
            login = "testuser",
            name = "Test User",
            avatarUrl = "https://example.com/avatar.jpg"
        )
        mockUserProfile.value = UserViewModel.UserState.Success(mockUser)
        mockUserRepositories.value = UserViewModel.RepositoriesState.Success(emptyList(), false)
        var userClicked = false

        // When
        composeTestRule.setContent {
            UserProfileScreen(
                username = "testuser",
                viewModel = mockViewModel,
                onBackClick = {},
                onRepositoryClick = {},
                onUserClick = { userClicked = true }
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("User avatar").performClick()
        assert(userClicked)
    }
}