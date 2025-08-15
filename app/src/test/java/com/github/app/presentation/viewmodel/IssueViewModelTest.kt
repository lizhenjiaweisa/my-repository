package com.github.app.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
    fun `resetCreateIssueState should update createIssueState to Idle`() {
        // When
        viewModel.resetCreateIssueState()

        // Then
        assertTrue(viewModel.createIssueState.value is CreateIssueState.Idle)
    }
}