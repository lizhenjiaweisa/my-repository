# BDD实施指南

## 🎯 项目概述

本文档为GitHub Android App提供完整的BDD（行为驱动开发）实施指南，包括测试框架配置、最佳实践和持续集成设置。

## 📋 技术栈

### 测试框架
- **Cucumber**: BDD测试框架
- **Espresso**: Android UI测试
- **MockWebServer**: 网络请求模拟
- **Room**: 本地数据库测试
- **Hilt**: 依赖注入测试

### 工具配置
```gradle
// build.gradle (app module)
androidTestImplementation 'io.cucumber:cucumber-android:4.8.4'
androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
androidTestImplementation 'com.squareup.okhttp3:mockwebserver:4.9.1'
androidTestImplementation 'com.google.dagger:hilt-android-testing:2.38.1'
```

## 🏗️ 项目结构

```
doc/features/
├── authentication.feature       # 用户认证
├── repository-search.feature    # 仓库搜索
├── user-profile.feature        # 用户资料
├── issues.feature              # 问题管理
└── offline-mode.feature        # 离线模式

src/androidTest/java/com/github/test/
├── runner/
│   └── CucumberTestRunner.kt
├── steps/
│   ├── AuthenticationSteps.kt
│   ├── RepositorySteps.kt
│   ├── UserProfileSteps.kt
│   └── IssuesSteps.kt
├── support/
│   ├── TestData.kt
│   ├── NetworkModule.kt
│   └── DatabaseModule.kt
└── utils/
    ├── IdlingResource.kt
    └── ScreenshotUtil.kt
```

## 🔧 配置步骤

### 1. 测试运行器配置

```kotlin
// CucumberTestRunner.kt
class CucumberTestRunner : AndroidJUnitRunner() {
    override fun onCreate(arguments: Bundle) {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        super.onCreate(arguments)
    }

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
```

### 2. Cucumber配置

```kotlin
// src/androidTest/assets/cucumber.properties
cucumber.publish.quiet=true
cucumber.plugin=pretty,html:build/reports/cucumber.html
```

### 3. Hilt测试配置

```kotlin
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class]
)
object TestNetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }
}
```

## 🧪 测试场景实现

### 认证测试实现

```kotlin
class AuthenticationSteps {
    private lateinit var mockWebServer: MockWebServer
    
    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start(8080)
    }
    
    @Given("用户未登录")
    fun userIsNotLoggedIn() {
        clearSharedPreferences()
        assertNull(getAuthToken())
    }
    
    @When("用户点击\"使用GitHub登录\"按钮")
    fun clickLoginButton() {
        onView(withId(R.id.btn_github_login))
            .perform(click())
    }
    
    @Then("用户成功登录")
    fun userSuccessfullyLoggedIn() {
        onView(withId(R.id.toolbar_user_avatar))
            .check(matches(isDisplayed()))
        
        assertNotNull(getAuthToken())
    }
    
    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
```

### 搜索测试实现

```kotlin
class RepositorySearchSteps {
    
    @Given("用户在主界面")
    fun userIsOnMainScreen() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.search_view))
            .check(matches(isDisplayed()))
    }
    
    @When("用户在搜索框输入\"{string}\"")
    fun enterSearchQuery(query: String) {
        onView(withId(R.id.search_view))
            .perform(typeText(query))
    }
    
    @Then("显示搜索结果列表")
    fun searchResultsDisplayed() {
        onView(withId(R.id.recycler_view))
            .check(matches(hasMinimumChildCount(1)))
    }
    
    @Then("每个结果显示：")
    fun verifyRepositoryInfo(dataTable: DataTable) {
        val expectedData = dataTable.asMap(String::class.java, String::class.java)
        
        onView(withRecyclerView(R.id.recycler_view)
            .atPosition(0))
            .check(matches(hasDescendant(withText(expectedData["仓库名"]))))
    }
}
```

## 📊 测试数据管理

### 测试数据工厂

```kotlin
object TestDataFactory {
    
    fun createMockRepositoryResponse(): MockResponse {
        val json = """
            {
                "items": [
                    {
                        "name": "android-compose-samples",
                        "full_name": "android/android-compose-samples",
                        "description": "Samples for Jetpack Compose",
                        "language": "Kotlin",
                        "stargazers_count": 15234,
                        "forks_count": 2345
                    }
                ]
            }
        """.trimIndent()
        
        return MockResponse()
            .setResponseCode(200)
            .setBody(json)
            .setHeader("Content-Type", "application/json")
    }
    
    fun createMockUserResponse(): MockResponse {
        val json = """
            {
                "login": "octocat",
                "id": 1,
                "avatar_url": "https://avatars.githubusercontent.com/u/1?v=4",
                "name": "The Octocat",
                "location": "San Francisco",
                "followers": 3456,
                "following": 123
            }
        """.trimIndent()
        
        return MockResponse()
            .setResponseCode(200)
            .setBody(json)
    }
}
```

