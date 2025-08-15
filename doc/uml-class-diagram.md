# GitHub Android App - UML类图详细设计

## 1. 整体架构类图

### 1.1 MVVM架构类图
```mermaid
classDiagram
    class MainActivity {
        -NavController navController
        +onCreate(savedInstanceState: Bundle)
        +onSupportNavigateUp(): Boolean
    }
    
    class GitHubApplication {
        +onCreate()
    }
    
    class BaseViewModel {
        <<abstract>>
        #CompositeDisposable disposables
        +onCleared()
    }
    
    class RepositoryViewModel {
        -GetRepositoriesUseCase getRepositoriesUseCase
        -SearchRepositoriesUseCase searchRepositoriesUseCase
        -GetTrendingRepositoriesUseCase getTrendingRepositoriesUseCase
        +repositories: StateFlow~List~Repository~~
        +searchQuery: StateFlow~String~
        +isLoading: StateFlow~Boolean~
        +error: StateFlow~String?~
        +searchRepositories(query: String)
        +loadTrendingRepositories(language: String)
        +loadMoreRepositories()
    }
    
    class UserViewModel {
        -GetUserProfileUseCase getUserProfileUseCase
        -GetUserRepositoriesUseCase getUserRepositoriesUseCase
        +userProfile: StateFlow~User?~
        +userRepositories: StateFlow~List~Repository~~
        +loadUserProfile(username: String)
        +loadUserRepositories(username: String)
    }
    
    class IssueViewModel {
        -GetIssuesUseCase getIssuesUseCase
        -CreateIssueUseCase createIssueUseCase
        +issues: StateFlow~List~Issue~~
        +createIssue(owner: String, repo: String, issue: IssueRequest)
    }
    
    class AuthViewModel {
        -AuthRepository authRepository
        -GetCurrentUserUseCase getCurrentUserUseCase
        +authState: StateFlow~AuthState~
        +currentUser: StateFlow~User?~
        +loginWithGitHub()
        +logout()
        +handleAuthCallback(intent: Intent)
    }
    
    BaseViewModel <|-- RepositoryViewModel
    BaseViewModel <|-- UserViewModel
    BaseViewModel <|-- IssueViewModel
    BaseViewModel <|-- AuthViewModel
```

## 2. 数据层类图

### 2.1 仓库模式类图
```mermaid
classDiagram
    class Repository~T~ {
        <<interface>>
        +getAll(): Flow~List~T~~
        +getById(id: Long): Flow~T~
        +save(item: T): Flow~T~
        +delete(item: T): Flow~Boolean~
    }
    
    class RepositoryRepository {
        -RemoteDataSource remoteDataSource
        -LocalDataSource localDataSource
        +searchRepositories(query: String): Flow~List~Repository~
        +getRepository(owner: String, repo: String): Flow~Repository~
        +getTrendingRepositories(language: String): Flow~List~Repository~
        +getUserRepositories(username: String): Flow~List~Repository~
    }
    
    class UserRepository {
        -RemoteDataSource remoteDataSource
        -LocalDataSource localDataSource
        +getUserProfile(username: String): Flow~User~
        +getCurrentUser(): Flow~User~
    }
    
    class IssueRepository {
        -RemoteDataSource remoteDataSource
        -LocalDataSource localDataSource
        +getIssues(owner: String, repo: String): Flow~List~Issue~
        +getIssue(owner: String, repo: String, number: Int): Flow~Issue~
        +createIssue(owner: String, repo: String, issue: IssueRequest): Flow~Issue~
    }
    
    class AuthRepository {
        -AuthRemoteDataSource authRemoteDataSource
        -AuthLocalDataSource authLocalDataSource
        +authenticate(code: String): Flow~AuthToken~
        +getStoredToken(): Flow~AuthToken?~
        +saveToken(token: AuthToken): Flow~Boolean~
        +clearToken(): Flow~Boolean~
    }
    
    Repository~Repository~ <|-- RepositoryRepository
    Repository~User~ <|-- UserRepository
    Repository~Issue~ <|-- IssueRepository
    Repository~AuthToken~ <|-- AuthRepository
```

