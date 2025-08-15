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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.app.data.model.Issue
import com.github.app.presentation.viewmodel.IssuesState
import com.github.app.presentation.viewmodel.IssueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssuesScreen(
    owner: String,
    repoName: String,
    onBackClick: () -> Unit,
    onCreateIssue: () -> Unit,
    viewModel: IssueViewModel = hiltViewModel()
) {
    // TODO
    val issuesState by viewModel.issues.collectAsState()

    LaunchedEffect(owner, repoName) {
        viewModel.loadRepositoryIssues(owner, repoName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$owner/$repoName Issues") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onCreateIssue) {
                        Icon(Icons.Default.Add, contentDescription = "Create Issue")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (issuesState) {
            is IssuesState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is IssuesState.Error -> {
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
                        Text("Error loading issues")
                        Button(onClick = { viewModel.loadRepositoryIssues(owner, repoName) }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is IssuesState.Success -> {
                val issues = (issuesState as IssuesState.Success).issues
                if (issues.isEmpty()) {
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
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "No issues",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text("No issues found")
                            Button(onClick = onCreateIssue) {
                                Text("Create First Issue")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = paddingValues,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(issues) { issue ->
                            IssueCard(issue = issue)
                        }
                    }
                }
            }
            IssuesState.Idle -> {
                // Do nothing
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IssueCard(issue: Issue) {
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
            // Issue title and number
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = issue.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "#${issue.number}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Issue labels
            if (issue.labels.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    issue.labels.take(3).forEach { label ->
                        val backgroundColor = try {
                            Color(android.graphics.Color.parseColor("#${label.color}"))
                        } catch (e: Exception) {
                            MaterialTheme.colorScheme.primary
                        }
                        
                        AssistChip(
                            onClick = { },
                            label = { Text(label.name) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = backgroundColor.copy(alpha = 0.2f),
                                labelColor = backgroundColor
                            ),
                            modifier = Modifier.height(24.dp)
                        )
                    }
                    if (issue.labels.size > 3) {
                        Text(
                            text = "+${issue.labels.size - 3} more",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Issue state and metadata
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val icon = if (issue.state == "open") {
                        Icons.Default.ErrorOutline
                    } else {
                        Icons.Default.CheckCircle
                    }
                    
                    val color = if (issue.state == "open") {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    }
                    
                    Icon(
                        imageVector = icon,
                        contentDescription = issue.state,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Text(
                        text = issue.state.uppercase(),
                        style = MaterialTheme.typography.bodySmall,
                        color = color
                    )
                }
                
                Text(
                    text = "Opened by ${issue.user.login}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Issue body preview
            issue.body?.let { body ->
                if (body.isNotBlank()) {
                    Text(
                        text = body.take(100) + if (body.length > 100) "..." else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateIssueScreen(
    owner: String,
    repoName: String,
    onBackClick: () -> Unit,
    onIssueCreated: () -> Unit,
    viewModel: IssueViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var labels by remember { mutableStateOf("") }
    
    val createIssueState by viewModel.createIssueState.collectAsState()

    LaunchedEffect(createIssueState) {
        if (createIssueState is com.github.app.presentation.viewmodel.CreateIssueState.Success) {
            onIssueCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Issue") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            val labelList = labels.split(",")
                                .map { it.trim() }
                                .filter { it.isNotEmpty() }
                            viewModel.createIssue(owner, repoName, title, body, labelList)
                        },
                        enabled = title.isNotBlank() && 
                                createIssueState !is com.github.app.presentation.viewmodel.CreateIssueState.Loading
                    ) {
                        Text("Create")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (createIssueState) {
                is com.github.app.presentation.viewmodel.CreateIssueState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is com.github.app.presentation.viewmodel.CreateIssueState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = (createIssueState as com.github.app.presentation.viewmodel.CreateIssueState.Error).message,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                else -> {
                    // Title input
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Body input
                    OutlinedTextField(
                        value = body,
                        onValueChange = { body = it },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        maxLines = 10
                    )

                    // Labels input
                    OutlinedTextField(
                        value = labels,
                        onValueChange = { labels = it },
                        label = { Text("Labels (comma-separated)") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("bug, enhancement, documentation") }
                    )
                }
            }
        }
    }
}