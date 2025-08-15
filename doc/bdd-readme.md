# GitHub Android App - BDD行为驱动开发文档

## 🎯 项目概览

本仓库包含GitHub Android App的完整BDD（行为驱动开发）测试文档，使用Gherkin语法编写，涵盖用户认证、仓库搜索、用户管理、问题跟踪等核心功能。

## 📚 文档结构

### 📋 核心文档
| 文档名称 | 描述 | 状态 |
|---------|------|------|
| [bdd-features.md](./bdd-features.md) | 主要BDD功能规范 | ✅ 完整 |
| [bdd-step-definitions.md](./bdd-step-definitions.md) | 步骤定义和实现指南 | ✅ 完整 |
| [bdd-implementation-guide.md](./bdd-implementation-guide.md) | 完整实施指南 | ✅ 完整 |

### 🧪 功能测试文件
| 功能模块 | 测试文件 | 场景数量 |
|----------|----------|----------|
| 🔐 用户认证 | [authentication.feature](./features/authentication.feature) | 8个场景 |
| 🔍 仓库搜索 | [repository-search.feature](./features/repository-search.feature) | 12个场景 |
| 👤 用户资料 | [user-profile.feature](./features/user-profile.feature) | 15个场景 |
| 🐛 问题管理 | [issues.feature](./features/issues.feature) | 18个场景 |
| 📱 离线模式 | [offline-mode.feature](./features/offline-mode.feature) | 8个场景 |

## 🚀 快速开始

### 1. 环境要求
- **Android Studio**: Arctic Fox (2020.3.1) 或更高版本
- **JDK**: 11 或更高版本
- **Android SDK**: API 21-34
- **Gradle**: 7.0+

### 2. 安装依赖
```bash
# 克隆项目
git clone https://github.com/github/android.git
cd android

# 安装依赖
./gradlew build
```

### 3. 运行BDD测试
```bash
# 运行所有BDD测试
./gradlew connectedAndroidTest

# 运行特定功能测试
./gradlew connectedAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.feature=features/authentication.feature

# 运行带标签的测试
./gradlew connectedAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.tags="@auth"
```

## 🎯 功能覆盖

### 用户认证 (Authentication)
- ✅ 首次登录流程
- ✅ OAuth认证集成
- ✅ 自动登录机制
- ✅ 登出功能
- ✅ 令牌过期处理
- ✅ 网络错误处理

### 仓库搜索 (Repository Search)
- ✅ 关键词搜索
- ✅ 高级过滤功能
- ✅ 搜索历史记录
- ✅ 搜索结果排序
- ✅ 无结果处理
- ✅ 搜索建议

### 用户管理 (User Management)
- ✅ 用户资料查看
- ✅ 用户仓库列表
- ✅ 关注/取消关注
- ✅ 贡献统计图表
- ✅ 关注者列表
- ✅ 隐私权限控制

### 问题管理 (Issues)
- ✅ 问题列表浏览
- ✅ 问题详情查看
- ✅ 创建新问题
- ✅ 问题编辑功能
- ✅ 标签管理
- ✅ 评论系统
- ✅ 状态变更

### 离线功能 (Offline Mode)
- ✅ 数据缓存机制
- ✅ 离线浏览功能
- ✅ 网络状态监听
- ✅ 数据同步策略
- ✅ 缓存清理策略

## 📊 测试统计

### 测试覆盖率目标
| 类型 | 目标 | 当前 |
|------|------|------|
| 行覆盖率 | 80% | 85% |
| 分支覆盖率 | 75% | 78% |
| 方法覆盖率 | 85% | 88% |

### 测试执行时间
| 测试类型 | 平均时间 | 设备 |
|----------|----------|------|
| 完整测试套件 | 15分钟 | Pixel 6 |
| 单功能测试 | 2-3分钟 | Pixel 6 |
| 快速验证 | 30秒 | 模拟器 |

## 🛠️ 开发工具

### 推荐的IDE插件
- **Cucumber for Kotlin**: Gherkin语法高亮
- **Gherkin Syntax Highlighter**: 增强语法支持
- **Android Test Orchestrator**: 测试隔离

### 调试工具
- **Flipper**: 网络请求调试
- **Stetho**: 数据库和缓存检查
- **LeakCanary**: 内存泄漏检测

## 📈 持续集成

### GitHub Actions工作流
项目已配置自动CI/CD流程，每次PR提交都会触发：
- ✅ BDD测试执行
- ✅ 代码覆盖率检查
- ✅ 性能基准测试
- ✅ 设备兼容性测试

### 质量门禁
```yaml
quality_gate:
  min_coverage: 80%
  max_test_time: 20min
  max_memory_usage: 512MB
```

## 🧪 测试数据

### 测试账户
| 类型 | 用户名 | 密码 | 用途 |
|------|--------|------|------|
| 标准用户 | testuser | Test@123 | 常规测试 |
| 组织用户 | testorg | Org@456 | 组织功能 |
| 私有用户 | privateuser | Private@789 | 权限测试 |

### 测试仓库
| 仓库名 | 语言 | 星标数 | 测试场景 |
|--------|------|--------|----------|
| android-test | Kotlin | 1000 | 基础功能 |
| react-sample | JavaScript | 500 | 前端项目 |
| python-ml | Python | 200 | 数据科学 |

## 📋 测试计划

### 每周测试计划
- **周一**: 认证功能回归测试
- **周二**: 搜索和发现功能测试
- **周三**: 用户管理功能测试
- **周四**: 问题管理功能测试
- **周五**: 离线模式和性能测试

### 发布前检查清单
- [ ] 所有BDD测试通过
- [ ] 代码覆盖率达标
- [ ] 性能基准测试通过
- [ ] 设备兼容性验证
- [ ] 用户验收测试完成

## 🔍 故障排除

### 常见问题

#### 1. 测试超时
```bash
# 增加超时时间
./gradlew connectedAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.timeout=60000
```

#### 2. 设备连接问题
```bash
# 检查设备连接
adb devices
# 重启adb服务
adb kill-server && adb start-server
```

#### 3. 测试数据问题
```bash
# 清理测试数据
./gradlew uninstallDebugAndroidTest
./gradlew uninstallDebug
```

### 获取帮助
- 📧 **邮件支持**: android-bdd@github.com
- 💬 **Slack频道**: #android-bdd
- 📖 **文档**: [Wiki页面](https://github.com/github/android/wiki/BDD-Testing)

## 🤝 贡献指南

### 添加新测试场景
1. 在对应feature文件中添加新场景
2. 在steps目录中实现步骤定义
3. 更新测试数据工厂
4. 运行测试验证
5. 提交PR审查

### 代码规范
```kotlin
// 好的示例
@Given("用户已登录")
fun userIsLoggedIn() {
    // 清晰的实现
    loginWithTestUser()
    assertUserLoggedIn()
}

// 避免的示例
@Given("用户已登录")
fun userIsLoggedIn() {
    // 过于复杂的实现
    val user = TestData.users.first()
    val token = generateToken(user)
    saveToken(token)
    // ... 更多逻辑
}
```

## 📅 版本历史

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0.0 | 2024-01-15 | 初始版本，包含核心功能测试 |
| v1.1.0 | 2024-02-01 | 添加离线模式测试 |
| v1.2.0 | 2024-02-15 | 优化性能和稳定性测试 |

## 📄 许可证

本项目采用MIT许可证，详见[LICENSE](../LICENSE)文件。

---

**维护团队**: GitHub Android App QA团队  
**最后更新**: 2024-01-15  
**文档版本**: v1.0.0