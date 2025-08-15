# GitHub Android App - UML序列图详细设计

## 1. 用户认证序列图

### 1.1 GitHub OAuth认证流程
```mermaid
sequenceDiagram
    participant User as 用户
    participant App as 移动应用
    participant AuthVM as AuthViewModel
    participant AuthRepo as AuthRepository
    participant AuthService as GitHub Auth Service
    participant DataStore as DataStore
    
    User->>App: 点击"使用GitHub登录"
    App->>AuthVM: loginWithGitHub()
    AuthVM->>AuthRepo: initiateOAuthFlow()
    AuthRepo->>AuthService: 构建授权URL
    AuthService-->>AuthRepo: 返回授权URL
    AuthRepo-->>AuthVM: 返回授权URL
    AuthVM-->>App: 打开GitHub授权页面
    
    App->>User: 显示GitHub登录页面
    User->>GitHub: 输入用户名密码并授权
    GitHub-->>App: 重定向到应用，携带授权码
    
    App->>AuthVM: handleAuthCallback(intent)
    AuthVM->>AuthRepo: exchangeCodeForToken(code)
    AuthRepo->>AuthService: POST /login/oauth/access_token
    AuthService-->>AuthRepo: 返回访问令牌
    AuthRepo->>DataStore: saveToken(token)
    DataStore-->>AuthRepo: 保存成功
    AuthRepo-->>AuthVM: 认证成功
    AuthVM-->>App: 更新UI状态为已登录
    App->>User: 显示主界面
```

### 1.2 自动登录流程
```mermaid
sequenceDiagram
    participant App as 应用启动
    participant AuthVM as AuthViewModel
    participant AuthRepo as AuthRepository
    participant DataStore as DataStore
    participant GitHubAPI as GitHub API
    
    App->>AuthVM: 检查登录状态
    AuthVM->>AuthRepo: getCurrentToken()
    AuthRepo->>DataStore: getStoredToken()
    DataStore-->>AuthRepo: 返回存储的token
    
    alt Token存在且有效
        AuthRepo->>GitHubAPI: 验证token有效性
        GitHubAPI-->>AuthRepo: token有效
        AuthRepo-->>AuthVM: 返回当前用户
        AuthVM-->>App: 自动登录成功
    else Token不存在或无效
        AuthRepo-->>AuthVM: 无有效token
        AuthVM-->>App: 显示登录界面
    end
```

## 2. 仓库浏览序列图

### 2.1 搜索仓库流程
```mermaid
sequenceDiagram
    participant User as 用户
    participant SearchUI as 搜索界面
    participant RepoVM as RepositoryViewModel
    participant SearchUseCase as SearchUseCase
    participant RepoRepo as RepositoryRepository
    participant GitHubAPI as GitHub API
    participant Cache as 本地缓存
    
    User->>SearchUI: 输入搜索关键词
    SearchUI->>RepoVM: 更新搜索查询
    RepoVM->>SearchUseCase: execute(query)
    
    SearchUseCase->>RepoRepo: searchRepositories(query)
    RepoRepo->>Cache: 检查缓存
    
    alt 缓存命中
        Cache-->>RepoRepo: 返回缓存结果
    else 缓存未命中
        RepoRepo->>GitHubAPI: GET /search/repositories
        GitHubAPI-->>RepoRepo: 返回搜索结果
        RepoRepo->>Cache: 缓存结果
    end
    
    RepoRepo-->>SearchUseCase: 返回仓库列表
    SearchUseCase-->>RepoVM: 返回结果
    RepoVM-->>SearchUI: 更新UI状态
    SearchUI->>User: 显示搜索结果
```

### 2.2 查看仓库详情流程
```mermaid
sequenceDiagram
    participant User as 用户
    participant ListUI as 列表界面
    participant DetailUI as 详情界面
    participant RepoVM as RepositoryViewModel
    participant GetRepoUseCase as GetRepositoryUseCase
    participant RepoRepo as RepositoryRepository
    participant GitHubAPI as GitHub API
    
    User->>ListUI: 点击仓库
    ListUI->>DetailUI: 导航到详情页
    DetailUI->>RepoVM: loadRepository(owner, repo)
    
    RepoVM->>GetRepoUseCase: execute(owner, repo)
    GetRepoUseCase->>RepoRepo: getRepository(owner, repo)
    RepoRepo->>GitHubAPI: GET /repos/{owner}/{repo}
    GitHubAPI-->>RepoRepo: 返回仓库详情
    RepoRepo-->>GetRepoUseCase: 返回仓库信息
    GetRepoUseCase-->>RepoVM: 返回结果
    
    RepoVM->>DetailUI: 更新仓库详情
    DetailUI->>User: 显示仓库信息
    
    DetailUI->>RepoVM: loadReadme(owner, repo)
    RepoVM->>GitHubAPI: GET /repos/{owner}/{repo}/readme
    GitHubAPI-->>RepoVM: 返回README内容
    RepoVM-->>DetailUI: 更新README显示
```

