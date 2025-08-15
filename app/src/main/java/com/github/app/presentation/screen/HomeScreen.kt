package com.github.app.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Login
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.app.data.model.Repository
import com.github.app.presentation.component.RepositoryCard
import com.github.app.presentation.viewmodel.RepositoryState
import com.github.app.presentation.viewmodel.RepositoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRepositoryClick: (String, String) -> Unit,
    onUserProfileClick: (String) -> Unit,
    onAuthClick: () -> Unit,
    onIssuesClick: (String, String) -> Unit,
    viewModel: RepositoryViewModel = hiltViewModel()
) {
    val repositories by viewModel.repositories.collectAsState()
    val trendingRepositories by viewModel.trendingRepositories.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf<String?>(null) }
    var showSearch by remember { mutableStateOf(false) }

    val languages = listOf(
        "All", "Kotlin", "Java", "JavaScript", "TypeScript", "Python",
        "Go", "Rust", "C++", "C", "Swift", "PHP", "Ruby", "Shell", "Dart"
    )

    LaunchedEffect(Unit) {
        viewModel.getTrendingRepositories(refresh = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GitHub Explorer") },
                actions = {
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = onAuthClick) {
                        Icon(Icons.Default.Login, contentDescription = "Sign in to GitHub")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showSearch) {
                SearchSection(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    selectedLanguage = selectedLanguage,
                    onLanguageChange = { selectedLanguage = it },
                    languages = languages,
                    onSearch = {
                        if (searchQuery.isNotBlank()) {
                            viewModel.searchRepositories(
                                query = searchQuery,
                                language = selectedLanguage?.takeIf { it != "All" },
                                refresh = true
                            )
                        }
                    }
                )
            }

            TabRow(
                selectedTabIndex = if (showSearch) 0 else 1,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = showSearch,
                    onClick = { showSearch = true },
                    text = { Text("Search") }
                )
                Tab(
                    selected = !showSearch,
                    onClick = { showSearch = false },
                    text = { Text("Trending") }
                )
            }

            val currentState = if (showSearch) repositories else trendingRepositories
            
            when (currentState) {
                is RepositoryState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is RepositoryState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Error: ${currentState.message}")
                            Button(onClick = {
                                if (showSearch && searchQuery.isNotBlank()) {
                                    viewModel.searchRepositories(
                                        query = searchQuery,
                                        language = selectedLanguage?.takeIf { it != "All" },
                                        refresh = true
                                    )
                                } else {
                                    viewModel.getTrendingRepositories(refresh = true)
                                }
                            }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is RepositoryState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(currentState.data) { repository ->
                            RepositoryCard(
                                repository = repository,
                                onClick = { onRepositoryClick(repository.owner.login, repository.name) },
                                onUserClick = { onUserProfileClick(repository.owner.login) },
                                onIssuesClick = { str1,str2 ->
                                    onIssuesClick(repository.owner.login, repository.name) }
                            )
                        }
                        
                        if (currentState.data.size < currentState.totalCount) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Button(
                                        onClick = {
                                            if (showSearch && searchQuery.isNotBlank()) {
                                                viewModel.loadMoreRepositories(
                                                    query = searchQuery,
                                                    language = selectedLanguage?.takeIf { it != "All" }
                                                )
                                            } else {
                                                viewModel.loadMoreTrending()
                                            }
                                        }
                                    ) {
                                        Text("Load More")
                                    }
                                }
                            }
                        }
                    }
                }
                RepositoryState.Idle -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedLanguage: String?,
    onLanguageChange: (String?) -> Unit,
    languages: List<String>,
    onSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Search repositories") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically ,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            var expanded by remember { mutableStateOf(false) }
            
            Box(modifier = Modifier.weight(1f)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedLanguage ?: "All",
                        onValueChange = {},
                        label = { Text("Language") },
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.exposedDropdownSize()
                    ) {
                        languages.forEach { language ->
                            DropdownMenuItem(
                                text = { Text(language) },
                                onClick = {
                                    onLanguageChange(language.takeIf { it != "All" })
                                    expanded = false
                                }
                    )
                        }
                    }
                }
            }
            
            Button(
                onClick = onSearch,
                modifier = Modifier.width(120.dp)
            ) {
                Text("Search")
            }
        }
    }
}