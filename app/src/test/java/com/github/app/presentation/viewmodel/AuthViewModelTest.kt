package com.github.app.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.github.app.data.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: AuthViewModel
    private lateinit var authRepository: AuthRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        viewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `checkAuthStatus should update state to Authenticated when token exists`() = runTest {
        // Given
        val mockToken = "mock_access_token"
        coEvery { authRepository.getAccessToken() } returns mockToken

        // When
        viewModel.checkAuthStatus()

        // Then
        viewModel.authState.test {
            val state = awaitItem()
            assertTrue(state is AuthViewModel.AuthState.Authenticated)
            assertEquals(mockToken, (state as AuthViewModel.AuthState.Authenticated).token)
        }
    }

    @Test
    fun `checkAuthStatus should update state to NotAuthenticated when no token exists`() = runTest {
        // Given
        coEvery { authRepository.getAccessToken() } returns null

        // When
        viewModel.checkAuthStatus()

        // Then
        viewModel.authState.test {
            val state = awaitItem()
            assertTrue(state is AuthViewModel.AuthState.NotAuthenticated)
        }
    }

    @Test
    fun `exchangeCodeForToken should update state to Loading then Authenticated`() = runTest {
        // Given
        val code = "auth_code"
        val mockToken = "mock_access_token"
        coEvery { authRepository.exchangeCodeForToken(code) } returns Result.success(mockToken)

        // When
        viewModel.exchangeCodeForToken(code)

        // Then
        viewModel.authState.test {
            val initialState = awaitItem()
            assertTrue(initialState is AuthViewModel.AuthState.Loading)
            
            val successState = awaitItem()
            assertTrue(successState is AuthViewModel.AuthState.Authenticated)
            assertEquals(mockToken, (successState as AuthViewModel.AuthState.Authenticated).token)
        }
    }

    @Test
    fun `exchangeCodeForToken should update state to Loading then Error when exchange fails`() = runTest {
        // Given
        val code = "auth_code"
        val error = Exception("Failed to exchange code")
        coEvery { authRepository.exchangeCodeForToken(code) } returns Result.failure(error)

        // When
        viewModel.exchangeCodeForToken(code)

        // Then
        viewModel.authState.test {
            val initialState = awaitItem()
            assertTrue(initialState is AuthViewModel.AuthState.Loading)
            
            val errorState = awaitItem()
            assertTrue(errorState is AuthViewModel.AuthState.Error)
            assertEquals(error.message, (errorState as AuthViewModel.AuthState.Error).message)
        }
    }

    @Test
    fun `logout should clear token and update state to NotAuthenticated`() = runTest {
        // Given
        val mockToken = "mock_access_token"
        coEvery { authRepository.getAccessToken() } returns mockToken
        coEvery { authRepository.clearAccessToken() } returns Unit

        // Set initial authenticated state
        viewModel.checkAuthStatus()

        // When
        viewModel.logout()

        // Then
        viewModel.authState.test {
            val state = awaitItem()
            assertTrue(state is AuthViewModel.AuthState.NotAuthenticated)
        }
    }

    @Test
    fun `logout should handle errors when clearing token`() = runTest {
        // Given
        val mockToken = "mock_access_token"
        val error = Exception("Failed to clear token")
        coEvery { authRepository.getAccessToken() } returns mockToken
        coEvery { authRepository.clearAccessToken() } throws error

        // Set initial authenticated state
        viewModel.checkAuthStatus()

        // When
        viewModel.logout()

        // Then
        viewModel.authState.test {
            val state = awaitItem()
            assertTrue(state is AuthViewModel.AuthState.NotAuthenticated)
        }
    }

    @Test
    fun `getAuthUrl should return correct URL`() {
        // When
        val authUrl = viewModel.getAuthUrl()

        // Then
        assertTrue(authUrl.contains("github.com/login/oauth/authorize"))
        assertTrue(authUrl.contains("client_id"))
        assertTrue(authUrl.contains("scope=repo%20user"))
        assertTrue(authUrl.contains("redirect_uri"))
    }

    @Test
    fun `getAuthUrl should include all required parameters`() {
        // When
        val authUrl = viewModel.getAuthUrl()

        // Then
        assertTrue(authUrl.contains("response_type=code"))
        assertTrue(authUrl.contains("scope=repo%20user"))
        assertTrue(authUrl.contains("client_id="))
    }

    @Test
    fun `handleAuthCallback should extract code from valid callback URL`() {
        // Given
        val callbackUrl = "githubapp://callback?code=test_auth_code&state=random_state"

        // When
        val code = viewModel.handleAuthCallback(callbackUrl)

        // Then
        assertEquals("test_auth_code", code)
    }

    @Test
    fun `handleAuthCallback should return null for invalid callback URL`() {
        // Given
        val invalidCallbackUrl = "githubapp://callback?error=access_denied"

        // When
        val code = viewModel.handleAuthCallback(invalidCallbackUrl)

        // Then
        assertNull(code)
    }

    @Test
    fun `handleAuthCallback should return null for URL without code parameter`() {
        // Given
        val invalidCallbackUrl = "githubapp://callback?state=random_state"

        // When
        val code = viewModel.handleAuthCallback(invalidCallbackUrl)

        // Then
        assertNull(code)
    }

    @Test
    fun `handleAuthCallback should return null for non-matching scheme`() {
        // Given
        val invalidCallbackUrl = "https://example.com/callback?code=test_code"

        // When
        val code = viewModel.handleAuthCallback(invalidCallbackUrl)

        // Then
        assertNull(code)
    }

    @Test
    fun `isAuthenticated should return true when token exists`() = runTest {
        // Given
        val mockToken = "mock_access_token"
        coEvery { authRepository.getAccessToken() } returns mockToken

        // When
        val isAuthenticated = viewModel.isAuthenticated()

        // Then
        assertTrue(isAuthenticated)
    }

    @Test
    fun `isAuthenticated should return false when no token exists`() = runTest {
        // Given
        coEvery { authRepository.getAccessToken() } returns null

        // When
        val isAuthenticated = viewModel.isAuthenticated()

        // Then
        assertFalse(isAuthenticated)
    }

    @Test
    fun `getCurrentToken should return token when authenticated`() = runTest {
        // Given
        val mockToken = "mock_access_token"
        coEvery { authRepository.getAccessToken() } returns mockToken

        // When
        val token = viewModel.getCurrentToken()

        // Then
        assertEquals(mockToken, token)
    }

    @Test
    fun `getCurrentToken should return null when not authenticated`() = runTest {
        // Given
        coEvery { authRepository.getAccessToken() } returns null

        // When
        val token = viewModel.getCurrentToken()

        // Then
        assertNull(token)
    }
}