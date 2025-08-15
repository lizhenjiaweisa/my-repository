package com.github.app.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.github.app.data.model.User
import com.github.app.presentation.component.RepositoryCard
import com.github.app.presentation.viewmodel.UserRepositoriesState
import com.github.app.presentation.viewmodel.UserState
import com.github.app.presentation.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    username: String,
    onBackClick: () -> Unit,
    onRepositoryClick: (String, String) -> Unit,
    onIssuesClick: (String, String) -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    val userState by viewModel.userProfile.collectAsState()
    val userRepositoriesState by viewModel.userRepositories.collectAsState()

    LaunchedEffect(username) {
        viewModel.loadUserProfile(username)
        viewModel.loadUserRepositories(username)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (userState) {
            is UserState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UserState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Error loading user profile")
                        Button(onClick = { viewModel.loadUserProfile(username) }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is UserState.Success -> {
                val user = (userState as UserState.Success).user
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        UserHeader(user)
                    }
                    
                    item {
                        UserStats(user)
                    }
                    
                    item {
                        UserBio(user)
                    }
                    
                    item {
                        UserLinks(user)
                    }
                    
                    item {
                        Text(
                            text = "Repositories",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    
                    when (userRepositoriesState) {
                        is UserRepositoriesState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        is UserRepositoriesState.Error -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Error loading repositories")
                                }
                            }
                        }
                        is UserRepositoriesState.Success -> {
                            val repositories = (userRepositoriesState as UserRepositoriesState.Success).repositories
                            items(repositories) { repository ->
                                RepositoryCard(
                                    repository = repository,
                                    onClick = { onRepositoryClick(repository.owner.login, repository.name) },
                                    onUserClick = { /* Already on user profile */ },
                                    onIssuesClick = onIssuesClick,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                        UserRepositoriesState.Idle -> {
                            // Do nothing
                        }
                    }
                }
            }
            UserState.Idle -> {
                // Do nothing
            }
        }
    }
}

@Composable
private fun UserHeader(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = user.login + " avatar",
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
            
            Text(
                text = user.name ?: user.login,
                style = MaterialTheme.typography.headlineMedium
            )
            
            Text(
                text = "@${user.login}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            user.company?.let { company ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = "Company",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = company,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            user.location?.let { location ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun UserStats(user: User) {
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
            StatItem("Followers", user.followers.toString(), Icons.Default.People)
            StatItem("Following", user.following.toString(), Icons.Default.PersonAdd)
            StatItem("Repositories", user.publicRepos.toString(), Icons.Default.Folder)
            StatItem("Gists", user.publicGists.toString(), Icons.Default.Description)
        }
    }
}

@Composable
private fun UserBio(user: User) {
    user.bio?.let { bio ->
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
                    text = "Bio",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun UserLinks(user: User) {
    val hasLinks = user.blog?.isNotEmpty() == true || user.email?.isNotEmpty() == true
    
    if (hasLinks) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
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
                
                user.email?.takeIf { it.isNotEmpty() }?.let { email ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                user.blog?.takeIf { it.isNotEmpty() }?.let { blog ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Blog",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = blog,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}