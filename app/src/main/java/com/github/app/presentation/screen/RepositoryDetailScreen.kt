package com.github.app.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.github.app.data.model.Repository
import com.github.app.presentation.viewmodel.RepositoryState
import com.github.app.presentation.viewmodel.RepositoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepositoryDetailScreen(
    owner: String,
    repoName: String,
    onBackClick: () -> Unit,
    onUserClick: (String) -> Unit,
    onIssuesClick: (String, String) -> Unit,
    viewModel: RepositoryViewModel = hiltViewModel()
) {
    val repositoryDetails by viewModel.repositoryDetails.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.getRepositoryDetails(owner, repoName)
    }

    val currentRepo = when (repositoryDetails) {
        is RepositoryState.Success -> {
            (repositoryDetails as RepositoryState.Success).data.firstOrNull()
        }
        else -> null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Repository Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onIssuesClick(owner, repoName) }) {
                        Icon(Icons.Default.BugReport, contentDescription = "Issues")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            currentRepo == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    when (repositoryDetails) {
                        is RepositoryState.Loading -> CircularProgressIndicator()
                        is RepositoryState.Error -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text("Error loading repository")
                                Button(onClick = { viewModel.getRepositoryDetails(owner, repoName) }) {
                                    Text("Retry")
                                }
                            }
                        }
                        else -> Text("Repository not found")
                    }
                }
            }
            else -> {
                val repo = currentRepo!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                ) {
                    RepositoryHeader(repo, onUserClick)
                    RepositoryStats(repo)
                    RepositoryDescription(repo)
                    RepositoryTopics(repo)
                    RepositoryLinks(repo)
                }
            }
        }
    }
}


@Composable
private fun RepositoryHeader(repository: Repository, onUserClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = repository.owner.avatarUrl,
                    contentDescription = "${repository.owner.login} avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onUserClick(repository.owner.login) },
                    contentScale = ContentScale.Crop
                )
                
                Column {
                    Text(
                        text = repository.name,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = repository.owner.login,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onUserClick(repository.owner.login) }
                    )
                }
            }
            
            Text(
                text = repository.fullName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RepositoryStats(repository: Repository) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem("Stars", repository.stars.toString(), Icons.Default.Star)
            StatItem("Forks", repository.forks.toString(), Icons.Default.CallSplit)
            StatItem("Language", repository.language ?: "N/A", Icons.Default.Code)
        }
    }
}

@Composable
fun StatItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RepositoryDescription(repository: Repository) {
    repository.description?.let { description ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RepositoryTopics(repository: Repository) {
    if (repository.topics.isNotEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Topics",
                    style = MaterialTheme.typography.titleMedium
                )
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repository.topics.forEach { topic ->
                        AssistChip(
                            onClick = { },
                            label = { Text(topic) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RepositoryLinks(repository: Repository) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Links",
                style = MaterialTheme.typography.titleMedium
            )
            
            Button(
                onClick = { /* Open in browser */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open in GitHub")
            }
            
            OutlinedButton(
                onClick = { /* Copy URL */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Copy Repository URL")
            }
        }
    }
}