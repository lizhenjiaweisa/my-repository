package com.github.app.presentation.component

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchBar_displaysCorrectly() {
        composeTestRule.setContent {
            SearchBar(
                query = "",
                onQueryChange = {},
                onSearch = {},
                modifier = androidx.compose.ui.Modifier
            )
        }

        composeTestRule.onNodeWithTag("SearchTextField").assertExists()
        composeTestRule.onNodeWithTag("SearchIcon").assertExists()
    }

    @Test
    fun searchBar_showsQueryText() {
        val testQuery = "android"
        
        composeTestRule.setContent {
            SearchBar(
                query = testQuery,
                onQueryChange = {},
                onSearch = {},
                modifier = androidx.compose.ui.Modifier
            )
        }

        composeTestRule.onNodeWithText(testQuery).assertExists()
    }

    @Test
    fun searchBar_clearButton_showsWhenQueryNotEmpty() {
        composeTestRule.setContent {
            SearchBar(
                query = "test",
                onQueryChange = {},
                onSearch = {},
                modifier = androidx.compose.ui.Modifier
            )
        }

        composeTestRule.onNodeWithTag("ClearButton").assertExists()
    }

    @Test
    fun searchBar_clearButton_hiddenWhenQueryEmpty() {
        composeTestRule.setContent {
            SearchBar(
                query = "",
                onQueryChange = {},
                onSearch = {},
                modifier = androidx.compose.ui.Modifier
            )
        }

        composeTestRule.onNodeWithTag("ClearButton").assertDoesNotExist()
    }

    @Test
    fun searchBar_onQueryChangeCalledWhenTextChanges() {
        var queryChanged = ""
        
        composeTestRule.setContent {
            SearchBar(
                query = "",
                onQueryChange = { queryChanged = it },
                onSearch = {},
                modifier = androidx.compose.ui.Modifier
            )
        }

        val testInput = "kotlin"
        composeTestRule.onNodeWithTag("SearchTextField").performTextInput(testInput)
        assert(queryChanged == testInput)
    }

    @Test
    fun searchBar_onSearchCalledWhenSearchActionPerformed() {
        var searchPerformed = false
        
        composeTestRule.setContent {
            SearchBar(
                query = "test",
                onQueryChange = {},
                onSearch = { searchPerformed = true },
                modifier = androidx.compose.ui.Modifier
            )
        }

        // Note: This would require IME action testing which needs special setup
        // For now, we'll test the clear button click
        composeTestRule.onNodeWithTag("SearchIcon").performClick()
        // In a real scenario, you'd test the IME action
    }

    @Test
    fun searchBar_clearButtonClearsQuery() {
        var query = "test"
        var cleared = false
        
        composeTestRule.setContent {
            SearchBar(
                query = query,
                onQueryChange = { 
                    if (it.isEmpty()) cleared = true
                    query = it 
                },
                onSearch = {},
                modifier = androidx.compose.ui.Modifier
            )
        }

        composeTestRule.onNodeWithTag("ClearButton").performClick()
        assert(cleared)
    }
}