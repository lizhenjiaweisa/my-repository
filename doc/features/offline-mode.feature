# language: zh-CN
Feature: 离线模式
  GitHub Android App的离线功能和网络状态处理测试

  Background:
    Given 用户已登录

  @offline @cache
  Scenario: 离线查看缓存的仓库列表
    Given 用户之前浏览过仓库列表
    And 网络连接已断开
    When 用户打开应用
    Then 显示缓存的仓库列表
    And 显示"离线模式"提示
    And 每个仓库显示缓存时的信息
    And 禁用需要网络的操作

  @offline @repository
  Scenario: 离线查看仓库详情
    Given 用户之前查看过"android-compose-samples"仓库
    And 网络连接已断开
    When 用户再次打开该仓库
    Then 显示缓存的仓库详情
    And 显示README缓存内容
    And 显示"最后更新于2小时前"提示
    And 禁用"关注"和"星标"按钮

  @offline @user-profile
  Scenario: 离线查看用户资料
    Given 用户之前查看过用户"octocat"的资料
    And 网络连接已断开
    When 用户再次查看该用户
    Then 显示缓存的用户资料
    And 显示缓存的用户仓库列表
    And 显示"离线查看"提示

  @offline @issues
  Scenario: 离线查看问题列表
    Given 用户之前查看过某仓库的问题列表
    And 网络连接已断开
    When 用户再次查看该仓库的问题
    Then 显示缓存的问题列表
    And 显示问题详情缓存
    And 禁用创建和编辑功能

  @network @reconnect
  Scenario: 网络重新连接
    Given 应用处于离线模式
    And 用户正在查看缓存内容
    When 网络重新连接
    Then 显示"网络已恢复"提示
    And 自动刷新当前页面
    And 更新缓存数据
    And 重新启用网络功能

  @network @intermittent
  Scenario: 间歇性网络连接
    Given 用户正在浏览内容
    When 网络连接不稳定
    Then 显示"网络连接不稳定"提示
    And 使用缓存数据继续显示
    And 在后台尝试重新连接
    And 成功连接后自动更新

  @cache @expiration
  Scenario: 缓存数据过期
    Given 缓存数据已超过24小时
    And 网络连接已断开
    When 用户尝试查看缓存内容
    Then 显示"数据可能已过期"警告
    And 显示最后更新时间
    And 提供"重试连接"选项

  @cache @storage
  Scenario: 缓存存储限制
    Given 设备存储空间不足
    When 应用尝试缓存新数据
    Then 自动清理最旧的缓存
    And 保留最近访问的内容
    And 显示"已清理缓存"提示

  @cache @manual-refresh
  Scenario: 手动刷新缓存
    Given 用户在网络恢复后
    And 正在查看缓存内容
    When 用户下拉刷新
    Then 强制更新缓存数据
    And 显示"正在更新..."指示器
    And 更新完成后显示"已更新"

  @offline @search
  Scenario: 离线搜索限制
    Given 网络连接已断开
    When 用户尝试搜索仓库
    Then 显示"离线模式无法搜索"提示
    And 提供查看历史搜索选项
    And 显示最近搜索的缓存结果

  @offline @error-handling
  Scenario: 离线模式下的错误处理
    Given 网络连接已断开
    And 没有相关缓存数据
    When 用户尝试查看未缓存的内容
    Then 显示"无可用数据"提示
    And 提供"查看其他内容"选项
    And 建议连接网络后重试