# BDD步骤定义文档

## 概述
本文档为GitHub Android App的BDD测试提供详细的步骤定义，包括Gherkin语法的Given/When/Then步骤的具体实现说明。

## 认证相关步骤定义

### Given步骤
```gherkin
Given 用户未登录
```
- **实现**: 检查SharedPreferences中是否存在auth_token
- **预期结果**: auth_token为空或不存在

```gherkin
Given 用户已登录
```
- **实现**: 验证auth_token存在且有效
- **预期结果**: 返回有效的用户对象

```gherkin
Given 用户有GitHub账户
```
- **实现**: 使用测试账户配置
- **测试数据**: username="testuser", password="testpass123"

### When步骤
```gherkin
When 用户点击"使用GitHub登录"按钮
```
- **实现**: 触发OAuth登录流程
- **操作**: 启动GitHub OAuth授权页面

```gherkin
When 用户输入用户名"([^"]*)"和密码"([^"]*)"
```
- **实现**: 在WebView中自动填充登录表单
- **参数**: username, password

### Then步骤
```gherkin
Then 用户成功登录
```
- **验证**: 检查MainActivity是否启动
- **验证**: 检查用户头像是否显示在Toolbar

## 搜索相关步骤定义

### Given步骤
```gherkin
Given 用户在主界面
```
- **实现**: 验证当前Activity为MainActivity
- **验证**: 搜索框可见且可交互

### When步骤
```gherkin
When 用户在搜索框输入"([^"]*)"
```
- **实现**: 在SearchView中输入文本
- **延迟**: 500ms防抖延迟

```gherkin
When 点击搜索按钮
```
- **实现**: 触发SearchView的提交操作
- **API调用**: GET /search/repositories?q={query}

### Then步骤
```gherkin
Then 显示搜索结果列表
```
- **验证**: RecyclerView显示结果
- **验证**: 每个item显示name, description, language, stars

## 用户资料步骤定义

### Given步骤
```gherkin
Given 用户在用户资料页面
```
- **实现**: 验证当前Fragment为UserProfileFragment
- **验证**: 显示用户头像、用户名、关注数

### When步骤
```gherkin
When 用户点击"关注"按钮
```
- **实现**: 调用PUT /user/following/{username}
- **状态更新**: 更新UI按钮状态

### Then步骤
```gherkin
Then 按钮文本变为"已关注"
```
- **验证**: 检查按钮text属性
- **验证**: 检查following_count增加

## 问题管理步骤定义

### Given步骤
```gherkin
Given 用户在仓库详情页面
```
- **实现**: 验证RepositoryDetailActivity已启动
- **验证**: 显示仓库基本信息

### When步骤
```gherkin
When 用户点击"新建问题"按钮
```
- **实现**: 启动CreateIssueActivity
- **验证**: 表单字段预填充

### Then步骤
```gherkin
Then 成功创建问题
```
- **验证**: API返回201状态码
- **验证**: 跳转到IssueDetailActivity

## 网络状态步骤定义

### Given步骤
```gherkin
Given 网络连接已断开
```
- **实现**: 使用NetworkCallback模拟断网
- **验证**: ConnectivityManager返回false

### When步骤
```gherkin
When 网络重新连接
```
- **实现**: 触发NetworkCallback的onAvailable回调
- **延迟**: 2秒重连延迟

### Then步骤
```gherkin
Then 显示"网络已恢复"提示
```
- **验证**: Snackbar显示网络状态消息
- **验证**: 自动刷新当前页面

## 测试数据管理

### 测试用户数据
```kotlin
object TestData {
    val testUser = User(
        login = "testuser",
        id = 12345,
        avatar_url = "https://avatars.githubusercontent.com/u/12345?v=4",
        name = "Test User",
        location = "Test City",
        company = "Test Company",
        followers = 100,
        following = 50
    )
}
```

### 测试仓库数据
```kotlin
object TestData {
    val testRepository = Repository(
        name = "android-test-repo",
        full_name = "testuser/android-test-repo",
        description = "A test repository for Android app",
        language = "Kotlin",
        stargazers_count = 150,
        forks_count = 25,
        open_issues_count = 5
    )
}
```

## UI交互步骤定义

### 等待条件
```kotlin
fun waitForView(viewId: Int, timeout: Long = 5000) {
    onView(withId(viewId))
        .perform(waitForViewToAppear(timeout))
}
```

### 滚动操作
```gherkin
When 用户向下滑动
```
- **实现**: 执行RecyclerView滚动
- **验证**: 检查目标视图可见

### 点击操作
```gherkin
When 用户点击"([^"]*)"按钮
```
- **实现**: 使用onView(withText()).perform(click())
- **验证**: 检查预期Activity或Fragment启动

## 错误处理步骤定义

### API错误场景
```gherkin
When GitHub API返回(\d+)错误
```
- **实现**: 使用MockWebServer模拟错误响应
- **验证**: 检查错误消息显示

### 网络超时场景
```gherkin
When 网络请求超时
```
- **实现**: 设置MockWebServer延迟30秒
- **验证**: 检查超时错误提示

## 缓存验证步骤定义

### 缓存存在验证
```gherkin
Then 显示缓存的仓库列表
```
- **验证**: 检查Room数据库中的缓存数据
- **验证**: 显示"离线模式"提示

### 缓存过期验证
```gherkin
Given 缓存数据已超过24小时
```
- **实现**: 修改数据库中created_at时间戳
- **验证**: 显示数据过期警告

## 性能测试步骤定义

### 加载时间验证
```gherkin
Then 页面在(\d+)秒内加载完成
```
- **实现**: 使用IdlingResource监控加载时间
- **验证**: 检查加载时间小于阈值

### 内存使用验证
```gherkin
Then 内存使用不超过(\d+)MB
```
- **实现**: 使用ActivityManager获取内存信息
- **验证**: 检查内存泄漏

## 跨平台测试注意事项

### Android版本兼容性
- **最低版本**: API 21 (Android 5.0)
- **目标版本**: API 34 (Android 14)
- **测试设备**: 覆盖主流厂商设备

### 屏幕尺寸适配
- **小屏幕**: 4.7英寸 (720x1280)
- **标准屏幕**: 6.1英寸 (1080x2340)
- **大屏幕**: 6.7英寸 (1440x3200)

## 测试执行环境

### 本地开发环境
```bash
# 运行单个feature
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.github.test.AuthenticationFeatureTest

# 运行所有BDD测试
./gradlew connectedAndroidTest
```

### CI/CD环境
```yaml
# GitHub Actions配置
- name: Run BDD Tests
  uses: reactivecircus/android-emulator-runner@v2
  with:
    api-level: 29
    script: ./gradlew connectedAndroidTest
```

## 测试报告

### Allure报告配置
```gradle
allure {
    version = "2.13.0"
    aspectjweaver = true
    autoconfigure = true
}
```

### 报告输出路径
- **HTML报告**: `build/reports/androidTests/connected/index.html`
- **XML报告**: `build/outputs/androidTest-results/connected/`
- **截图**: `build/outputs/connected_android_test_additional_output/`

## 调试技巧

### 日志调试
```kotlin
fun logStep(step: String) {
    Log.d("BDD_TEST", "Executing step: $step")
}
```

### 截图调试
```kotlin
fun takeScreenshot(name: String) {
    ScreenCapture.takeScreenshot(name)
}
```

### 网络调试
```kotlin
fun logNetworkCalls() {
    OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
}
```

---

**文档版本**: v1.0.0  
**最后更新**: 2024年  
**维护团队**: GitHub Android App QA团队