package com.github.app.presentation.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.app.data.model.Issue
import com.github.app.data.model.Owner
import com.github.app.data.model.User
import com.github.app.presentation.viewmodel.IssueViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IssuesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: IssueViewModel
    private lateinit var mockIssues: MutableStateFlow<List<Issue>>
    private lateinit var mockIsLoading: MutableStateFlow<Boolean>
    private lateinit var mockError: MutableStateFlow<String?>

    @Before
    fun setup() {
        mockViewModel = mockk(relaxed = true)
        mockIssues = MutableStateFlow(emptyList())
        mockIsLoading = MutableStateFlow(false)
        mockError = MutableStateFlow(null)
        
        every { mockViewModel.issues } returns mockIssues
        every { mockViewModel.isLoading } returns mockIsLoading
        every { mockViewModel.error } returns mockError
    }

    @Test
    fun issuesScreen_displaysLoadingState() {
        // Given
        val owner = "testowner"
        val repoName = "testrepo"
        mockIsLoading.value = true
        mockIssues.value = emptyList()

        // When
        composeTestRule.setContent {
            IssuesScreen(
                owner = owner,
                repoName = repoName,
                viewModel = mockViewModel,
                onBackClick = {},
                onIssueClick = {},
                onCreateIssueClick = { _, _ -> }
            )
        }

        // Then
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun issuesScreen_displaysEmptyState() {
        // Given
        val owner = "testowner"
        val repoName = "testrepo"
        mockIsLoading.value = false
        mockIssues.value = emptyList()

        // When
        composeTestRule.setContent {
            IssuesScreen(
                owner = owner,
                repoName = repoName,
                viewModel = mockViewModel,
                onBackClick = {},
                onIssueClick = {},
                onCreateIssueClick = { _, _ -> }
            )
        }

        // Then
        composeTestRule.onNodeWithText("No issues found").assertIsDisplayed()
        composeTestRule.onNodeWithText("This repository doesn't have any open issues.").assertIsDisplayed()
    }

    @Test
    fun issuesScreen_displaysIssuesList() {
        // Given
        val owner = "testowner"
        val repoName = "testrepo"
        val mockIssuesList = listOf(
            Issue(
                id = 1,
                title = "Bug: App crashes on startup",
                body = "The app crashes immediately when launched on Android 12",
                state = "open",
                user = Issue.User("bugreporter", "https://example.com/avatar1.png"),
                createdAt = "2024-01-15T10:30:00Z",
                updatedAt = "2024-01-15T10:30:00Z",
                htmlUrl = "https://github.com/testowner/testrepo/issues/1"
            ),
            Issue(
                id = 2,
                title = "Feature: Add dark mode support",
                body = "Please add dark mode support for better user experience",
                state = "open",
                user = Issue.User("featureuser", "https://example.com/avatar2.png"),
                createdAt = "2024-01-14T15:45:00Z",
                updatedAt = "2024-01-14T15:45:00Z",
                htmlUrl = "https://github.com/testowner/testrepo/issues/2"
            )
        )
        mockIsLoading.value = false
        mockIssues.value = mockIssuesList

        // When
        composeTestRule.setContent {
            IssuesScreen(
                owner = owner,
                repoName = repoName,
                viewModel = mockViewModel,
                onBackClick = {},
                onIssueClick = {},
                onCreateIssueClick = { _, _ -> }
            )
        }

        // Then
        composeTestRule.onNodeWithText("Bug: App crashes on startup").assertIsDisplayed()
        composeTestRule.onNodeWithText("The app crashes immediately when launched on Android 12").assertIsDisplayed()
        composeTestRule.onNodeWithText("bugreporter").assertIsDisplayed()
        composeTestRule.onNodeWithText("Feature: Add dark mode support").assertIsDisplayed()
        composeTestRule.onNodeWithText("featureuser").assertIsDisplayed()
    }

    @Test
    fun issuesScreen_displaysErrorState() {
        // Given
        val owner = "testowner"
        val repoName = "testrepo"
        mockIsLoading.value = false
        mockIssues.value = emptyList()
        mockError.value = "Failed to load issues: Network error"

        // When
        composeTestRule.setContent {
            IssuesScreen(
                owner = owner,
                repoName = repoName,
                viewModel = mockViewModel,
                onBackClick = {},
                onIssueClick = {},
                onCreateIssueClick = { _, _ -> }
            )
        }

        // Then
        composeTestRule.onNodeWithText("Failed to load issues: Network error").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun backButton_triggersBackAction() {
        // Given
        val owner = "testowner"
        val repoName = "testrepo"
        mockIsLoading.value = false
        mockIssues.value = emptyList()
        var backClicked = false

        // When
        composeTestRule.setContent {
            IssuesScreen(
                owner = owner,
                repoName = repoName,
                viewModel = mockViewModel,
                onBackClick = { backClicked = true },
                onIssueClick = {},
                onCreateIssueClick = { _, _ -> }
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(backClicked)
    }

    @Test
    fun issueItem_triggersIssueClickAction() {
        // Given
        val owner = "testowner"
        val repoName = "testrepo"
        val mockIssuesList = listOf(
            Issue(
                id = 1,
                title = "Test Issue",
                body = "This is a test issue",
                state = "open",
                user = Owner("testuser", 1231, "https://example.com/avatar.png", ""),
                createdAt = "2024-01-15T10:30:00Z",
                updatedAt = "2024-01-15T10:30:00Z",
                number = 10,
                assignee = Owner("testuser", 1231, "https://example.com/avatar.png", ""),
                assignees =  emptyList(),
                labels =  emptyList(),
                closedAt ="",
                commentsCount = 100
            )
        )
        mockIsLoading.value = false
        mockIssues.value = mockIssuesList
        var issueClicked = false

        // When
        composeTestRule.setContent {
            IssuesScreen(
                owner = owner,
                repoName = repoName,
                viewModel = mockViewModel,
                onBackClick = {},
                onIssueClick = { issueClicked = true },
                onCreateIssueClick = { _, _ -> }
            )
        }

        // Then
        composeTestRule.onNodeWithText("Test Issue").performClick()
        assert(issueClicked)
    }

    @Test
    fun createIssueButton_triggersCreateIssueAction() {
        // Given
        val owner = "testowner"
        val repoName = "testrepo"
        mockIsLoading.value = false
        mockIssues.value = emptyList()
        var createIssueClicked = false

        // When
        composeTestRule.setContent {
            IssuesScreen(
                owner = owner,
                repoName = repoName,
                viewModel = mockViewModel,
                onBackClick = {},
                onIssueClick = {},
                onCreateIssueClick = { _, _ -> createIssueClicked = true }
            )
        }

        // Then
        composeTestRule.onNodeWithText("Create Issue").performClick()
        assert(createIssueClicked)
    }

    @Test
    fun createIssueScreen_displaysFormFields() {
        // Given
        val owner = "testowner"
        val repoName = "testrepo"

        // When
        composeTestRule.setContent {
            CreateIssueScreen(
                owner = owner,
                repoName = repoName,
                onBackClick = {},
                onIssueCreated = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Create Issue").assertIsDisplayed()
        composeTestRule.onNodeWithText("Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Description").assertIsDisplayed()
        composeTestRule.onNodeWithText("Labels (comma-separated)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create").assertIsDisplayed()
    }
}