### 2.2 数据源类图
```mermaid
classDiagram
    class RemoteDataSource {
        <<interface>>
        +getRepositories(query: String): Flow~List~Repository~~
        +getRepository(owner: String, repo: String): Flow~Repository~
        +getUserRepositories(username: String): Flow~List~Repository~~
    }
    
    class GitHubApiService {
        -OkHttpClient httpClient
        +searchRepositories(@Query("q") query: String): Call~SearchResponse~
        +getRepository(@Path("owner") owner: String, @Path("repo") repo: String): Call~Repository~
        +getUserRepositories(@Path("username") username: String): Call~List~Repository~~
    }
    
    class LocalDataSource {
        -AppDatabase database
        -DataStore dataStore
        +getCachedRepositories(): Flow~List~RepositoryEntity~~
        +cacheRepositories(repositories: List~RepositoryEntity~): Flow~Boolean~
        +getCachedUser(username: String): Flow~UserEntity~
    }
    
    class AuthRemoteDataSource {
        -AuthApiService authApiService
        +exchangeCodeForToken(code: String): Flow~AuthToken~
        +refreshToken(refreshToken: String): Flow~AuthToken~
    }
    
    class AuthLocalDataSource {
        -DataStore dataStore
        +getAuthToken(): Flow~AuthToken?~
        +saveAuthToken(token: AuthToken): Flow~Boolean~
        +clearAuthToken(): Flow~Boolean~
    }
    
    RemoteDataSource <|-- GitHubApiService
    RemoteDataSource <|-- AuthRemoteDataSource
```

## 3. 领域层类图

### 3.1 用例类图
```mermaid
classDiagram
    class UseCase~Input, Output~ {
        <<interface>>
        +execute(input: Input): Flow~Output~
    }
    
    class GetRepositoriesUseCase {
        -RepositoryRepository repository
        +execute(page: Int): Flow~List~Repository~~
    }
    
    class SearchRepositoriesUseCase {
        -RepositoryRepository repository
        +execute(query: String): Flow~List~Repository~~
    }
    
    class GetTrendingRepositoriesUseCase {
        -RepositoryRepository repository
        +execute(language: String): Flow~List~Repository~~
    }
    
    class GetUserProfileUseCase {
        -UserRepository userRepository
        +execute(username: String): Flow~User~
    }
    
    class GetUserRepositoriesUseCase {
        -RepositoryRepository repository
        +execute(username: String): Flow~List~Repository~~
    }
    
    class GetIssuesUseCase {
        -IssueRepository issueRepository
        +execute(owner: String, repo: String): Flow~List~Issue~~
    }
    
    class CreateIssueUseCase {
        -IssueRepository issueRepository
        +execute(request: IssueRequest): Flow~Issue~
    }
    
    UseCase~Int, List~Repository~~ <|-- GetRepositoriesUseCase
    UseCase~String, List~Repository~~ <|-- SearchRepositoriesUseCase
    UseCase~String, List~Repository~~ <|-- GetTrendingRepositoriesUseCase
    UseCase~String, User~ <|-- GetUserProfileUseCase
    UseCase~String, List~Repository~~ <|-- GetUserRepositoriesUseCase
    UseCase~IssueRequest, Issue~ <|-- CreateIssueUseCase
```

### 3.2 实体类图
```mermaid
classDiagram
    class BaseEntity {
        <<abstract>>
        +Long id
        +Date createdAt
        +Date updatedAt
    }
    
    class Repository {
        +String name
        +String fullName
        +String description
        +String language
        +Int stargazersCount
        +Int forksCount
        +Int openIssuesCount
        +User owner
        +String htmlUrl
        +Boolean isPrivate
        +Int size
        +String defaultBranch
    }
    
    class User {
        +String login
        +String name
        +String email
        +String avatarUrl
        +String bio
        +String location
        +String company
        +Int followers
        +Int following
        +Int publicRepos
        +String htmlUrl
    }
    
    class Issue {
        +Int number
        +String title
        +String body
        +String state
        +User user
        +Repository repository
        +List~Label~ labels
        +User assignee
        +Date closedAt
    }
    
    class Label {
        +String name
        +String color
        +String description
    }
    
    class AuthToken {
        +String accessToken
        +String tokenType
        +String scope
        +Long expiresIn
        +String refreshToken
    }
    
    BaseEntity <|-- Repository
    BaseEntity <|-- User
    BaseEntity <|-- Issue
    BaseEntity <|-- Label
```

