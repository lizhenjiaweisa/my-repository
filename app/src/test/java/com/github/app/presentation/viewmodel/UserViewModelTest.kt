package com.github.app.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.app.data.repository.GitHubRepository
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
    fun `clearUserData should reset user states to Idle`() {
        // When
        viewModel.clearUserData()

        // Then
        assertTrue(viewModel.userProfile.value is UserState.Idle)
        assertTrue(viewModel.userRepositories.value is UserRepositoriesState.Idle)
    }
}