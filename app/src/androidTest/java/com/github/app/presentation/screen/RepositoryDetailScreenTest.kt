package com.github.app.presentation.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.app.data.model.Owner
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

    private lateinit var mockOwner: Owner

    @Before
    fun setup() {
        mockViewModel = mockk(relaxed = true)
        mockOwner = mockk(relaxed = true)
        mockSelectedRepository = MutableStateFlow(null)
        every { mockViewModel.selectedRepository } returns mockSelectedRepository
    }

    @Test
    fun repositoryDetailScreen_displaysLoadingState() {
        // Given
        val owner = "testowner"
        val repoName = "testrepo"
        mockSelectedRepository.value = null

        // When
        composeTestRule.setContent {
            RepositoryDetailScreen(
                owner = owner,
                repoName = repoName,
                viewModel = mockViewModel,
                onBackClick = {},
                onUserClick = {},
                onIssuesClick = { _, _ -> }
            )
        }

        // Then
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun repositoryDetailScreen_displaysRepositoryDetails() {
        // Given
        val owner = "developer"
        val repoName = "awesome-android-app"
        val mockRepository = Repository(
            id = 1,
            name = "awesome-android-app",
            fullName = "developer/awesome-android-app",
            description = "An amazing Android application built with modern technologies",
            stars = 2500,
            forks = 500,
            language = "Kotlin",
            topics = listOf("android", "kotlin", "mvvm", "compose", "clean-architecture"),
            htmlUrl = "https://github.com/developer/awesome-android-app",
            updatedAt = "2024-01-20T15:45:00Z",
            size = 2048,
            openIssues = 25,
            owner = mockOwner,
            private = false,
            fork = false,
            defaultBranch = "main"
        )
        mockSelectedRepository.value = mockRepository

        // When
        composeTestRule.setContent {
            RepositoryDetailScreen(
                owner = owner,
                repoName = repoName,
                viewModel = mockViewModel,
                onBackClick = {},
                onUserClick = {},
                onIssuesClick = { _, _ -> }
            )
        }

        // Then
        composeTestRule.onNodeWithText("awesome-android-app").assertIsDisplayed()
        composeTestRule.onNodeWithText("An amazing Android application built with modern technologies")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("developer").assertIsDisplayed()
        composeTestRule.onNodeWithText("2,500").assertIsDisplayed()
        composeTestRule.onNodeWithText("500").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kotlin").assertIsDisplayed()
    }

    @Test
    fun repositoryDetailScreen_displaysTopics() {
        // Given
        val owner = "user"
        val repoName = "kotlin-app"
        val mockRepository = Repository(
            id = 1,
            name = "kotlin-app",
            fullName = "user/kotlin-app",
            description = "Kotlin application",
            owner = Owner("user", 1, "", "https://example.com/avatar.png"),
            stars = 1000,
            forks = 200,
            language = "Kotlin",
            topics = listOf("android", "kotlin", "mvvm", "compose"),
            htmlUrl = "https://github.com/user/kotlin-app",
            updatedAt ="",
            private =false,
            fork = false,
            size = 10,
            openIssues = 10,
            defaultBranch = "main"
        )
        mockSelectedRepository.value = mockRepository

        // When
        composeTestRule.setContent {
            RepositoryDetailScreen(
                owner = owner,
                repoName = repoName,
                viewModel = mockViewModel,
                onBackClick = {},
                onUserClick = {},
                onIssuesClick = { _, _ -> }
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
        val owner = "user"
        val repoName = "test-repo"
        val mockRepository = Repository(
            id = 1,
            name = "test-repo",
            fullName = "user/test-repo",
            description = "Test repository",
            owner = Owner("user", 12, "", "https://example.com/avatar.png"),
            stars = 100,
            forks = 20,
            language = "Kotlin",
            topics = listOf("test"),
            htmlUrl = "https://github.com/user/test-repo",
            updatedAt ="",
            private =false,
            fork = false,
            size = 10,
            openIssues = 10,
            defaultBranch = "main"
        )
        mockSelectedRepository.value = mockRepository
        var backClicked = false

        // When
        composeTestRule.setContent {
            RepositoryDetailScreen(
                owner = owner,
                repoName = repoName,
                viewModel = mockViewModel,
                onBackClick = { backClicked = true },
                onUserClick = {},
                onIssuesClick = { _, _ -> }
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(backClicked)
    }

    @Test
    fun userAvatar_triggersUserClickAction() {
        // Given
        val owner = "user"
        val repoName = "test-repo"
        val mockRepository = Repository(
            id = 1,
            name = "test-repo",
            fullName = "user/test-repo",
            description = "Test repository",
            owner = Owner("user", 123,"","https://example.com/avatar.png"),
            stars = 100,
            forks = 20,
            language = "Kotlin",
            topics = listOf("test"),
            htmlUrl = "https://github.com/user/test-repo",
            updatedAt ="",
            private =false,
            fork = false,
            size = 10,
            openIssues = 10,
            defaultBranch = "main"
        )
        mockSelectedRepository.value = mockRepository
        var userClicked = false

        // When
        composeTestRule.setContent {
            RepositoryDetailScreen(
                owner = owner,
                repoName = repoName,
                viewModel = mockViewModel,
                onBackClick = {},
                onUserClick = { userClicked = true },
                onIssuesClick = { _, _ -> }
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("User avatar").performClick()
        assert(userClicked)
    }

    @Test
    fun issuesButton_triggersIssuesClickAction() {
        // Given
        val owner = "user"
        val repoName = "test-repo"
        val mockRepository = Repository(
            id = 1,
            name = "test-repo",
            fullName = "user/test-repo",
            description = "Test repository",
            owner = Owner("user", 1234,"https://example.com/avatar.png",""),
            stars = 100,
            forks = 20,
            language = "Kotlin",
            topics = listOf("test"),
            htmlUrl = "https://github.com/user/test-repo",
            updatedAt ="",
            private =false,
            fork = false,
            size = 10,
            openIssues = 10,
            defaultBranch = "main"
        )
        mockSelectedRepository.value = mockRepository
        var issuesClicked = false

        // When
        composeTestRule.setContent {
            RepositoryDetailScreen(
                owner = owner,
                repoName = repoName,
                viewModel = mockViewModel,
                onBackClick = {},
                onUserClick = {},
                onIssuesClick = { _, _ -> issuesClicked = true }
            )
        }

        // Then
        composeTestRule.onNodeWithText("25 open issues").performClick()
        assert(issuesClicked)
    }

    @Test
    fun repositoryDetailScreen_displaysStatsCorrectly() {
        // Given
        val owner = "user"
        val repoName = "popular-repo"
        val mockRepository = Repository(
            id = 1,
            name = "popular-repo",
            fullName = "user/popular-repo",
            description = "A very popular repository",
            owner = Owner("user", 21,"https://example.com/avatar.png",""),
            stars = 15000,
            forks = 3500,
            language = "JavaScript",
            topics = listOf("javascript", "nodejs", "web"),
            htmlUrl = "https://github.com/user/popular-repo",
            updatedAt ="",
            private =false,
            fork = false,
            size = 10,
            openIssues = 150,
            defaultBranch = "main"
        )
        mockSelectedRepository.value = mockRepository

        // When
        composeTestRule.setContent {
            RepositoryDetailScreen(
                owner = owner,
                repoName = repoName,
                viewModel = mockViewModel,
                onBackClick = {},
                onUserClick = {},
                onIssuesClick = { _, _ -> }
            )
        }

        // Then
        composeTestRule.onNodeWithText("15,000").assertIsDisplayed()
        composeTestRule.onNodeWithText("3,500").assertIsDisplayed()
        composeTestRule.onNodeWithText("150").assertIsDisplayed()
    }
}