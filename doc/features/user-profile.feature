# language: zh-CN
Feature: 用户资料管理
  GitHub Android App的用户资料和关注功能测试

  Background:
    Given 用户已登录
    And 网络连接正常

  @profile @view
  Scenario: 查看用户资料
    Given 用户在仓库详情页面
    When 用户点击作者头像"octocat"
    Then 显示用户资料页面
    And 显示以下用户信息：
      | 信息类型 | 示例内容               |
      | 头像     | octocat头像           |
      | 用户名   | octocat               |
      | 全名     | The Octocat           |
      | 简介     | GitHub官方吉祥物       |
      | 位置     | San Francisco, CA    |
      | 公司     | GitHub                |
      | 网站     | https://github.blog   |
      | 关注者   | 3,456 关注者          |
      | 正在关注 | 123 正在关注          |

  @profile @repositories
  Scenario: 查看用户仓库
    Given 用户在用户资料页面
    When 用户点击"仓库"选项卡
    Then 显示用户的公开仓库列表
    And 按最后更新时间排序
    And 每个仓库显示：
      | 字段   | 内容示例        |
      | 名称   | hello-world    |
      | 描述   | My first repository |
      | 语言   | JavaScript     |
      | 星标   | 150 ⭐         |
      | 分支   | 50 🍴          |

  @profile @contributions
  Scenario: 查看用户贡献
    Given 用户在用户资料页面
    When 用户点击"贡献"选项卡
    Then 显示贡献图表
    And 显示过去一年的贡献活动
    And 显示贡献统计：
      | 统计类型 | 数值  |
      | 总贡献   | 1,234 |
      | 最长连续 | 45天  |
      | 当前连续 | 12天  |

  @profile @followers
  Scenario: 查看关注列表
    Given 用户在用户资料页面
    When 用户点击"关注者"链接
    Then 显示关注者列表
    And 每个关注者显示：
      | 信息   | 内容示例   |
      | 头像   | 用户头像   |
      | 用户名 | follower1  |
      | 全名   | John Doe   |
      | 简介   | Developer  |

  @profile @following
  Scenario: 查看正在关注的用户
    Given 用户在用户资料页面
    When 用户点击"正在关注"链接
    Then 显示正在关注的用户列表
    And 列表信息与关注者列表格式一致

  @profile @follow
  Scenario: 关注用户
    Given 用户查看其他用户资料
    And 用户未关注该用户
    When 用户点击"关注"按钮
    Then 按钮文本变为"已关注"
    And 关注者数量增加1
    And 显示成功提示"已关注用户"

  @profile @unfollow
  Scenario: 取消关注用户
    Given 用户查看已关注的用户资料
    And 用户已关注该用户
    When 用户点击"已关注"按钮
    And 确认取消关注
    Then 按钮文本变为"关注"
    And 关注者数量减少1
    And 显示成功提示"已取消关注"

  @profile @self
  Scenario: 查看自己的资料
    Given 用户查看自己的用户名"myuser"
    When 用户导航到个人资料页面
    Then 显示"编辑资料"按钮
    And 不显示"关注"按钮
    And 显示私密仓库数量

  @profile @private
  Scenario: 查看私密信息权限
    Given 用户查看其他用户资料
    When 用户尝试查看私密仓库
    Then 只显示公开仓库
    And 不显示私密仓库信息
    And 显示"仅显示公开仓库"提示

  @profile @not-found
  Scenario: 用户不存在
    Given 用户尝试查看不存在的用户"nonexistentuser123"
    When 系统加载用户资料
    Then 显示"用户不存在"错误页面
    And 提供"返回首页"按钮
    And 记录错误日志

  @profile @refresh
  Scenario: 刷新用户资料
    Given 用户在用户资料页面
    When 用户下拉页面
    Then 显示刷新指示器
    And 更新用户资料信息
    And 更新仓库列表
    And 隐藏刷新指示器

  @profile @offline
  Scenario: 离线查看缓存的用户资料
    Given 用户之前查看过该用户资料
    And 网络连接已断开
    When 用户打开用户资料页面
    Then 显示缓存的用户信息
    And 显示"离线模式"提示
    And 禁用需要网络的功能

  @profile @error
  Scenario: 用户资料加载错误
    Given 用户尝试查看用户资料
    When GitHub API返回500错误
    Then 显示"加载失败，请重试"
    And 提供"重试"按钮
    And 保留缓存的数据