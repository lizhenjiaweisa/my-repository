package com.github.app.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.github.app.presentation.navigation.GitHubNavGraph
import com.github.app.presentation.theme.GitHubAppTheme
import com.github.app.presentation.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    private val authLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("MainActivity", "Auth result received: resultCode=${result.resultCode}")
        
        if (result.resultCode == RESULT_OK) {
            val access_token = result.data?.getStringExtra("access_token")
            val token_type = result.data?.getStringExtra("token_type")
            val scope = result.data?.getStringExtra("scope")
            Log.d("MainActivity", "Auth code received: $access_token $token_type $scope")
            
            if (access_token != null) {
                authViewModel.exchangeCodeForToken(access_token)
            } else {
                Log.w("MainActivity", "No auth code found in result")
                authViewModel.setAuthError("No authorization code received")
            }
        } else {
            val errorMessage = result.data?.getStringExtra("error_message") ?: "Authentication cancelled"
            Log.d("MainActivity", "Auth cancelled or failed: $errorMessage")
            authViewModel.setAuthError(errorMessage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 不再处理深度链接，让AuthActivity专门处理
        setContent {
            GitHubAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    GitHubNavGraph(
                        navController = navController,
                        startDestination = "home"
                    )
                }
            }
        }
    }


    fun launchAuthActivity() {
        try {
            val intent = authViewModel.getAuthorizationIntent(this)
            authLauncher.launch(intent)
        } catch (e: Exception) {

        }
    }
}