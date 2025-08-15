# GitHub Android App - 项目结构图

## 1. 整体项目结构

### 1.1 目录结构树形图
```
Github/
├── 📁 .gradle/
│   ├── 8.2/
│   ├── buildOutputCleanup/
│   └── vcs-1/
├── 📁 .idea/
│   ├── AndroidProjectSystem.xml
│   ├── gradle.xml
│   ├── misc.xml
│   └── workspace.xml
├── 📁 app/
│   ├── 📁 src/
│   │   ├── 📁 main/
│   │   │   ├── 📁 java/com/github/app/
│   │   │   │   ├── 📁 data/
│   │   │   │   │   ├── 📁 model/
│   │   │   │   │   ├── 📁 remote/
│   │   │   │   │   ├── 📁 repository/
│   │   │   │   │   └── 📁 local/
│   │   │   │   ├── 📁 domain/
│   │   │   │   │   ├── 📁 model/
│   │   │   │   │   ├── 📁 repository/
│   │   │   │   │   └── 📁 usecase/
│   │   │   │   ├── 📁 presentation/
│   │   │   │   │   ├── 📁 component/
│   │   │   │   │   ├── 📁 navigation/
│   │   │   │   │   ├── 📁 screen/
│   │   │   │   │   ├── 📁 theme/
│   │   │   │   │   └── 📁 viewmodel/
│   │   │   │   ├── 📁 di/
│   │   │   │   └── GitHubApplication.kt
│   │   │   ├── 📁 res/
│   │   │   │   ├── 📁 drawable/
│   │   │   │   ├── 📁 values/
│   │   │   │   └── 📁 xml/
│   │   │   └── AndroidManifest.xml
│   │   ├── 📁 test/
│   │   └── 📁 androidTest/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── 📁 doc/
│   ├── 需求文档.md
│   ├── uml-class-diagram.md
│   ├── uml-sequence-diagram.md
│   └── uml-component-deployment.md
├── 📁 gradle/
│   └── 📁 wrapper/
├── 📁 build/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── local.properties
└── README.md
```

### 1.2 模块结构可视化
```mermaid
graph TD
    subgraph Root Project
        A[GitHub Android App]
    end
    
    subgraph App Module
        B[app]
        B1[src/main]
        B2[src/test]
        B3[src/androidTest]
    end
    
    subgraph Source Structure
        C1[data]
        C2[domain]
        C3[presentation]
        C4[di]
        C5[GitHubApplication.kt]
    end
    
    subgraph Data Layer
        D1[model]
        D2[remote]
        D3[repository]
        D4[local]
    end
    
    subgraph Domain Layer
        E1[model]
        E2[repository]
        E3[usecase]
    end
    
    subgraph Presentation Layer
        F1[component]
        F2[navigation]
        F3[screen]
        F4[theme]
        F5[viewmodel]
    end
    
    A --> B
    B --> B1
    B1 --> C1
    B1 --> C2
    B1 --> C3
    B1 --> C4
    B1 --> C5
    
    C1 --> D1
    C1 --> D2
    C1 --> D3
    C1 --> D4
    
    C2 --> E1
    C2 --> E2
    C2 --> E3
    
    C3 --> F1
    C3 --> F2
    C3 --> F3
    C3 --> F4
    C3 --> F5
```

## 2. 代码结构详细分析

### 2.1 数据层结构
```mermaid
graph LR
    subgraph Data Layer
        subgraph Model
            M1[Repository.kt]
            M2[User.kt]
            M3[Issue.kt]
            M4[AuthToken.kt]
        end
        
        subgraph Remote
            R1[GitHubApiService.kt]
            R2[AuthApiService.kt]
            R3[ApiResponse.kt]
            R4[NetworkModule.kt]
        end
        
        subgraph Repository
            RE1[RepositoryRepository.kt]
            RE2[UserRepository.kt]
            RE3[IssueRepository.kt]
            RE4[AuthRepository.kt]
        end
        
        subgraph Local
            L1[AppDatabase.kt]
            L2[RepositoryDao.kt]
            L3[UserDao.kt]
            L4[DataStoreManager.kt]
        end
    end
    
    M1 --> RE1
    M2 --> RE2
    M3 --> RE3
    M4 --> RE4
    
    R1 --> RE1
    R2 --> RE4
    
    L1 --> RE1
    L1 --> RE2
    L1 --> RE3
    L4 --> RE4
```

### 2.2 领域层结构
```mermaid
graph LR
    subgraph Domain Layer
        subgraph Model
            DM1[Repository.kt]
            DM2[User.kt]
            DM3[Issue.kt]
        end
        
        subgraph Repository Interface
            RI1[IRepositoryRepository.kt]
            RI2[IUserRepository.kt]
            RI3[IIssueRepository.kt]
            RI4[IAuthRepository.kt]
        end
        
        subgraph Use Cases
            UC1[GetRepositoriesUseCase.kt]
            UC2[SearchRepositoriesUseCase.kt]
            UC3[GetUserProfileUseCase.kt]
            UC4[CreateIssueUseCase.kt]
        end
    end
    
    DM1 --> UC1
    DM1 --> UC2
    DM2 --> UC3
    DM3 --> UC4
    
    RI1 --> UC1
    RI1 --> UC2
    RI2 --> UC3
    RI3 --> UC4
```

