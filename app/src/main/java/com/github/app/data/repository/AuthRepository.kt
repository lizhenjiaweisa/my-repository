package com.github.app.data.repository

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.app.BuildConfig
import com.github.app.data.model.AuthenticatedUser
import com.github.app.data.model.User
import com.github.app.data.remote.GitHubApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.openid.appauth.*
import javax.inject.Inject
import javax.inject.Singleton
import android.content.Intent
import android.content.Context
import com.github.app.data.remote.GitHubAuthService
import com.github.app.presentation.AuthActivity
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Singleton
class AuthRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val apiService: GitHubApiService
) {
    private val TOKEN_KEY = stringPreferencesKey("access_token")

    val authState: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[TOKEN_KEY]
        }

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun clearAuthToken() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }

    suspend fun getAuthenticatedUser(): Result<AuthenticatedUser> {
        return try {
            val token = dataStore.data.map { preferences ->
                preferences[TOKEN_KEY]
            }.first()
            
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No authentication token found"))
            }

            val response = apiService.getAuthenticatedUser()
            if (response.isSuccessful) {
                response.body()?.let { user ->
                    Result.success(AuthenticatedUser(user, token))
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to fetch user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAuthorizationRequest(): AuthorizationRequest {
        val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse("https://github.com/login/oauth/authorize"),
            Uri.parse("https://github.com/login/oauth/access_token")
        )

        return AuthorizationRequest.Builder(
            serviceConfig,
            BuildConfig.GITHUB_CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse("githubapp://oauth/callback")
        )
            .setScope("repo user")
            .build()
    }

    suspend fun exchangeCodeForToken(code: String): Result<String> {
        return try {
            // 创建临时的Retrofit实例用于OAuth交换
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://github.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val gitHubAuthService = retrofit.create(GitHubAuthService::class.java)
            
            val response = gitHubAuthService.exchangeCodeForToken(
                clientId = BuildConfig.GITHUB_CLIENT_ID,
                clientSecret = BuildConfig.GITHUB_CLIENT_SECRET,
                code = code
            )
            
            if (response.isSuccessful) {
                response.body()?.accessToken?.let { token ->
                    dataStore.edit { settings ->
                        settings[TOKEN_KEY] = token
                    }
                    Result.success(token)
                } ?: Result.failure(Exception("Empty token response"))
            } else {
                Result.failure(Exception("Token exchange failed: ${response.code()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAuthorizationIntent(context: Context): Intent {
        val intent = Intent(context, AuthActivity::class.java)
        intent.putExtra("start_auth", true)
        return intent
    }

    // TODO
    suspend fun isLoggedIn(): Flow<Boolean> {
        return authState.map { it != null }
    }
}