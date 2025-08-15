## TODO list
1. ✅ **登录功能已实现** - 已增加PKCE校验支持，支持OAuth 2.0 PKCE流程
2. ✅ **AuthorizationService问题已修复** - 修复了服务被提前释放的问题
3. UI测试，单元测试完善
4. 代码逻辑优化

## PKCE实现总结
✅ **已完成功能：**
- AuthActivity.kt：增加PKCE code_verifier生成和保存
- AuthRepository.kt：支持PKCE的token交换
- GitHubAuthService.kt：增加exchangeCodeForTokenWithPKCE方法
- 支持OAuth 2.0 PKCE标准流程

## 修复记录
### ✅ AuthorizationService问题修复
- **问题**: Service has been disposed and rendered inoperable
- **原因**: AuthorizationService被Dagger注入但生命周期管理不当
- **解决方案**: 
  - 改为手动创建AuthorizationService实例
  - 在onCreate中初始化，在onDestroy中释放
  - 添加空值检查，防止使用已释放的服务
  - 移除AuthViewModel的依赖，直接在Activity中处理token交换

## PKCE实现细节
- 使用SHA-256生成code challenge
- 支持S256 code challenge method
- 使用SharedPreferences保存code_verifier
- 自动清理已使用的code_verifier
- 向后兼容传统OAuth流程

## 测试验证
- 支持GitHub OAuth 2.0 PKCE流程
- 支持deep link回调处理
- 支持access token获取和存储
- 修复了服务生命周期问题

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