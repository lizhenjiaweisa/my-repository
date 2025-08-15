# GitHub Android App - 组件图与部署图

## 1. 系统组件架构图

### 1.1 整体组件结构
```mermaid
graph TB
    subgraph Android Application
        subgraph Presentation Layer
            UI[UI Components]
            VM[ViewModels]
            NAV[Navigation]
            COMP[Custom Components]
        end
        
        subgraph Domain Layer
            UC[Use Cases]
            ENT[Domain Entities]
            REPO[Repository Interfaces]
        end
        
        subgraph Data Layer
            REPO_IMPL[Repository Implementations]
            API[API Clients]
            DB[Database Layer]
            CACHE[Cache Manager]
        end
        
        subgraph Core Layer
            DI[Dependency Injection]
            ERROR[Error Handling]
            LOG[Logging]
            CONFIG[Configuration]
        end
    end
    
    subgraph External Services
        GITHUB[GitHub REST API]
        CDN[GitHub CDN]
        OAUTH[GitHub OAuth]
    end
    
    subgraph Storage
        LOCAL[Shared Preferences]
        ROOM[Room Database]
        DATASTORE[DataStore]
    end
    
    UI --> VM
    VM --> UC
    UC --> REPO_IMPL
    REPO_IMPL --> API
    REPO_IMPL --> DB
    API --> GITHUB
    API --> CDN
    API --> OAUTH
    
    DB --> ROOM
    CACHE --> LOCAL
    CACHE --> DATASTORE
    
    DI --> VM
    DI --> REPO_IMPL
    DI --> API
    ERROR --> VM
    LOG --> ALL[All Components]
```

### 1.2 模块依赖关系图
```mermaid
graph LR
    subgraph App Module
        A[app]
    end
    
    subgraph Core Modules
        B[data]
        C[domain]
        D[presentation]
    end
    
    subgraph External Libraries
        E[Jetpack Compose]
        F[Hilt]
        G[Retrofit]
        H[Room]
        I[Coil]
    end
    
    A --> B
    A --> C
    A --> D
    
    B --> G
    B --> H
    C --> F
    D --> E
    D --> I
    
    style A fill:#f9f,stroke:#333
    style B fill:#bbf,stroke:#333
    style C fill:#9f9,stroke:#333
    style D fill:#ff9,stroke:#333
```

## 2. 部署架构图

### 2.1 客户端部署架构
```mermaid
graph TB
    subgraph Android Device
        subgraph Application
            APP[GitHub App APK]
            RES[Resources]
            CONFIG[Configuration Files]
        end
        
        subgraph Android OS
            ART[ART Runtime]
            SYS[System Services]
            PERM[Permission Manager]
        end
        
        subgraph Device Storage
            APP_DATA[App Data]
            CACHE[App Cache]
            DB_FILE[Database File]
        end
    end
    
    subgraph Network Layer
        WIFI[WiFi/4G/5G]
        DNS[DNS Resolution]
        TLS[TLS/SSL]
    end
    
    subgraph Cloud Infrastructure
        GITHUB_API[api.github.com]
        GITHUB_CDN[user-images.githubusercontent.com]
        CDN[GitHub Content CDN]
    end
    
    APP --> ART
    APP --> SYS
    APP --> APP_DATA
    
    APP --> WIFI
    WIFI --> DNS
    DNS --> GITHUB_API
    TLS --> GITHUB_API
    TLS --> GITHUB_CDN
    TLS --> CDN
```

### 2.2 网络架构图
```mermaid
graph LR
    subgraph Client Side
        APP[Android App]
        HTTP[OkHttp Client]
        CACHE[HTTP Cache]
        INTERCEPTOR[Auth Interceptor]
    end
    
    subgraph Network
        INTERNET[Internet]
        DNS[DNS Server]
        PROXY[Corporate Proxy]
    end
    
    subgraph GitHub Infrastructure
        LB[Load Balancer]
        API[API Gateway]
        AUTH[OAuth Service]
        REST[REST API v3]
        GRAPHQL[GraphQL API v4]
    end
    
    APP --> HTTP
    HTTP --> CACHE
    HTTP --> INTERCEPTOR
    INTERCEPTOR --> INTERNET
    INTERNET --> DNS
    INTERNET --> PROXY
    
    INTERNET --> LB
    LB --> API
    API --> AUTH
    API --> REST
    API --> GRAPHQL
```

## 3. 运行时架构图

