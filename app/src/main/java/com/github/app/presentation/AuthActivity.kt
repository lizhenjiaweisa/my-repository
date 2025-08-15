package com.github.app.presentation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.github.app.BuildConfig
import com.github.app.presentation.screen.AuthScreen
import com.github.app.presentation.theme.GitHubAppTheme
import com.github.app.presentation.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import java.security.MessageDigest
import java.util.Base64
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    companion object {
        private const val RC_AUTH = 1001
        private const val TAG = "AuthActivity"
        private const val PREFS_NAME = "github_auth_prefs"
        private const val KEY_CODE_VERIFIER = "code_verifier"
    }

    private var authService: AuthorizationService? = null

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化AuthorizationService
        authService = AuthorizationService(this)
        
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
                
                // 获取保存的code_verifier用于PKCE校验
                val codeVerifier = sharedPreferences.getString(KEY_CODE_VERIFIER, null)
                if (codeVerifier != null) {
                    exchangeCodeForToken(code, codeVerifier)
                } else {
                    Log.e(TAG, "Code verifier not found for PKCE")
                    setResult(RESULT_CANCELED)
                    finish()
                }
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
        
        // 生成PKCE参数 - 使用符合规范的长度
        val codeVerifier = generateCodeVerifier(43)  // 最小长度43，最大128
        val codeChallenge = generateCodeChallenge(codeVerifier)
        
        // 保存code_verifier用于后续token交换
        sharedPreferences.edit()
            .putString(KEY_CODE_VERIFIER, codeVerifier)
            .apply()
        
        Log.d(TAG, "PKCE code verifier generated and saved")

        val request = AuthorizationRequest.Builder(
            serviceConfig,
            BuildConfig.GITHUB_CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse("githubapp://oauth/callback")
        )
            .setScope("repo user")
            .setCodeVerifier(codeVerifier, codeChallenge, "S256")
            .build()

        Log.d(TAG, "Authorization request built with PKCE: ${request.jsonSerializeString()}")
        authService?.let { service ->
            val authIntent = service.getAuthorizationRequestIntent(request)
            startActivityForResult(authIntent, RC_AUTH)
        } ?: run {
            Log.e(TAG, "AuthorizationService is null")
            setResult(RESULT_CANCELED)
            finish()
        }
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
            
            // 获取保存的code_verifier用于PKCE校验
            val codeVerifier = sharedPreferences.getString(KEY_CODE_VERIFIER, null)
            if (codeVerifier != null) {
                exchangeCodeForToken(code!!, codeVerifier)
            } else {
                Log.e(TAG, "Code verifier not found for PKCE")
                setResult(RESULT_CANCELED)
                finish()
            }
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

    private fun exchangeCodeForToken(code: String, codeVerifier: String) {
        Log.d(TAG, "Exchanging authorization code for access token with PKCE")
        
        val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse("https://github.com/login/oauth/authorize"),
            Uri.parse("https://github.com/login/oauth/access_token")
        )
        
        val tokenRequest = TokenRequest.Builder(
            serviceConfig,
            BuildConfig.GITHUB_CLIENT_ID
        )
            .setAuthorizationCode(code)
            .setRedirectUri(Uri.parse("githubapp://oauth/callback"))
            .setCodeVerifier(codeVerifier)
            .build()

        authService?.performTokenRequest(tokenRequest) { response, exception ->
            if (response != null) {
                val accessToken = response.accessToken
                Log.d(TAG, "Access token received successfully")
                
                // 清除保存的code_verifier
                sharedPreferences.edit()
                    .remove(KEY_CODE_VERIFIER)
                    .apply()
                
                val resultIntent = Intent().apply {
                    putExtra("access_token", accessToken)
                    putExtra("token_type", response.tokenType)
                    putExtra("scope", response.scope)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Log.e(TAG, "Token exchange failed: ${exception?.errorDescription}")
                val resultIntent = Intent().apply {
                    putExtra("error_message", exception?.errorDescription ?: "Token exchange failed")
                }
                setResult(RESULT_CANCELED, resultIntent)
                finish()
            }
        } ?: run {
            Log.e(TAG, "AuthorizationService is null during token exchange")
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    fun generateCodeVerifier(length: Int): String {
        // PKCE规范要求长度在43-128之间
        val validLength = length.coerceIn(43, 128)
        val bytes = ByteArray(validLength)
        kotlin.random.Random.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    // Step 2: Generate the code challenge from the code verifier
    fun generateCodeChallenge(codeVerifier: String): String {
        val hashBytes = MessageDigest.getInstance("SHA-256").digest(codeVerifier.toByteArray())
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes)
    }

    override fun onDestroy() {
        super.onDestroy()
        authService?.dispose()
        authService = null
    }
}