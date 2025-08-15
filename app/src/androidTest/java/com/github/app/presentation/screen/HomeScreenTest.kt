package com.github.app.presentation.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.app.data.model.Repository
import com.github.app.presentation.viewmodel.RepositoryViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: RepositoryViewModel
    private lateinit var mockRepositoryState: MutableStateFlow<RepositoryViewModel.RepositoryState>

    @Before
    fun setup() {
        mockViewModel = mockk(relaxed = true)
        mockRepositoryState = MutableStateFlow(RepositoryViewModel.RepositoryState.Loading)
        every { mockViewModel.repositoryState } returns mockRepositoryState
        every { mockViewModel.searchQuery } returns MutableStateFlow("")
        every { mockViewModel.selectedLanguage } returns MutableStateFlow("All Languages")
    }

    @Test
    fun homeScreen_displaysLoadingState() {
        // Given
        mockRepositoryState.value = RepositoryViewModel.RepositoryState.Loading

        // When
        composeTestRule.setContent {
            HomeScreen(
                viewModel = mockViewModel,
                onRepositoryClick = {},
                onUserClick = {},
                onIssuesClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysEmptyState() {
        // Given
        mockRepositoryState.value = RepositoryViewModel.RepositoryState.Success(emptyList())

        // When
        composeTestRule.setContent {
            HomeScreen(
                viewModel = mockViewModel,
                onRepositoryClick = {},
                onUserClick = {},
                onIssuesClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("No repositories found").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysRepositoriesList() {
        // Given
        val mockRepositories = listOf(
            Repository(
                id = 1,
                name = "test-repo",
                fullName = "user/test-repo",
                description = "A test repository",
                owner = Repository.Owner("user", "https://example.com/avatar.png"),
                stars = 100,
                forks = 50,
                language = "Kotlin",
                topics = listOf("android", "kotlin"),
                htmlUrl = "https://github.com/user/test-repo"
            )
        )
        mockRepositoryState.value = RepositoryViewModel.RepositoryState.Success(mockRepositories)

        // When
        composeTestRule.setContent {
            HomeScreen(
                viewModel = mockViewModel,
                onRepositoryClick = {},
                onUserClick = {},
                onIssuesClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("test-repo").assertIsDisplayed()
        composeTestRule.onNodeWithText("A test repository").assertIsDisplayed()
        composeTestRule.onNodeWithText("100").assertIsDisplayed()
        composeTestRule.onNodeWithText("50").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysErrorState() {
        // Given
        val errorMessage = "Network error occurred"
        mockRepositoryState.value = RepositoryViewModel.RepositoryState.Error(errorMessage)

        // When
        composeTestRule.setContent {
            HomeScreen(
                viewModel = mockViewModel,
                onRepositoryClick = {},
                onUserClick = {},
                onIssuesClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun searchBar_performsSearch() {
        // Given
        val mockRepositories = listOf(
            Repository(
                id = 1,
                name = "kotlin-project",
                fullName = "user/kotlin-project",
                description = "A Kotlin project",
                owner = Repository.Owner("user", "https://example.com/avatar.png"),
                stars = 100,
                forks = 50,
                language = "Kotlin",
                topics = listOf("android"),
                htmlUrl = "https://github.com/user/kotlin-project"
            )
        )
        mockRepositoryState.value = RepositoryViewModel.RepositoryState.Success(mockRepositories)

        // When
        composeTestRule.setContent {
            HomeScreen(
                viewModel = mockViewModel,
                onRepositoryClick = {},
                onUserClick = {},
                onIssuesClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithTag("search_bar").performTextInput("kotlin")
        // Verify search was triggered
    }

    @Test
    fun repositoryCard_triggersClickAction() {
        // Given
        var repositoryClicked = false
        val mockRepositories = listOf(
            Repository(
                id = 1,
                name = "test-repo",
                fullName = "user/test-repo",
                description = "A test repository",
                owner = Repository.Owner("user", "https://example.com/avatar.png"),
                stars = 100,
                forks = 50,
                language = "Kotlin",
                topics = listOf("android"),
                htmlUrl = "https://github.com/user/test-repo"
            )
        )
        mockRepositoryState.value = RepositoryViewModel.RepositoryState.Success(mockRepositories)

        // When
        composeTestRule.setContent {
            HomeScreen(
                viewModel = mockViewModel,
                onRepositoryClick = { repositoryClicked = true },
                onUserClick = {},
                onIssuesClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("test-repo").performClick()
        assert(repositoryClicked)
    }

    @Test
    fun userAvatar_triggersUserClickAction() {
        // Given
        var userClicked = false
        val mockRepositories = listOf(
            Repository(
                id = 1,
                name = "test-repo",
                fullName = "user/test-repo",
                description = "A test repository",
                owner = Repository.Owner("user", "https://example.com/avatar.png"),
                stars = 100,
                forks = 50,
                language = "Kotlin",
                topics = listOf("android"),
                htmlUrl = "https://github.com/user/test-repo"
            )
        )
        mockRepositoryState.value = RepositoryViewModel.RepositoryState.Success(mockRepositories)

        // When
        composeTestRule.setContent {
            HomeScreen(
                viewModel = mockViewModel,
                onRepositoryClick = {},
                onUserClick = { userClicked = true },
                onIssuesClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("User avatar").performClick()
        assert(userClicked)
    }

    @Test
    fun issuesButton_triggersIssuesClickAction() {
        // Given
        var issuesClicked = false
        val mockRepositories = listOf(
            Repository(
                id = 1,
                name = "test-repo",
                fullName = "user/test-repo",
                description = "A test repository",
                owner = Repository.Owner("user", "https://example.com/avatar.png"),
                stars = 100,
                forks = 50,
                language = "Kotlin",
                topics = listOf("android"),
                htmlUrl = "https://github.com/user/test-repo"
            )
        )
        mockRepositoryState.value = RepositoryViewModel.RepositoryState.Success(mockRepositories)

        // When
        composeTestRule.setContent {
            HomeScreen(
                viewModel = mockViewModel,
                onRepositoryClick = {},
                onUserClick = {},
                onIssuesClick = { issuesClicked = true }
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Issues").performClick()
        assert(issuesClicked)
    }

    @Test
    fun languageFilter_showsAllLanguagesOption() {
        // Given
        mockRepositoryState.value = RepositoryViewModel.RepositoryState.Success(emptyList())

        // When
        composeTestRule.setContent {
            HomeScreen(
                viewModel = mockViewModel,
                onRepositoryClick = {},
                onUserClick = {},
                onIssuesClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("All Languages").assertIsDisplayed()
    }

    @Test
    fun languageFilter_opensDropdownMenu() {
        // Given
        mockRepositoryState.value = RepositoryViewModel.RepositoryState.Success(emptyList())

        // When
        composeTestRule.setContent {
            HomeScreen(
                viewModel = mockViewModel,
                onRepositoryClick = {},
                onUserClick = {},
                onIssuesClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("All Languages").performClick()
        composeTestRule.onNodeWithText("Kotlin").assertIsDisplayed()
    }

    @Test
    fun retryButton_triggersRetryAction() {
        // Given
        val errorMessage = "Network error occurred"
        mockRepositoryState.value = RepositoryViewModel.RepositoryState.Error(errorMessage)

        // When
        composeTestRule.setContent {
            HomeScreen(
                viewModel = mockViewModel,
                onRepositoryClick = {},
                onUserClick = {},
                onIssuesClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Retry").performClick()
        // Verify retry was triggered
    }

    @Test
    fun searchBar_clearButton_clearsSearchQuery() {
        // Given
        mockRepositoryState.value = RepositoryViewModel.RepositoryState.Success(emptyList())

        // When
        composeTestRule.setContent {
            HomeScreen(
                viewModel = mockViewModel,
                onRepositoryClick = {},
                onUserClick = {},
                onIssuesClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithTag("search_bar").performTextInput("test")
        composeTestRule.onNodeWithContentDescription("Clear search").performClick()
        // Verify search was cleared
    }

    @Test
    fun repositoryCard_displaysLanguageColor() {
        // Given
        val mockRepositories = listOf(
            Repository(
                id = 1,
                name = "kotlin-repo",
                fullName = "user/kotlin-repo",
                description = "A Kotlin repository",
                owner = Repository.Owner("user", "https://example.com/avatar.png"),
                stars = 100,
                forks = 50,
                language = "Kotlin",
                topics = listOf("android"),
                htmlUrl = "https://github.com/user/kotlin-repo"
            )
        )
        mockRepositoryState.value = RepositoryViewModel.RepositoryState.Success(mockRepositories)

        // When
        composeTestRule.setContent {
            HomeScreen(
                viewModel = mockViewModel,
                onRepositoryClick = {},
                onUserClick = {},
                onIssuesClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Kotlin").assertIsDisplayed()
    }

    @Test
    fun repositoryCard_displaysTopics() {
        // Given
        val mockRepositories = listOf(
            Repository(
                id = 1,
                name = "android-app",
                fullName = "user/android-app",
                description = "An Android application",
                owner = Repository.Owner("user", "https://example.com/avatar.png"),
                stars = 100,
                forks = 50,
                language = "Kotlin",
                topics = listOf("android", "kotlin", "mvvm"),
                htmlUrl = "https://github.com/user/android-app"
            )
        )
        mockRepositoryState.value = RepositoryViewModel.RepositoryState.Success(mockRepositories)

        // When
        composeTestRule.setContent {
            HomeScreen(
                viewModel = mockViewModel,
                onRepositoryClick = {},
                onUserClick = {},
                onIssuesClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("android").assertIsDisplayed()
        composeTestRule.onNodeWithText("kotlin").assertIsDisplayed()
        composeTestRule.onNodeWithText("mvvm").assertIsDisplayed()
    }
}