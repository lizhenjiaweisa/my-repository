# language: zh-CN
Feature: 仓库搜索和浏览
  GitHub Android App的仓库搜索和浏览功能测试

  Background:
    Given 用户已登录
    And 网络连接正常

  @search @repository
  Scenario: 成功搜索仓库
    Given 用户在主界面
    And 搜索框可见
    When 用户在搜索框输入"android jetpack compose"
    And 点击搜索按钮
    Then 显示搜索结果列表
    And 结果包含"android-compose-samples"等仓库
    And 每个结果显示：
      | 字段     | 内容示例                  |
      | 仓库名   | android-compose-samples  |
      | 描述     | Jetpack Compose samples  |
      | 语言     | Kotlin                   |
      | 星标数   | 15,234                   |
      | 更新时间 | 2天前                    |

  @search @filter
  Scenario Outline: 搜索过滤条件
    Given 用户在搜索结果页面
    When 用户应用过滤条件
      | 过滤类型 | 选择值     |
      | 语言     | <language> |
      | 排序     | <sort_by>  |
    Then 结果按指定条件过滤
    And 显示应用的过滤标签
    Examples:
      | language | sort_by    |
      | Kotlin   | stars      |
      | Java     | updated    |
      | Python   | forks      |

  @search @empty
  Scenario: 搜索无结果
    Given 用户在搜索页面
    When 用户搜索"xyz123nonexistent"
    Then 显示"没有找到匹配的仓库"
    And 显示搜索建议
    And 提供"清除搜索"按钮

  @search @history
  Scenario: 搜索历史记录
    Given 用户之前搜索过"android"
    And 搜索过"kotlin"
    When 用户点击搜索框
    Then 显示搜索历史列表
    And 包含"android"和"kotlin"
    When 用户点击历史记录"android"
    Then 立即搜索"android"
    And 显示相关结果

  @repository @details
  Scenario: 查看仓库详情
    Given 用户在搜索结果页面
    When 用户点击"android-compose-samples"仓库
    Then 显示仓库详情页面
    And 显示以下信息：
      | 信息类型 | 示例内容                         |
      | 仓库名   | android-compose-samples        |
      | 描述     | Samples for Jetpack Compose      |
      | 语言     | Kotlin 100%                      |
      | 星标     | 15,234 ⭐                        |
      | 分支     | 2,345 🍴                         |
      | 问题     | 89 ❗                            |
      | 许可证   | Apache-2.0                      |
      | 最后更新 | 2天前                           |

  @repository @readme
  Scenario: 查看README文件
    Given 用户在仓库详情页面
    When 用户向下滑动到README部分
    Then 显示README.md内容
    And 正确渲染Markdown格式
    And 代码块有语法高亮
    And 图片正常显示

  @repository @files
  Scenario: 浏览仓库文件
    Given 用户在仓库详情页面
    When 用户点击"文件"选项卡
    Then 显示仓库文件结构
    And 显示目录层级：
      | 路径             | 类型   |
      | /                | 目录   |
      | /README.md       | 文件   |
      | /app/            | 目录   |
      | /app/build.gradle| 文件   |
    When 用户点击"app"目录
    Then 显示app目录下的文件和子目录

  @repository @refresh
  Scenario: 下拉刷新仓库信息
    Given 用户在仓库详情页面
    When 用户下拉页面
    Then 显示刷新指示器
    And 更新仓库信息
    And 隐藏刷新指示器
    And 显示"已更新"提示

  @repository @offline
  Scenario: 离线查看缓存的仓库
    Given 用户之前查看过该仓库
    And 网络连接已断开
    When 用户打开仓库详情
    Then 显示缓存的仓库信息
    And 显示"离线模式"提示
    And 禁用需要网络的功能

  @repository @error
  Scenario: 仓库加载错误
    Given 用户尝试查看仓库
    When GitHub API返回404错误
    Then 显示"仓库不存在或已被删除"
    And 提供"返回"按钮
    And 记录错误日志