## 4. 表示层类图

### 4.1 UI组件类图
```mermaid
classDiagram
    class GitHubApp {
        +NavHostController navController
        +onCreate()
    }
    
    class MainScreen {
        -RepositoryViewModel viewModel
        -NavController navController
        +Content()
    }
    
    class RepositoryListScreen {
        -RepositoryViewModel viewModel
        -LazyListState listState
        +Content()
        +RepositoryItem(repository: Repository)
        +LoadingIndicator()
        +ErrorMessage(error: String)
    }
    
    class RepositoryDetailScreen {
        -RepositoryViewModel viewModel
        -String owner
        -String repo
        +Content()
        +RepositoryHeader(repository: Repository)
        +ReadmeSection(readme: String)
        +IssuesSection(issues: List~Issue~)
    }
    
    class UserProfileScreen {
        -UserViewModel viewModel
        -String username
        +Content()
        +UserHeader(user: User)
        +RepositoryGrid(repositories: List~Repository~)
    }
    
    class SearchScreen {
        -RepositoryViewModel viewModel
        -String initialQuery
        +Content()
        +SearchBar(query: String, onQueryChange: (String) -> Unit)
        +FilterChips(selectedLanguage: String, onLanguageChange: (String) -> Unit)
    }
    
    class AuthScreen {
        -AuthViewModel viewModel
        +Content()
        +LoginButton(onLoginClick: () -> Unit)
        +LoadingState()
        +ErrorState(error: String)
    }
    
    class CustomTopAppBar {
        +title: String
        +navigationIcon: @Composable () -> Unit
        +actions: @Composable RowScope.() -> Unit
    }
    
    class RepositoryCard {
        -Repository repository
        -onClick: (Repository) -> Unit
        +Content()
    }
    
    class LanguageChip {
        -language: String
        -isSelected: Boolean
        -onClick: () -> Unit
        +Content()
    }
```

### 4.2 导航类图
```mermaid
classDiagram
    class NavGraph {
        <<interface>>
        +NavGraphBuilder.build()
    }
    
    class AppNavigation {
        -NavController navController
        +navigateToRepositoryDetail(owner: String, repo: String)
        +navigateToUserProfile(username: String)
        +navigateToIssues(owner: String, repo: String)
        +navigateToSearch(query: String)
        +popBackStack()
    }
    
    class NavigationRoutes {
        <<enumeration>>
        HOME
        REPOSITORY_DETAIL
        USER_PROFILE
        ISSUES
        SEARCH
        AUTH
    }
    
    class Route {
        +String path
        +Map~String, Any~ arguments
        +String buildRoute(vararg args: Any)
    }
    
    AppNavigation --> Route
    AppNavigation --> NavigationRoutes
```

## 5. 依赖注入类图

### 5.1 Hilt模块类图
```mermaid
classDiagram
    class AppModule {
        <<object>>
        +provideDatabase(@ApplicationContext context: Context): AppDatabase
        +provideGitHubApiService(retrofit: Retrofit): GitHubApiService
        +provideRetrofit(okHttpClient: OkHttpClient): Retrofit
        +provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient
    }
    
    class AuthModule {
        <<object>>
        +provideAuthApiService(retrofit: Retrofit): AuthApiService
        +provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor
    }
    
    class DatabaseModule {
        <<object>>
        +provideRepositoryDao(database: AppDatabase): RepositoryDao
        +provideUserDao(database: AppDatabase): UserDao
        +provideIssueDao(database: AppDatabase): IssueDao
    }
    
    class DataStoreModule {
        <<object>>
        +provideDataStore(@ApplicationContext context: Context): DataStore~Preferences~
    }
    
    class RepositoryModule {
        <<object>>
        +provideRepositoryRepository(
            remoteDataSource: RemoteDataSource,
            localDataSource: LocalDataSource
        ): RepositoryRepository
    }
    
    class ViewModelModule {
        <<abstract>>
        +bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
    }
    
    AppModule --> DatabaseModule
    AppModule --> AuthModule
    DatabaseModule --> DataStoreModule
    RepositoryModule --> AppModule
    RepositoryModule --> DatabaseModule
```