### 2.3 加载趋势仓库流程
```mermaid
sequenceDiagram
    participant HomeUI as 主界面
    participant RepoVM as RepositoryViewModel
    participant TrendingUseCase as GetTrendingUseCase
    participant RepoRepo as RepositoryRepository
    participant GitHubAPI as GitHub API
    participant Cache as 本地缓存
    
    HomeUI->>RepoVM: loadTrendingRepositories(language)
    RepoVM->>TrendingUseCase: execute(language)
    
    TrendingUseCase->>RepoRepo: getTrendingRepositories(language)
    RepoRepo->>Cache: 检查趋势数据缓存
    
    alt 缓存有效且未过期
        Cache-->>RepoRepo: 返回缓存的趋势数据
    else 需要刷新
        RepoRepo->>GitHubAPI: GET /search/repositories (按stars排序)
        GitHubAPI-->>RepoRepo: 返回热门仓库
        RepoRepo->>Cache: 更新缓存
    end
    
    RepoRepo-->>TrendingUseCase: 返回趋势仓库列表
    TrendingUseCase-->>RepoVM: 返回结果
    RepoVM-->>HomeUI: 更新UI显示
```

## 3. 用户资料浏览序列图

### 3.1 查看用户资料流程
```mermaid
sequenceDiagram
    participant User as 用户
    participant ProfileUI as 用户资料界面
    participant UserVM as UserViewModel
    participant GetUserUseCase as GetUserUseCase
    participant UserRepo as UserRepository
    participant GitHubAPI as GitHub API
    
    User->>ProfileUI: 点击用户名
    ProfileUI->>UserVM: loadUserProfile(username)
    
    UserVM->>GetUserUseCase: execute(username)
    GetUserUseCase->>UserRepo: getUserProfile(username)
    UserRepo->>GitHubAPI: GET /users/{username}
    GitHubAPI-->>UserRepo: 返回用户资料
    UserRepo-->>GetUserUseCase: 返回用户信息
    GetUserUseCase-->>UserVM: 返回结果
    
    UserVM->>UserRepo: getUserRepositories(username)
    UserRepo->>GitHubAPI: GET /users/{username}/repos
    GitHubAPI-->>UserRepo: 返回用户仓库列表
    UserRepo-->>UserVM: 返回仓库列表
    
    UserVM-->>ProfileUI: 更新用户资料和仓库
    ProfileUI->>User: 显示用户资料页面
```

## 4. 问题管理序列图

### 4.1 查看仓库问题流程
```mermaid
sequenceDiagram
    participant User as 用户
    participant DetailUI as 仓库详情界面
    participant IssuesUI as 问题列表界面
    participant IssueVM as IssueViewModel
    participant GetIssuesUseCase as GetIssuesUseCase
    participant IssueRepo as IssueRepository
    participant GitHubAPI as GitHub API
    
    User->>DetailUI: 点击"查看问题"
    DetailUI->>IssuesUI: 导航到问题列表
    IssuesUI->>IssueVM: loadIssues(owner, repo)
    
    IssueVM->>GetIssuesUseCase: execute(owner, repo)
    GetIssuesUseCase->>IssueRepo: getIssues(owner, repo)
    IssueRepo->>GitHubAPI: GET /repos/{owner}/{repo}/issues
    GitHubAPI-->>IssueRepo: 返回问题列表
    IssueRepo-->>GetIssuesUseCase: 返回问题
    GetIssuesUseCase-->>IssueVM: 返回结果
    IssueVM-->>IssuesUI: 更新问题列表
    IssuesUI->>User: 显示问题列表
```

