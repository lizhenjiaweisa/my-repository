## TODO list
1 登录功能为实现，可以拿到AccessToken,PKCE 鉴权过不去。 待修改
2 UI测试，单元测试完善
3 代码逻辑优化

PKCE 参考
```java
import okhttp3.*
import java.security.MessageDigest
import java.util.Base64

// 替换为你的 GitHub Client ID 和回调 URL
const val CLIENT_ID = "your-client-id"
const val REDIRECT_URI = "http://localhost:3000/callback"

// Step 1: Generate a random code verifier
fun generateCodeVerifier(length: Int): String {
    val bytes = ByteArray(length)
    kotlin.random.Random.nextBytes(bytes)
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
}

// Step 2: Generate the code challenge from the code verifier
fun generateCodeChallenge(codeVerifier: String): String {
    val hashBytes = MessageDigest.getInstance("SHA-256").digest(codeVerifier.toByteArray())
    return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes)
}

// Step 3: Start the authorization request
fun getAuthUrl(codeChallenge: String) {
    val authUrl = "https://github.com/login/oauth/authorize?" +
            "client_id=$CLIENT_ID&" +
            "redirect_uri=${REDIRECT_URI.encodeToUriComponent()}&" +
            "response_type=code&" +
            "scope=repo&" +
            "code_challenge=${codeChallenge.encodeToUriComponent()}&" +
            "code_challenge_method=S256"
    println("Redirect user to: $authUrl")
}

// Step 4: Handle the callback and exchange the authorization code for an access token
suspend fun exchangeCodeForToken(code: String, codeVerifier: String) {
    val client = OkHttpClient()
    val formBody = FormBody.Builder()
        .add("client_id", CLIENT_ID)
        .add("redirect_uri", REDIRECT_URI)
        .add("code", code)
        .add("code_verifier", codeVerifier)
        .build()

    val request = Request.Builder()
        .url("https://github.com/login/oauth/access_token")
        .post(formBody)
        .header("Accept", "application/json")
        .build()

    try {
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) throw IOException("Unexpected code $response")

        val responseBody = response.body?.string() ?: ""
        val responseData = responseBody.split("&").associate { it.substringBefore("=") to it.substringAfter("=") }
        val accessToken = responseData["access_token"]
        val tokenType = responseData["token_type"]

        println("Access Token: $accessToken")
        println("Token Type: $tokenType")

        return
    } catch (e: Exception) {
        System.err.println("Error exchanging code for token: ${e.message}")
    }
}

// Example usage
fun main() = runBlocking {
    val codeVerifier = generateCodeVerifier(128)
    val codeChallenge = generateCodeChallenge(codeVerifier)

    getAuthUrl(codeChallenge)

    // Simulate receiving the code from GitHub callback
    val receivedCode = "your-received-code-from-github-callback" // Replace this with the actual code you receive

    exchangeCodeForToken(receivedCode, codeVerifier)
}
```