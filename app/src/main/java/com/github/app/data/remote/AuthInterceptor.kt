package com.github.app.data.remote

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val dataStore: DataStore<Preferences>
) : Interceptor {
    
    companion object {
        val TOKEN_KEY = stringPreferencesKey("access_token")
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Accept", "application/vnd.github.v3+json")
            .header("User-Agent", "GitHub-Android-App")
        
        // 从DataStore获取token
        val token = runBlocking {
            dataStore.data.first()[TOKEN_KEY]
        }
        
        if (!token.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "token $token")
        }
        
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}