### 4.2 创建新问题流程
```mermaid
sequenceDiagram
    participant User as 用户
    participant IssuesUI as 问题列表界面
    participant CreateUI as 创建问题界面
    participant IssueVM as IssueViewModel
    participant CreateIssueUseCase as CreateIssueUseCase
    participant IssueRepo as IssueRepository
    participant GitHubAPI as GitHub API
    
    User->>IssuesUI: 点击"创建新问题"
    IssuesUI->>CreateUI: 导航到创建界面
    
    User->>CreateUI: 填写问题标题和内容
    CreateUI->>IssueVM: createIssue(owner, repo, issueRequest)
    
    IssueVM->>CreateIssueUseCase: execute(request)
    CreateIssueUseCase->>IssueRepo: createIssue(owner, repo, issue)
    IssueRepo->>GitHubAPI: POST /repos/{owner}/{repo}/issues
    
    alt 创建成功
        GitHubAPI-->>IssueRepo: 返回创建的问题
        IssueRepo-->>CreateIssueUseCase: 返回新问题
        CreateIssueUseCase-->>IssueVM: 返回结果
        IssueVM-->>CreateUI: 创建成功
        CreateUI->>IssuesUI: 返回并刷新列表
        IssuesUI->>User: 显示新问题
    else 创建失败
        GitHubAPI-->>IssueRepo: 错误响应
        IssueRepo-->>CreateIssueUseCase: 抛出异常
        CreateIssueUseCase-->>IssueVM: 错误状态
        IssueVM-->>CreateUI: 显示错误信息
    end
```

## 5. 数据同步序列图

### 5.1 下拉刷新流程
```mermaid
sequenceDiagram
    participant User as 用户
    participant UI as 界面
    participant RepoVM as RepositoryViewModel
    participant SyncUseCase as SyncDataUseCase
    participant RepoRepo as RepositoryRepository
    participant GitHubAPI as GitHub API
    participant Cache as 本地缓存
    
    User->>UI: 下拉刷新
    UI->>RepoVM: refreshData()
    RepoVM->>SyncUseCase: execute()
    
    SyncUseCase->>RepoRepo: syncRepositories()
    RepoRepo->>GitHubAPI: GET /search/repositories (最新数据)
    GitHubAPI-->>RepoRepo: 返回最新仓库数据
    RepoRepo->>Cache: 清除旧缓存
    RepoRepo->>Cache: 保存新数据
    Cache-->>RepoRepo: 更新完成
    RepoRepo-->>SyncUseCase: 同步完成
    SyncUseCase-->>RepoVM: 返回最新数据
    RepoVM-->>UI: 更新显示
    UI->>User: 显示刷新后的数据
```

### 5.2 分页加载更多流程
```mermaid
sequenceDiagram
    participant User as 用户
    participant ListUI as 列表界面
    participant RepoVM as RepositoryViewModel
    participant LoadMoreUseCase as LoadMoreUseCase
    participant RepoRepo as RepositoryRepository
    participant GitHubAPI as GitHub API
    
    User->>ListUI: 滚动到底部
    ListUI->>RepoVM: loadMore()
    
    RepoVM->>LoadMoreUseCase: execute(nextPage)
    LoadMoreUseCase->>RepoRepo: getRepositories(page=nextPage)
    RepoRepo->>GitHubAPI: GET /search/repositories?page=nextPage
    GitHubAPI-->>RepoRepo: 返回下一页数据
    RepoRepo-->>LoadMoreUseCase: 返回更多仓库
    LoadMoreUseCase-->>RepoVM: 返回结果
    
    RepoVM->>ListUI: 追加数据到列表
    ListUI->>User: 显示更多仓库
```

## 6. 错误处理序列图

### 6.1 网络错误处理
```mermaid
sequenceDiagram
    participant UI as 界面
    participant RepoVM as RepositoryViewModel
    participant UseCase as 用例
    participant RepoRepo as RepositoryRepository
    participant GitHubAPI as GitHub API
    participant ErrorHandler as 错误处理器
    
    UI->>RepoVM: 请求数据
    RepoVM->>UseCase: execute()
    UseCase->>RepoRepo: 获取数据
    RepoRepo->>GitHubAPI: API调用
    
    alt 网络错误
        GitHubAPI-->>RepoRepo: 网络异常
        RepoRepo-->>UseCase: 抛出NetworkException
        UseCase-->>RepoVM: 错误状态
        RepoVM->>ErrorHandler: 处理错误
        ErrorHandler-->>RepoVM: 返回用户友好的错误信息
        RepoVM-->>UI: 显示错误提示
        UI->>User: 显示"网络连接失败，请重试"
    else 服务器错误
        GitHubAPI-->>RepoRepo: 500错误
        RepoRepo-->>UseCase: 抛出ServerException
        UseCase-->>RepoVM: 错误状态
        RepoVM-->>UI: 显示服务器错误
    else 认证失败
        GitHubAPI-->>RepoRepo: 401未授权
        RepoRepo-->>UseCase: 抛出AuthException
        UseCase-->>RepoVM: 需要重新登录
        RepoVM-->>UI: 导航到登录页
    end
```

