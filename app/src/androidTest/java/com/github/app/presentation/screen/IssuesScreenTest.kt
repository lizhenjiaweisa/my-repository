package com.github.app.presentation.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.app.data.model.Issue
import com.github.app.data.model.Repository
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
    private lateinit var mockIssues: MutableStateFlow<IssueViewModel.IssuesState>
    private lateinit var mockCreateState: MutableStateFlow<IssueViewModel.CreateIssueState>

    @Before
    fun setup() {
        mockViewModel = mockk(relaxed = true)
        mockIssues = MutableStateFlow(IssueViewModel.IssuesState.Loading)
        mockCreateState = MutableStateFlow(IssueViewModel.CreateIssueState.Idle)
        every { mockViewModel.issues } returns mockIssues
        every { mockViewModel.createIssueState } returns mockCreateState
    }

    @Test
    fun issuesScreen_displaysLoadingState() {
        // Given
        mockIssues.value = IssueViewModel.IssuesState.Loading

        // When
        composeTestRule.setContent {
            IssuesScreen(
                repository = Repository(
                    id = 1,
                    name = "test-repo",
                    fullName = "user/test-repo",
                    owner = Repository.Owner("user", "https://example.com/avatar.png")
                ),
                viewModel = mockViewModel,
                onBackClick = {},
                onCreateIssue = {}
            )
        }

        // Then
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun issuesScreen_displaysEmptyState() {
        // Given
        mockIssues.value = IssueViewModel.IssuesState.Success(emptyList())

        // When
        composeTestRule.setContent {
            IssuesScreen(
                repository = Repository(
                    id = 1,
                    name = "test-repo",
                    fullName = "user/test-repo",
                    owner = Repository.Owner("user", "https://example.com/avatar.png")
                ),
                viewModel = mockViewModel,
                onBackClick = {},
                onCreateIssue = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("No issues found").assertIsDisplayed()
        composeTestRule.onNodeWithText("This repository has no open issues").assertIsDisplayed()
    }

    @Test
    fun issuesScreen_displaysIssuesList() {
        // Given
        val mockIssuesList = listOf(
            Issue(
                id = 1,
                title = "Bug: App crashes on startup",
                body = "The app crashes when opened on Android 14",
                state = "open",
                user = Issue.User("bugreporter", "https://example.com/avatar1.png"),
                createdAt = "2024-01-15T10:30:00Z",
                updatedAt = "2024-01-15T10:30:00Z",
                htmlUrl = "https://github.com/user/test-repo/issues/1"
            ),
            Issue(
                id = 2,
                title = "Feature: Add dark mode support",
                body = "Please add dark mode to the application",
                state = "open",
                user = Issue.User("featurerequest", "https://example.com/avatar2.png"),
                createdAt = "2024-01-14T15:45:00Z",
                updatedAt = "2024-01-14T15:45:00Z",
                htmlUrl = "https://github.com/user/test-repo/issues/2"
            )
        )
        mockIssues.value = IssueViewModel.IssuesState.Success(mockIssuesList)

        // When
        composeTestRule.setContent {
            IssuesScreen(
                repository = Repository(
                    id = 1,
                    name = "test-repo",
                    fullName = "user/test-repo",
                    owner = Repository.Owner("user", "https://example.com/avatar.png")
                ),
                viewModel = mockViewModel,
                onBackClick = {},
                onCreateIssue = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Bug: App crashes on startup").assertIsDisplayed()
        composeTestRule.onNodeWithText("The app crashes when opened on Android 14").assertIsDisplayed()
        composeTestRule.onNodeWithText("bugreporter").assertIsDisplayed()
        composeTestRule.onNodeWithText("Feature: Add dark mode support").assertIsDisplayed()
        composeTestRule.onNodeWithText("Please add dark mode to the application").assertIsDisplayed()
        composeTestRule.onNodeWithText("featurerequest").assertIsDisplayed()
    }

    @Test
    fun issuesScreen_displaysErrorState() {
        // Given
        val errorMessage = "Failed to load issues"
        mockIssues.value = IssueViewModel.IssuesState.Error(errorMessage)

        // When
        composeTestRule.setContent {
            IssuesScreen(
                repository = Repository(
                    id = 1,
                    name = "test-repo",
                    fullName = "user/test-repo",
                    owner = Repository.Owner("user", "https://example.com/avatar.png")
                ),
                viewModel = mockViewModel,
                onBackClick = {},
                onCreateIssue = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun backButton_triggersBackAction() {
        // Given
        mockIssues.value = IssueViewModel.IssuesState.Success(emptyList())
        var backClicked = false

        // When
        composeTestRule.setContent {
            IssuesScreen(
                repository = Repository(
                    id = 1,
                    name = "test-repo",
                    fullName = "user/test-repo",
                    owner = Repository.Owner("user", "https://example.com/avatar.png")
                ),
                viewModel = mockViewModel,
                onBackClick = { backClicked = true },
                onCreateIssue = {}
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(backClicked)
    }

    @Test
    fun createIssueButton_triggersCreateIssue() {
        // Given
        mockIssues.value = IssueViewModel.IssuesState.Success(emptyList())
        var createIssueTriggered = false

        // When
        composeTestRule.setContent {
            IssuesScreen(
                repository = Repository(
                    id = 1,
                    name = "test-repo",
                    fullName = "user/test-repo",
                    owner = Repository.Owner("user", "https://example.com/avatar.png")
                ),
                viewModel = mockViewModel,
                onBackClick = {},
                onCreateIssue = { createIssueTriggered = true }
            )
        }

        // Then
        composeTestRule.onNodeWithText("Create Issue").performClick()
        assert(createIssueTriggered)
    }

    @Test
    fun createIssueScreen_displaysFormFields() {
        // Given
        val mockRepository = Repository(
            id = 1,
            name = "test-repo",
            fullName = "user/test-repo",
            owner = Repository.Owner("user", "https://example.com/avatar.png")
        )

        // When
        composeTestRule.setContent {
            CreateIssueScreen(
                repository = mockRepository,
                viewModel = mockViewModel,
                onBackClick = {},
                onIssueCreated = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Create New Issue").assertIsDisplayed()
        composeTestRule.onNodeWithText("Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Issue title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Description").assertIsDisplayed()
        composeTestRule