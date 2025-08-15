package com.github.app.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.github.app.data.model.Issue
import com.github.app.data.repository.GitHubRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class IssueViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: IssueViewModel
    private lateinit var gitHubRepository: GitHubRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        gitHubRepository = mockk()
        viewModel = IssueViewModel(gitHubRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadRepositoryIssues should update state to Loading then Success`() = runTest {
        // Given
        val owner = "testowner"
        val repo = "testrepo"
        val mockIssues = listOf(
            Issue(
                id = 1,
                number = 1,
                title = "Test Issue 1",
                body = "This is a test issue",
                state = "open",
                user = Issue.User("testuser", "https://example.com/avatar.png"),
                labels = listOf(Issue.Label("bug", "d73a4a")),
                createdAt = "2024-01-01T00:00:00Z",
                updatedAt = "2024-01-01T00:00:00Z",
                htmlUrl = "https://github.com/testowner/testrepo/issues/1"
            )
        )

        coEvery { gitHubRepository.getRepositoryIssues(owner, repo, 1) } returns Result.success(mockIssues)

        // When
        viewModel.loadRepositoryIssues(owner, repo)

        // Then
        viewModel.issuesState.test {
            val initialState = awaitItem()
            assertTrue(initialState is IssueViewModel.IssuesState.Loading)
            
            val successState = awaitItem()
            assertTrue(successState is IssueViewModel.IssuesState.Success)
            assertEquals(mockIssues, (successState as IssueViewModel.IssuesState.Success).issues)
        }
    }

    @Test
    fun `loadRepositoryIssues should update state to Error when repository fails`() = runTest {
        // Given
        val owner = "testowner"
        val repo = "testrepo"
        val error = Exception("Repository not found")

        coEvery { gitHubRepository.getRepositoryIssues(owner, repo, 1) } returns Result.failure(error)

        // When
        viewModel.loadRepositoryIssues(owner, repo)

        // Then
        viewModel.issuesState.test {
            val initialState = awaitItem()
            assertTrue(initialState is IssueViewModel.IssuesState.Loading)
            
            val errorState = awaitItem()
            assertTrue(errorState is IssueViewModel.IssuesState.Error)
            assertEquals(error.message, (errorState as IssueViewModel.IssuesState.Error).message)
        }
    }

    @Test
    fun `createIssue should update state to Loading then Success`() = runTest {
        // Given
        val owner = "testowner"
        val repo = "testrepo"
        val title = "New Issue"
        val body = "Issue description"
        val labels = listOf("bug", "enhancement")
        val mockIssue = Issue(
            id = 2,
            number = 2,
            title = title,
            body = body,
            state = "open",
            user = Issue.User("currentuser", "https://example.com/avatar.png"),
            labels = labels.map { Issue.Label(it, "d73a4a") },
            createdAt = "2024-01-02T00:00:00Z",
            updatedAt = "2024-01-02T00:00:00Z",
            htmlUrl = "https://github.com/testowner/testrepo/issues/2"
        )

        coEvery { gitHubRepository.createIssue(owner, repo, title, body, labels) } returns Result.success(mockIssue)

        // When
        viewModel.createIssue(owner, repo, title, body, labels)

        // Then
        viewModel.createIssueState.test {
            val initialState = awaitItem()
            assertTrue(initialState is IssueViewModel.CreateIssueState.Loading)
            
            val successState = awaitItem()
            assertTrue(successState is IssueViewModel.CreateIssueState.Success)
            assertEquals(mockIssue, (successState as IssueViewModel.CreateIssueState.Success).issue)
        }
    }

    @Test
    fun `createIssue should update state to Error when repository fails`() = runTest {
        // Given
        val owner = "testowner"
        val repo = "testrepo"
        val title = "New Issue"
        val body = "Issue description"
        val labels = listOf("bug")
        val error = Exception("Failed to create issue")

        coEvery { gitHubRepository.createIssue(owner, repo, title, body, labels) } returns Result.failure(error)

        // When
        viewModel.createIssue(owner, repo, title, body, labels)

        // Then
        viewModel.createIssueState.test {
            val initialState = awaitItem()
            assertTrue(initialState is IssueViewModel.CreateIssueState.Loading)
            
            val errorState = awaitItem()
            assertTrue(errorState is IssueViewModel.CreateIssueState.Error)
            assertEquals(error.message, (errorState as IssueViewModel.CreateIssueState.Error).message)
        }
    }

    @Test
    fun `resetCreateState should reset createIssueState to Idle`() = runTest {
        // Given
        val owner = "testowner"
        val repo = "testrepo"
        val title = "New Issue"
        val body = "Issue description"
        val labels = listOf("bug")
        val mockIssue = Issue(
            id = 2,
            number = 2,
            title = title,
            body = body,
            state = "open",
            user = Issue.User("currentuser", "https://example.com/avatar.png"),
            labels = labels.map { Issue.Label(it, "d73a4a") },
            createdAt = "2024-01-02T00:00:00Z",
            updatedAt = "2024-01-02T00:00:00Z",
            htmlUrl = "https://github.com/testowner/testrepo/issues/2"
        )

        coEvery { gitHubRepository.createIssue(owner, repo, title, body, labels) } returns Result.success(mockIssue)
        viewModel.createIssue(owner, repo, title, body, labels)

        // When
        viewModel.resetCreateState()

        // Then
        assertTrue(viewModel.createIssueState.value is IssueViewModel.CreateIssueState.Idle)
    }

    @Test
    fun `refreshIssues should reload issues`() = runTest {
        // Given
        val owner = "testowner"
        val repo = "testrepo"
        val mockIssues = listOf(
            Issue(
                id = 1,
                number = 1,
                title = "Test Issue 1",
                body = "This is a test issue",
                state = "open",
                user = Issue.User("testuser", "https://example.com/avatar.png"),
                labels = listOf(Issue.Label("bug", "d73a4a")),
                createdAt = "2024-01-01T00:00:00Z",
                updatedAt = "2024-01-01T00:00:00Z",
                htmlUrl = "https://github.com/testowner/testrepo/issues/1"
            )
        )

        coEvery { gitHubRepository.getRepositoryIssues(owner, repo, 1) } returns Result.success(mockIssues)

        // When
        viewModel.refreshIssues(owner, repo)

        // Then
        viewModel.issuesState.test {
            val initialState = awaitItem()
            assertTrue(initialState is IssueViewModel.IssuesState.Loading)