### 6.2 缓存失效处理
```mermaid
sequenceDiagram
    participant RepoVM as RepositoryViewModel
    participant Cache as 缓存管理器
    participant Validator as 数据验证器
    participant RepoRepo as RepositoryRepository
    participant GitHubAPI as GitHub API
    
    RepoVM->>Cache: 获取缓存数据
    Cache->>Validator: 检查缓存有效性
    
    alt 缓存过期
        Validator-->>Cache: 缓存已过期
        Cache-->>RepoVM: 返回空或过期数据
        RepoVM->>RepoRepo: 从网络获取最新数据
        RepoRepo->>GitHubAPI: 获取最新数据
        GitHubAPI-->>RepoRepo: 返回最新数据
        RepoRepo->>Cache: 更新缓存
        Cache-->>RepoVM: 返回最新数据
    else 缓存有效
        Validator-->>Cache: 缓存有效
        Cache-->>RepoVM: 返回缓存数据
    end
```

## 7. 数据验证序列图

### 7.1 搜索查询验证
```mermaid
sequenceDiagram
    participant User as 用户
    participant SearchUI as 搜索界面
    participant Validator as 输入验证器
    participant RepoVM as RepositoryViewModel
    
    User->>SearchUI: 输入搜索词
    SearchUI->>Validator: validateQuery(query)
    
    alt 查询有效
        Validator-->>SearchUI: 验证通过
        SearchUI->>RepoVM: 执行搜索
    else 查询太短
        Validator-->>SearchUI: 查询至少需要2个字符
        SearchUI->>User: 显示错误提示
    else 特殊字符
        Validator-->>SearchUI: 包含无效字符
        SearchUI->>User: 清理输入并提示
    end
```

## 8. 性能优化序列图

### 8.1 图片加载优化
```mermaid
sequenceDiagram
    participant UI as 列表界面
    participant ImageLoader as 图片加载器
    participant Cache as 图片缓存
    participant Coil as Coil库
    participant Network as 网络
    
    UI->>ImageLoader: 加载用户头像
    ImageLoader->>Cache: 检查内存缓存
    
    alt 内存缓存命中
        Cache-->>ImageLoader: 返回缓存图片
        ImageLoader-->>UI: 显示图片
    else 内存缓存未命中
        ImageLoader->>Cache: 检查磁盘缓存
        alt 磁盘缓存命中
            Cache-->>ImageLoader: 返回磁盘图片
            ImageLoader->>Cache: 保存到内存缓存
            ImageLoader-->>UI: 显示图片
        else 磁盘缓存未命中
            ImageLoader->>Coil: 从网络加载
            Coil->>Network: 下载图片
            Network-->>Coil: 返回图片数据
            Coil->>Cache: 保存到磁盘缓存
            Coil->>Cache: 保存到内存缓存
            Coil-->>ImageLoader: 返回图片
            ImageLoader-->>UI: 显示图片
        end
    end
```

### 8.2 数据预加载
```mermaid
sequenceDiagram
    participant ListUI as 列表界面
    participant RepoVM as RepositoryViewModel
    participant Preloader as 数据预加载器
    participant RepoRepo as RepositoryRepository
    
    ListUI->>RepoVM: 显示当前页面
    RepoVM->>Preloader: 检测滚动位置
    
    alt 接近底部
        Preloader->>RepoRepo: 预加载下一页
        RepoRepo-->>Preloader: 返回预加载数据
        Preloader->>RepoVM: 缓存预加载数据
    end
    
    ListUI->>RepoVM: 用户滚动到底部
    RepoVM-->>ListUI: 立即显示预加载数据
```

---

**说明**: 所有序列图使用Mermaid语法，描述了应用的核心交互流程和错误处理机制。