package com.github.app.data.remote

import com.github.app.data.model.AccessTokenResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface GitHubAuthService {
    @POST("login/oauth/access_token")
    @FormUrlEncoded
    suspend fun exchangeCodeForToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String
    ): Response<AccessTokenResponse>
}