### 2.3 表示层结构
```mermaid
graph TD
    subgraph Presentation Layer
        subgraph Components
            C1[RepositoryCard.kt]
            C2[UserAvatar.kt]
            C3[SearchBar.kt]
            C4[LoadingIndicator.kt]
        end
        
        subgraph Screens
            S1[HomeScreen.kt]
            S2[RepositoryDetailScreen.kt]
            S3[UserProfileScreen.kt]
            S4[SearchScreen.kt]
            S5[AuthScreen.kt]
        end
        
        subgraph ViewModels
            VM1[RepositoryViewModel.kt]
            VM2[UserViewModel.kt]
            VM3[IssueViewModel.kt]
            VM4[AuthViewModel.kt]
        end
        
        subgraph Navigation
            N1[AppNavigation.kt]
            N2[NavigationRoutes.kt]
            N3[NavigationModule.kt]
        end
        
        subgraph Theme
            T1[AppTheme.kt]
            T2[Color.kt]
            T3[Typography.kt]
            T4[Shape.kt]
        end
    end
    
    C1 --> S1
    C1 --> S2
    C2 --> S3
    C3 --> S1
    C3 --> S4
    
    S1 --> VM1
    S2 --> VM1
    S3 --> VM2
    S4 --> VM1
    S5 --> VM4
    
    N1 --> S1
    N1 --> S2
    N1 --> S3
    N1 --> S4
    N1 --> S5
```

## 3. 配置文件结构

### 3.1 Gradle配置结构
```mermaid
graph TD
    subgraph Build Configuration
        A[build.gradle.kts (Project)]
        B[build.gradle.kts (App)]
        C[settings.gradle.kts]
        D[gradle.properties]
        E[local.properties]
    end
    
    subgraph Dependencies
        F[Jetpack Compose]
        G[Hilt]
        H[Retrofit]
        I[Room]
        J[Coil]
        K[Testing Libraries]
    end
    
    subgraph Build Types
        L[Debug]
        M[Release]
        N[Staging]
    end
    
    A --> B
    C --> B
    D --> B
    E --> B
    
    B --> F
    B --> G
    B --> H
    B --> I
    B --> J
    B --> K
    
    B --> L
    B --> M
    B --> N
```

### 3.2 清单文件结构
```mermaid
graph TD
    subgraph AndroidManifest.xml
        A[Application Declaration]
        B[Permissions]
        C[Activities]
        D[Services]
        E[Intent Filters]
    end
    
    subgraph Permissions
        P1[INTERNET]
        P2[ACCESS_NETWORK_STATE]
    end
    
    subgraph Activities
        ACT1[MainActivity]
        ACT2[AuthActivity]
    end
    
    subgraph Intent Filters
        IF1[Main Launcher]
        IF2[OAuth Callback]
    end
    
    A --> B
    A --> C
    C --> ACT1
    C --> ACT2
    ACT1 --> IF1
    ACT2 --> IF2
    B --> P1
    B --> P2
```

## 4. 测试结构

### 4.1 测试目录结构
```mermaid
graph TD
    subgraph Test Structure
        A[test/]
        B[androidTest/]
    end
    
    subgraph Unit Tests
        UT1[data/]
        UT2[domain/]
        UT3[presentation/]
    end
    
    subgraph Integration Tests
        IT1[api/]
        IT2[database/]
        IT3[repository/]
    end
    
    subgraph UI Tests
        UIT1[screens/]
        UIT2[navigation/]
        UIT3[components/]
    end
    
    A --> UT1
    A --> UT2
    A --> UT3
    
    B --> IT1
    B --> IT2
    B --> IT3
    
    B --> UIT1
    B --> UIT2
    B --> UIT3
```

### 4.2 测试覆盖率
```mermaid
pie title 测试覆盖率分布
    "单元测试" : 70
    "集成测试" : 20
    "UI测试" : 10
```

## 5. 构建输出结构

### 5.1 构建产物
```mermaid
graph TD
    subgraph Build Outputs
        A[build/]
        B[outputs/]
        C[reports/]
        D[test-results/]
    end
    
    subgraph APK Files
        E[debug/app-debug.apk]
        F[release/app-release.apk]
        G[staging/app-staging.apk]
    end
    
    subgraph Reports
        H[lint-results.html]
        I[jacocoTestReport/]
        J[test-report/]
    end
    
    A --> B
    B --> E
    B --> F
    B --> G
    
    A --> C
    C --> H
    C --> I
    
    A --> D
    D --> J
```

## 6. 依赖管理结构

### 6.1 第三方库依赖
```mermaid
graph LR
    subgraph Core Libraries
        A[AndroidX Core]
        B[Jetpack Compose]
        C[Kotlin Coroutines]
    end
    
    subgraph Network
        D[Retrofit]
        E[OkHttp]
        F[Gson]
    end
    
    subgraph Database
        G[Room]
        H[DataStore]
    end
    
    subgraph DI & Testing
        I[Hilt]
        J[JUnit]
        K[MockK]
        L[Espresso]
    end
    
    subgraph UI
        M[Coil]
        N[Material 3]
        O[Navigation]
    end
    
    A --> B
    A --> C
    D --> E
    D --> F
    G --> H
    I --> J
    J --> K
    K --> L
    B --> M
    B --> N
    B --> O
```

## 7. 版本控制结构

### 7.1 Git分支策略
```mermaid
graph LR
    subgraph Branches
        MAIN[main]
        DEV[develop]
        FEATURE[feature/*]
        HOTFIX[hotfix/*]
        RELEASE[release/*]
    end
    
    subgraph Workflow
        A[Feature Development]
        B[Code Review]
        C[Integration]
        D[Release]
    end
    
    MAIN --> DEV
    DEV --> FEATURE
    FEATURE --> B
    B --> DEV
    DEV --> C
    C --> RELEASE
    RELEASE --> MAIN
    
    MAIN --> HOTFIX
    HOTFIX --> MAIN
```

---

**总结**: 本项目采用清晰的分层架构，遵循Clean Architecture原则，将代码分为数据层、领域层和表示层。每个层次都有明确的职责和边界，便于维护和测试。