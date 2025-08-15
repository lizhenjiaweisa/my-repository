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
class RepositoryDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: RepositoryViewModel
    private lateinit var mockSelectedRepository: MutableStateFlow<Repository?>

    @Before
    fun setup() {
        mockViewModel = mockk(relaxed = true)
        mockSelectedRepository = MutableStateFlow(null)
        every { mockViewModel.selectedRepository } returns mockSelectedRepository
    }

    @Test
    fun repositoryDetailScreen_displaysLoadingState() {
        // Given
        mockSelectedRepository.value = null

        // When
        composeTestRule.setContent {
            RepositoryDetailScreen(
                viewModel = mockViewModel,
                onBackClick = {},
                onUserClick = {},
                onIssuesClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun repositoryDetailScreen_displaysRepositoryDetails() {
        // Given
        val mockRepository = Repository(
            id = 1,
            name = "awesome-android-app",
            fullName = "developer/awesome-android-app",
            description = "An amazing Android application built with modern technologies",
            owner = Repository.Owner("developer", "https://example.com/avatar.png"),
            stars = 2500,
            forks = 500,
            language = "Kotlin",
            topics = listOf("android", "kotlin", "mvvm", "compose", "clean-architecture"),
            htmlUrl = "https://github.com/developer/awesome-android-app",
            createdAt = "2023-01-15T10:30:00Z",
            updatedAt = "2024-01-20T15:45:00Z",
            size = 2048,
            watchers = 100,
            openIssues = 25,
            defaultBranch = "main"
        )
        mockSelectedRepository.value = mockRepository

        // When
        composeTestRule.setContent {
            RepositoryDetailScreen(
                viewModel = mockViewModel,
                onBackClick = {},
                onUserClick = {},
                onIssuesClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("awesome-android-app").assertIsDisplayed()
        composeTestRule.onNodeWithText("An amazing Android application built with modern technologies").assertIsDisplayed()
        composeTestRule.onNodeWithText("developer").assertIsDisplayed()
        composeTestRule.onNodeWithText("2,500").assertIsDisplayed()
        composeTestRule.onNodeWithText("500").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kotlin").assertIsDisplayed()
    }

    @Test
    fun repositoryDetailScreen_displaysTopics() {
        // Given
        val mockRepository = Repository(
            id = 1,
            name = "kotlin-app",
            fullName = "user/kotlin-app",
            description = "Kotlin application",
            owner = Repository.Owner("user", "https://example.com/avatar.png"),
            stars = 1000,
            forks = 200,
            language = "Kotlin",
            topics = listOf("android", "kotlin", "mvvm", "compose"),
            htmlUrl = "https://github.com/user/kotlin-app"
        )
        mockSelectedRepository.value = mockRepository

        // When
        composeTestRule.setContent {
            RepositoryDetailScreen(
                viewModel = mockViewModel,
                onBackClick = {},
                onUserClick = {},
                onIssuesClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("android").assertIsDisplayed()
        composeTestRule.onNodeWithText("kotlin").assertIsDisplayed()
        composeTestRule.onNodeWithText("mvvm").assertIsDisplayed()
        composeTestRule.onNodeWithText("compose").assertIsDisplayed()
    }

    @Test
    fun backButton_triggersBackAction() {
        // Given
        val mockRepository = Repository(
            id = 1,
            name = "test-repo",
            fullName = "user/test-repo",
            description = "Test repository",
            owner = Repository.Owner("user", "https://example.com/avatar.png"),
            stars = 100,
            forks = 20,
            language = "Kotlin",
            topics = listOf("test"),
            htmlUrl = "https://github.com/user/test-repo"
        )
        mockSelectedRepository.value = mockRepository
        var backClicked = false

        // When
        composeTestRule.setContent {
            RepositoryDetailScreen(
                viewModel = mockViewModel,
                onBackClick = { backClicked = true },
                onUserClick = {},
                onIssuesClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(backClicked)
    }

    @Test
    fun userAvatar_triggersUserClickAction() {
        // Given
        val mockRepository = Repository(
            id = 1,
            name = "test-repo",
            fullName = "user/test-repo",
            description = "Test repository",
            owner = Repository.Owner("user", "https://example.com/avatar.png"),
            stars = 100,
            forks = 20,
            language = "Kotlin",
            topics = listOf("test"),
            htmlUrl = "https://github.com/user/test-repo"
        )
        mockSelectedRepository.value = mockRepository
        var userClicked = false

        // When
        composeTestRule.setContent {
            RepositoryDetailScreen(
                viewModel = mockViewModel,
                onBackClick = {},
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
        val mockRepository = Repository(
            id = 1,
            name = "test-repo",
            fullName = "user/test-repo",
            description = "Test repository",
            owner = Repository.Owner("user", "https://example.com/avatar.png"),
            stars = 100,
            forks = 20,
            language = "Kotlin",
            topics = listOf("test"),
            htmlUrl = "https://github.com/user/test-repo"
        )
        mockSelectedRepository.value = mockRepository
        var issuesClicked = false

        // When
        composeTestRule.setContent {
            RepositoryDetailScreen(
                viewModel = mockViewModel,
                onBackClick = {},
                onUserClick = {},
                onIssuesClick = { issuesClicked = true }
            )
        }

        // Then
        composeTestRule.onNodeWithText("25 open issues").performClick()
        assert(issuesClicked)
    }

    @Test
    fun repositoryDetailScreen_displaysStatsCorrectly() {
        // Given
        val mockRepository = Repository(
            id = 1,
            name = "popular-repo",
            fullName = "user/popular-repo",
            description = "A very popular repository",
            owner = Repository.Owner("user", "https://example.com/avatar.png"),
            stars = 15000,
            forks = 3500,
            language = "JavaScript",
            topics = listOf("javascript", "nodejs", "web"),
            htmlUrl = "https://github.com/user/popular-repo",
            openIssues = 150
        )
        mockSelectedRepository.value = mockRepository

        // When
        composeTestRule.setContent {
            RepositoryDetailScreen(
                viewModel = mockViewModel,
                onBackClick = {},
                onUserClick = {},
                onIssuesClick = {}
            )