## 6. 错误处理类图

### 6.1 异常处理类图
```mermaid
classDiagram
    class AppException {
        <<abstract>>
        +String message
        +Throwable cause
    }
    
    class NetworkException {
        +Int statusCode
        +String errorBody
    }
    
    class DatabaseException {
        +String operation
        +String tableName
    }
    
    class AuthException {
        +String reason
        +String errorCode
    }
    
    class ValidationException {
        +String field
        +String value
    }
    
    class ErrorHandler {
        +handleError(throwable: Throwable): ErrorState
        +isRetryable(error: AppException): Boolean
    }
    
    class ErrorState {
        +String message
        +Boolean isRetryable
        +String actionLabel
    }
    
    AppException <|-- NetworkException
    AppException <|-- DatabaseException
    AppException <|-- AuthException
    AppException <|-- ValidationException
    ErrorHandler --> ErrorState
    ErrorHandler --> AppException
```

## 7. 状态管理类图

### 7.1 UI状态类图
```mermaid
classDiagram
    class UiState {
        <<interface>>
    }
    
    class LoadingState {
        +Boolean isLoading
        +String message
    }
    
    class SuccessState~T~ {
        +T data
    }
    
    class ErrorState {
        +String errorMessage
        +Boolean isRetryable
    }
    
    class EmptyState {
        +String message
        +String actionLabel
    }
    
    class RepositoryListState {
        +List~Repository~ repositories
        +Boolean isLoading
        +String? error
        +Boolean hasMore
        +Int currentPage
    }
    
    class UserProfileState {
        +User? user
        +List~Repository~ repositories
        +Boolean isLoading
        +String? error
    }
    
    class AuthState {
        +Boolean isAuthenticated
        +User? currentUser
        +Boolean isLoading
        +String? error
    }
    
    UiState <|-- LoadingState
    UiState <|-- SuccessState
    UiState <|-- ErrorState
    UiState <|-- EmptyState
    RepositoryListState --> UiState
    UserProfileState --> UiState
    AuthState --> UiState
```

## 8. 网络层类图

### 8.1 API客户端类图
```mermaid
classDiagram
    class ApiClient {
        <<interface>>
        +get(url: String): Flow~Response~
        +post(url: String, body: Any): Flow~Response~
        +put(url: String, body: Any): Flow~Response~
        +delete(url: String): Flow~Response~
    }
    
    class GitHubApiClient {
        -OkHttpClient httpClient
        -Gson gson
        +searchRepositories(query: String): Flow~SearchResponse~
        +getRepository(owner: String, repo: String): Flow~Repository~
        +getUserRepositories(username: String): Flow~List~Repository~~
        +getUserProfile(username: String): Flow~User~
    }
    
    class AuthApiClient {
        -OkHttpClient httpClient
        +exchangeCodeForToken(code: String): Flow~AuthToken~
        +refreshToken(refreshToken: String): Flow~AuthToken~
    }
    
    class ApiResponse {
        <<sealed>>
    }
    
    class Success~T~ {
        +T data
        +Headers headers
    }
    
    class Error {
        +Int code
        +String message
        +String? errorBody
    }
    
    class Loading {
        +Boolean isLoading
    }
    
    ApiClient <|-- GitHubApiClient
    ApiClient <|-- AuthApiClient
    ApiResponse <|-- Success
    ApiResponse <|-- Error
    ApiResponse <|-- Loading
```

---

**注意**: 所有类图使用Mermaid语法绘制，可以在支持Mermaid的工具中直接渲染查看。