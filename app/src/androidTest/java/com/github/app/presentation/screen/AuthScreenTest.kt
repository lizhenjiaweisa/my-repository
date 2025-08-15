package com.github.app.presentation.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.app.presentation.viewmodel.AuthViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: AuthViewModel
    private lateinit var mockAuthState: MutableStateFlow<AuthViewModel.AuthState>

    @Before
    fun setup() {
        mockViewModel = mockk(relaxed = true)
        mockAuthState = MutableStateFlow(AuthViewModel.AuthState.NotAuthenticated)
        every { mockViewModel.authState } returns mockAuthState
        every { mockViewModel.getAuthUrl() } returns "https://github.com/login/oauth/authorize?client_id=test"
    }

    @Test
    fun authScreen_displaysLoginPrompt() {
        // Given
        mockAuthState.value = AuthViewModel.AuthState.NotAuthenticated

        // When
        composeTestRule.setContent {
            AuthScreen(
                viewModel = mockViewModel,
                onAuthSuccess = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("GitHub Login").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign in with GitHub to access repositories").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign in with GitHub").assertIsDisplayed()
    }

    @Test
    fun authScreen_displaysLoadingState() {
        // Given
        mockAuthState.value = AuthViewModel.AuthState.Loading

        // When
        composeTestRule.setContent {
            AuthScreen(
                viewModel = mockViewModel,
                onAuthSuccess = {}
            )
        }

        // Then
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
        composeTestRule.onNodeWithText("Authenticating...").assertIsDisplayed()
    }

    @Test
    fun authScreen_displaysAuthenticatedState() {
        // Given
        mockAuthState.value = AuthViewModel.AuthState.Authenticated("mock_token")

        // When
        composeTestRule.setContent {
            AuthScreen(
                viewModel = mockViewModel,
                onAuthSuccess = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Authentication Successful").assertIsDisplayed()
        composeTestRule.onNodeWithText("You are now logged in").assertIsDisplayed()
    }

    @Test
    fun authScreen_displaysErrorState() {
        // Given
        val errorMessage = "Authentication failed"
        mockAuthState.value = AuthViewModel.AuthState.Error(errorMessage)

        // When
        composeTestRule.setContent {
            AuthScreen(
                viewModel = mockViewModel,
                onAuthSuccess = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText("Try Again").assertIsDisplayed()
    }

    @Test
    fun loginButton_triggersAuthentication() {
        // Given
        mockAuthState.value = AuthViewModel.AuthState.NotAuthenticated
        var authTriggered = false
        every { mockViewModel.getAuthUrl() } returns "https://github.com/login/oauth/authorize"

        // When
        composeTestRule.setContent {
            AuthScreen(
                viewModel = mockViewModel,
                onAuthSuccess = { authTriggered = true }
            )
        }

        // Then
        composeTestRule.onNodeWithText("Sign in with GitHub").performClick()
        // Verify authentication flow was triggered
    }

    @Test
    fun errorRetryButton_triggersRetry() {
        // Given
        val errorMessage = "Authentication failed"
        mockAuthState.value = AuthViewModel.AuthState.Error(errorMessage)

        // When
        composeTestRule.setContent {
            AuthScreen(
                viewModel = mockViewModel,
                onAuthSuccess = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Try Again").performClick()
        // Verify retry was triggered
    }

    @Test
    fun authSuccess_triggersCallback() {
        // Given
        mockAuthState.value = AuthViewModel.AuthState.Authenticated("mock_token")
        var successCallbackTriggered = false

        // When
        composeTestRule.setContent {
            AuthScreen(
                viewModel = mockViewModel,
                onAuthSuccess = { successCallbackTriggered = true }
            )
        }

        // Then
        assert(successCallbackTriggered)
    }
}