### 3.1 进程架构
```mermaid
graph TB
    subgraph Android Process
        subgraph Main Process
            APP[Application]
            UI[Main Thread]
            BG[Background Threads]
            WORK[WorkManager]
        end
        
        subgraph Database Process
            ROOM[Room Database]
            QUERY[Query Threads]
        end
        
        subgraph Network Process
            RETRO[Retrofit]
            OKHTTP[OkHttp]
            DISPATCH[Dispatcher]
        end
    end
    
    subgraph System Services
        NOTIF[Notification Service]
        LOCATION[Location Service]
        NETWORK[Network Service]
    end
    
    UI --> BG
    BG --> ROOM
    BG --> RETRO
    WORK --> ROOM
    
    APP --> NOTIF
    APP --> LOCATION
    APP --> NETWORK
```

### 3.2 内存架构
```mermaid
graph LR
    subgraph Memory Layers
        HEAP[Java Heap]
        NATIVE[Native Heap]
        GRAPHICS[Graphics Memory]
    end
    
    subgraph Memory Components
        VIEWMODELS[ViewModels]
        REPOS[Repositories]
        IMAGES[Image Cache]
        DATABASE[Database Cache]
    end
    
    subgraph Garbage Collection
        GC[GC Roots]
        WEAK[Weak References]
        SOFT[Soft References]
    end
    
    VIEWMODELS --> HEAP
    REPOS --> HEAP
    IMAGES --> GRAPHICS
    DATABASE --> NATIVE
    
    GC --> VIEWMODELS
    WEAK --> IMAGES
    SOFT --> DATABASE
```

## 4. 安全架构图

### 4.1 认证架构
```mermaid
graph TB
    subgraph Authentication Flow
        USER[User]
        APP[App]
        WEBVIEW[Custom Tab]
        OAUTH[OAuth 2.0]
        TOKEN[Token Manager]
    end
    
    subgraph Token Storage
        ENCRYPT[Encrypted SharedPrefs]
        KEYSTORE[Android Keystore]
        BIOMETRIC[Biometric Auth]
    end
    
    subgraph API Security
        HTTPS[HTTPS/TLS 1.3]
        CERT[Certificate Pinning]
        INTERCEPT[Auth Interceptor]
    end
    
    USER --> APP
    APP --> WEBVIEW
    WEBVIEW --> OAUTH
    OAUTH --> TOKEN
    TOKEN --> ENCRYPT
    ENCRYPT --> KEYSTORE
    KEYSTORE --> BIOMETRIC
    
    TOKEN --> INTERCEPT
    INTERCEPT --> HTTPS
    HTTPS --> CERT
```

### 4.2 数据保护架构
```mermaid
graph LR
    subgraph Data at Rest
        DB_ENCRYPT[Database Encryption]
        FILE_ENCRYPT[File Encryption]
        PREFS_ENCRYPT[SharedPreferences Encryption]
    end
    
    subgraph Data in Transit
        TLS_13[TLS 1.3]
        CERT_PIN[Certificate Pinning]
        HSTS[HSTS Headers]
    end
    
    subgraph Key Management
        MASTER_KEY[Master Key]
        KEY_ALIAS[Key Alias]
        BIOMETRIC_KEY[Biometric Key]
    end
    
    MASTER_KEY --> DB_ENCRYPT
    MASTER_KEY --> FILE_ENCRYPT
    MASTER_KEY --> PREFS_ENCRYPT
    
    TLS_13 --> CERT_PIN
    CERT_PIN --> HSTS
    
    BIOMETRIC_KEY --> MASTER_KEY
    KEY_ALIAS --> MASTER_KEY
```

## 5. 性能架构图

### 5.1 缓存架构
```mermaid
graph TB
    subgraph Multi-Level Cache
        L1[Memory Cache<br/>LruCache]
        L2[Disk Cache<br/>DiskLruCache]
        L3[Network Cache<br/>HTTP Cache]
    end
    
    subgraph Cache Components
        IMAGE[Image Cache]
        API[API Response Cache]
        DB[Database Cache]
        CONFIG[Config Cache]
    end
    
    subgraph Cache Strategy
        TTL[TTL Strategy]
        LRU[LRU Eviction]
        SIZE[Size-based Eviction]
    end
    
    IMAGE --> L1
    IMAGE --> L2
    API --> L2
    API --> L3
    DB --> L2
    CONFIG --> L1
    
    TTL --> ALL[All Caches]
    LRU --> L1
    LRU --> L2
    SIZE --> L1
    SIZE --> L2
```

### 5.2 并发架构
```mermaid
graph LR
    subgraph Thread Pools
        MAIN[Main Thread]
        IO[IO Thread Pool]
        COMPUTE[Compute Thread Pool]
        NETWORK[Network Thread Pool]
    end
    
    subgraph Coroutines
        VIEW_SCOPE[ViewModelScope]
        LIFE_SCOPE[LifecycleScope]
        WORK_SCOPE[WorkManager Coroutine]
    end
    
    subgraph Synchronization
        MUTEX[Mutex Locks]
        CHANNEL[Channels]
        FLOW[Shared Flow]
    end
    
    VIEW_SCOPE --> MAIN
    LIFE_SCOPE --> IO
    WORK_SCOPE --> COMPUTE
    
    MUTEX --> FLOW
    CHANNEL --> FLOW
    NETWORK --> IO
```