## 🔍 测试执行

### 本地测试命令

```bash
# 运行所有BDD测试
./gradlew connectedAndroidTest

# 运行特定feature
./gradlew connectedAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.feature=features/authentication.feature

# 运行特定标签
./gradlew connectedAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.tags="@auth"

# 生成测试报告
./gradlew createDebugCoverageReport
```

### CI/CD集成

```yaml
# .github/workflows/bdd-tests.yml
name: BDD Tests

on:
  pull_request:
    branches: [ main, develop ]

jobs:
  bdd-tests:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'
        
    - name: Setup Android SDK
      uses: android-actions/setup-android@v2
      
    - name: Run BDD Tests
      uses: reactivecircus/android-emulator-runner@v2
      with:
        api-level: 29
        script: ./gradlew connectedAndroidTest
        
    - name: Upload Test Results
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: test-results
        path: |
          build/reports/androidTests/
          build/outputs/androidTest-results/
```

## 📈 测试报告

### Allure报告集成

```gradle
// build.gradle
apply plugin: 'io.qameta.allure'

allure {
    version = '2.13.0'
    autoconfigure = true
    aspectjweaver = true
}

allureReport {
    dependsOn 'connectedAndroidTest'
    resultsDir = file("$buildDir/allure-results")
}
```

### 报告目录结构
```
build/reports/
├── androidTests/
│   └── connected/
│       ├── index.html
│       ├── classes/
│       └── packages/
├── cucumber/
│   └── cucumber.html
└── allure/
    └── index.html
```

## 🎯 最佳实践

### 1. 测试独立性
- 每个测试场景应该是独立的
- 使用@Before和@After清理测试数据
- 避免测试间的状态共享

### 2. 数据管理
- 使用测试数据工厂模式
- 避免硬编码测试数据
- 定期清理测试数据库

### 3. 网络测试
- 使用MockWebServer模拟API响应
- 测试网络错误场景
- 验证超时处理

### 4. UI测试
- 使用Page Object模式
- 添加适当的等待条件
- 捕获测试失败截图

### 5. 性能测试
- 监控内存使用
- 检查内存泄漏
- 验证加载时间

## 🔧 调试技巧

### 日志调试
```kotlin
object TestLogger {
    fun logStep(step: String) {
        Log.d("BDD_TEST", "=== Executing: $step ===")
    }
    
    fun logResponse(response: String) {
        Log.d("BDD_TEST", "Response: $response")
    }
}
```

### 截图工具
```kotlin
object ScreenshotUtil {
    fun takeScreenshot(name: String) {
        val screenshot = Screenshot.capture()
        val path = "/sdcard/Pictures/bdd-tests/$name.png"
        screenshot.writeToFile(path)
    }
}
```

## 📊 代码覆盖率

### JaCoCo配置
```gradle
apply plugin: 'jacoco'

jacoco {
    toolVersion = "0.8.7"
}

task jacocoTestReport(type: JacocoReport, dependsOn: ['connectedAndroidTest']) {
    reports {
        xml.enabled = true
        html.enabled = true
    }
    
    executionData fileTree(dir: "$buildDir/outputs/code-coverage/connected", include: "**/*.ec")
    
    sourceDirectories.setFrom(files("$projectDir/src/main/java"))
    classDirectories.setFrom(fileTree(dir: "$buildDir/tmp/kotlin-classes/debug"))
}
```

### 覆盖率目标
- **行覆盖率**: >80%
- **分支覆盖率**: >75%
- **方法覆盖率**: >85%

## 🚀 持续优化

### 测试维护
- 定期更新测试数据
- 监控测试执行时间
- 清理过时测试用例

### 性能优化
- 使用并行测试执行
- 优化测试数据加载
- 减少测试执行时间

### 团队协作
- 代码审查测试用例
- 共享测试最佳实践
- 定期培训新成员

## 📞 支持和维护

### 常见问题
1. **测试超时**: 增加超时时间或优化网络模拟
2. **设备兼容性**: 在多个设备上验证测试
3. **数据一致性**: 确保测试数据同步

### 联系信息
- **测试团队**: qa-team@github.com
- **技术支持**: android-team@github.com
- **文档维护**: docs-team@github.com

---

**文档版本**: v1.0.0  
**最后更新**: 2024年  
**维护团队**: GitHub Android App QA团队