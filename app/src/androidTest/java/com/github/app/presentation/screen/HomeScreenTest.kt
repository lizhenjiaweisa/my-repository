package com.github.app.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.app.data.model.Repository
import com.github.app.presentation.viewmodel.RepositoryState
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
    private lateinit var mockRepositoryState: MutableStateFlow<RepositoryState>

    @Before
    fun setup() {
        mockViewModel = mockk(relaxed = true)
        mockRepositoryState = MutableStateFlow(RepositoryState.Loading)
        every { mockViewModel.repositories } returns mockRepositoryState
    }

    @Test
    fun homeScreen_displaysLoadingState() {
        // Given
        mockRepositoryState.value = RepositoryState.Loading

        // When
        composeTestRule.setContent {
            HomeScreen(
                viewModel = mockViewModel,
                onRepositoryClick = { _, _ -> },
                onUserProfileClick = { _ -> },
                onAuthClick = { },
                onIssuesClick = { _, _ -> }
            )
        }

        // Then
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }
}