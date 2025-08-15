package com.github.app.presentation.screen

import android.content.Context
import android.content.Intent
import com.github.app.presentation.MainActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.app.presentation.viewmodel.AuthState
import com.github.app.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onCancel: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val context = LocalContext.current



    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onAuthSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GitHub Authentication") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (authState) {
                is AuthState.Loading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Authenticating...")
                    }
                }
                is AuthState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = (authState as AuthState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.retryAuthentication() }) {
                            Text("Retry")
                        }
                        Button(
                            onClick = onCancel,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text("Cancel")
                        }
                    }
                }
                is AuthState.Authenticated -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val user = (authState as AuthState.Authenticated).user.user
                        Text("Welcome, ${user.name ?: user.login}!", style = MaterialTheme.typography.headlineMedium)
                        Text("You are now logged in to GitHub")
                        Button(onClick = onAuthSuccess) {
                            Text("Continue")
                        }
                    }
                }
                AuthState.Idle -> {
                    AuthPrompt(
                    onLoginClick = { 
                        val mainActivity = context as MainActivity
                        mainActivity.launchAuthActivity()
                    },
                    onCancel = onCancel
                )
                }
                AuthState.LoggedOut -> {
                    AuthPrompt(
                        onLoginClick = {
                            val mainActivity = context as MainActivity
                            mainActivity.launchAuthActivity()
                        },
                        onCancel = onCancel
                    )
                }
            }
        }
    }
}



@Composable
private fun AuthPrompt(
    onLoginClick: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "GitHub Explorer",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Text(
            text = "Sign in to access your repositories and create issues",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "What you get by signing in:",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Access your repositories")
                    Text("• Create and manage issues")
                    Text("• View your profile")
                    Text("• Follow other developers")
                }
            }
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onLoginClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Sign in with GitHub")
            }
            
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
        }
    }
}