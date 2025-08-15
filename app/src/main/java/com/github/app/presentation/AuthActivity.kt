package com.github.app.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.github.app.BuildConfig
import com.github.app.presentation.screen.AuthScreen
import com.github.app.presentation.theme.GitHubAppTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    companion object {
        private const val RC_AUTH = 1001
        private const val TAG = "AuthActivity"
    }

    @Inject
    lateinit var authService: AuthorizationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d(TAG, "onCreate called with intent: ${intent?.data}")
        Log.d(TAG, "Action: ${intent?.action}")
        Log.d(TAG, "Data: ${intent?.data}")
        
        // Handle the OAuth callback from deep link
        val data = intent?.data
        if (data != null && data.toString().startsWith("githubapp://oauth/callback")) {
            Log.d(TAG, "Handling OAuth callback via deep link: ${data.toString()}")
            handleDeepLinkCallback(data)
            return
        }

        // Handle the OAuth callback from authorization flow
        if (intent?.hasExtra("start_auth") == true) {
            Log.d(TAG, "Starting authorization flow")
            startAuthorization()
            return
        }

        Log.d(TAG, "Showing auth screen")
        // Show auth screen
        setContent {
            GitHubAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthScreen(
                        onAuthSuccess = {
                            Log.d(TAG, "Auth success callback")
                            setResult(RESULT_OK)
                            finish()
                        },
                        onCancel = {
                            Log.d(TAG, "Auth cancel callback")
                            setResult(RESULT_CANCELED)
                            finish()
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent called: ${intent?.data}")
        
        val data = intent?.data
        if (data != null && data.toString().startsWith("githubapp://oauth/callback")) {
            Log.d(TAG, "Handling OAuth callback in onNewIntent: ${data.toString()}")
            handleDeepLinkCallback(data)
        }
    }

    private fun handleDeepLinkCallback(data: Uri) {
        Log.d(TAG, "Processing deep link callback: $data")
        
        try {
            val code = data.getQueryParameter("code")
            val state = data.getQueryParameter("state")
            val error = data.getQueryParameter("error")
            
            if (error != null) {
                Log.e(TAG, "OAuth error: $error")
                val errorDescription = data.getQueryParameter("error_description") ?: "Authorization failed"
                
                val resultIntent = Intent().apply {
                    putExtra("error_message", errorDescription)
                }
                setResult(RESULT_CANCELED, resultIntent)
                finish()
                return
            }
            
            if (code != null) {
                Log.d(TAG, "Authorization code received from deep link: $code")
                
                val resultIntent = Intent().apply {
                    putExtra("auth_code", code)
                }
                Log.d(TAG, "11111111111111111111111111")
                setResult(RESULT_OK, resultIntent)
                Log.d(TAG, "222222222222222222222")
                finish()
                Log.d(TAG, "3333333333333333333")
            } else {
                Log.w(TAG, "No authorization code found in deep link")
                setResult(RESULT_CANCELED)
                finish()
            }
        } catch (e: Exception) {
            Log.d(TAG, "444444444444444"+e.toString())
            Log.e(TAG, "Error processing deep link callback", e)
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun startAuthorization() {
        Log.d(TAG, "Building authorization request")
        Log.d(TAG, "Client ID: ${BuildConfig.GITHUB_CLIENT_ID}")
        Log.d(TAG, "Redirect URI: githubapp://oauth/callback")
        
        val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse("https://github.com/login/oauth/authorize"),
            Uri.parse("https://github.com/login/oauth/access_token")
        )

        val request = AuthorizationRequest.Builder(
            serviceConfig,
            BuildConfig.GITHUB_CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse("githubapp://oauth/callback")
        )
            .setScope("repo user")
            .build()

        Log.d(TAG, "Authorization request built: ${request.jsonSerializeString()}")
        val authIntent = authService.getAuthorizationRequestIntent(request)
        startActivityForResult(authIntent, RC_AUTH)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        Log.d(TAG, "onActivityResult called: requestCode=$requestCode, resultCode=$resultCode, data=$data")
        
        if (requestCode == RC_AUTH) {
            handleAuthorizationResponse(data ?: return)
        }
    }

    private fun handleAuthorizationResponse(intent: Intent) {
        Log.d(TAG, "handleAuthorizationResponse called with intent: $intent")
        
        val response = AuthorizationResponse.fromIntent(intent)
        val exception = AuthorizationException.fromIntent(intent)

        Log.d(TAG, "Authorization response: $response")
        Log.d(TAG, "Authorization exception: $exception")

        if (response != null) {
            val code = response.authorizationCode
            Log.d(TAG, "Authorization code received: $code")
            
            val resultIntent = Intent().apply {
                putExtra("auth_code", code)
            }
            setResult(RESULT_OK, resultIntent)
            Log.d(TAG, "Setting RESULT_OK with auth code")
            finish()
        } else if (exception != null) {
            Log.e(TAG, "Authorization error: ${exception.error}, ${exception.errorDescription}")
            
            val resultIntent = Intent().apply {
                putExtra("error_message", exception.errorDescription ?: "Authorization cancelled")
            }
            setResult(RESULT_CANCELED, resultIntent)
            finish()
        } else {
            Log.w(TAG, "No response or exception found in intent")
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        authService.dispose()
    }
}