## 6. 监控架构图

### 6.1 应用监控
```mermaid
graph TB
    subgraph Client Monitoring
        CRASH[Crash Reporting]
        PERF[Performance Metrics]
        USAGE[Usage Analytics]
    end
    
    subgraph Backend Monitoring
        API_METRICS[API Metrics]
        ERROR_TRACK[Error Tracking]
        HEALTH[Health Checks]
    end
    
    subgraph Alerting
        SLACK[Slack Alerts]
        EMAIL[Email Notifications]
        DASHBOARD[Dashboard]
    end
    
    CRASH --> API_METRICS
    PERF --> ERROR_TRACK
    USAGE --> HEALTH
    
    API_METRICS --> SLACK
    ERROR_TRACK --> EMAIL
    HEALTH --> DASHBOARD
```

### 6.2 日志架构
```mermaid
graph LR
    subgraph Logging Layers
        DEBUG[Debug Logs]
        INFO[Info Logs]
        WARN[Warning Logs]
        ERROR[Error Logs]
    end
    
    subgraph Log Destinations
        CONSOLE[Logcat]
        FILE[File Logger]
        REMOTE[Remote Logger]
        CRASHLYTICS[Crashlytics]
    end
    
    subgraph Log Processing
        FILTER[Log Filters]
        FORMAT[Log Formatters]
        ROTATE[Log Rotation]
    end
    
    DEBUG --> FILTER
    INFO --> FILTER
    WARN --> FILTER
    ERROR --> FILTER
    
    FILTER --> CONSOLE
    FILTER --> FILE
    FILE --> ROTATE
    ERROR --> REMOTE
    ERROR --> CRASHLYTICS
```

## 7. 测试架构图

### 7.1 测试金字塔架构
```mermaid
graph TB
    subgraph Test Layers
        E2E[E2E Tests<br/>~5%]
        INTEGRATION[Integration Tests<br/>~15%]
        UNIT[Unit Tests<br/>~80%]
    end
    
    subgraph Test Types
        UI[UI Tests<br/>Espresso]
        API[API Tests<br/>MockWebServer]
        DB[Database Tests<br/>Room]
        DOMAIN[Domain Logic Tests]
    end
    
    subgraph Test Tools
        JUNIT[JUnit 5]
        MOCKK[MockK]
        TURBINE[Turbine]
        ROBOLECTRIC[Robolectric]
    end
    
    E2E --> UI
    INTEGRATION --> API
    INTEGRATION --> DB
    UNIT --> DOMAIN
    
    UI --> JUNIT
    API --> MOCKK
    DB --> ROBOLECTRIC
    DOMAIN --> TURBINE
```

### 7.2 CI/CD架构
```mermaid
graph LR
    subgraph Development
        DEV[Developer]
        GIT[Git Repository]
        PR[Pull Request]
    end
    
    subgraph CI Pipeline
        BUILD[Gradle Build]
        TEST[Run Tests]
        LINT[Code Linting]
        SECURITY[Security Scan]
    end
    
    subgraph CD Pipeline
        STAGING[Staging Deploy]
        QA[QA Testing]
        PROD[Production Deploy]
    end
    
    DEV --> GIT
    GIT --> PR
    PR --> BUILD
    BUILD --> TEST
    TEST --> LINT
    LINT --> SECURITY
    SECURITY --> STAGING
    STAGING --> QA
    QA --> PROD
```

## 8. 国际化架构图

### 8.1 本地化架构
```mermaid
graph TB
    subgraph Localization
        STRINGS[String Resources]
        PLURALS[Plurals]
        FORMATS[Date/Number Formats]
        RTL[RTL Support]
    end
    
    subgraph Languages
        EN[English]
        ZH[中文]
        JA[日本語]
        KO[한국어]
        ES[Español]
    end
    
    subgraph Regional
        US[en-US]
        CN[zh-CN]
        JP[ja-JP]
        KR[ko-KR]
        ES_ES[es-ES]
    end
    
    STRINGS --> EN
    STRINGS --> ZH
    PLURALS --> JA
    FORMATS --> KO
    RTL --> ES
    
    EN --> US
    ZH --> CN
    JA --> JP
    KO --> KR
    ES --> ES_ES
```

---

**总结**: 这些组件图和部署图展示了GitHub Android App的完整技术架构，包括客户端架构、网络架构、安全架构、性能架构和部署架构。这些图表为开发团队提供了系统设计的全面视图。