# language: zh-CN
Feature: 用户认证
  GitHub Android App的用户认证功能测试

  Background:
    Given 应用已安装并启动
    And 网络连接正常

  @auth @login
  Scenario: 首次用户登录成功
    Given 用户未登录
    And 用户有GitHub账户
    When 用户点击"使用GitHub登录"按钮
    And 用户在浏览器中输入有效的用户名"testuser"和密码"testpass123"
    And 用户授权应用访问权限
    Then 用户成功登录
    And 显示主界面
    And 用户头像显示在导航栏
    And 本地存储包含用户认证信息

  @auth @login @invalid
  Scenario Outline: 登录失败-无效凭据
    Given 用户在登录页面
    When 用户输入用户名"<username>"和密码"<password>"
    Then 显示错误消息"<error_message>"
    And 用户停留在登录页面
    Examples:
      | username | password   | error_message        |
      | invalid  | testpass   | 用户名或密码错误     |
      | testuser | wrongpass  | 用户名或密码错误     |
      |          | testpass   | 用户名不能为空       |
      | testuser |            | 密码不能为空         |

  @auth @login @network
  Scenario: 登录失败-网络错误
    Given 用户在登录页面
    And 网络连接已断开
    When 用户尝试登录
    Then 显示错误消息"网络连接失败"
    And 显示"重试"按钮

  @auth @logout
  Scenario: 用户登出成功
    Given 用户已登录
    And 用户在主界面
    When 用户点击头像菜单
    And 选择"登出"选项
    And 确认登出操作
    Then 用户成功登出
    And 显示登录页面
    And 清除本地认证信息

  @auth @auto-login
  Scenario: 自动登录-有效令牌
    Given 用户之前登录过
    And 本地存储有效的认证令牌
    When 用户启动应用
    Then 应用自动登录
    And 跳过登录页面
    And 直接显示主界面

  @auth @auto-login @expired
  Scenario: 自动登录-令牌过期
    Given 用户之前登录过
    And 本地存储的令牌已过期
    When 用户启动应用
    Then 显示登录页面
    And 提示"请重新登录"
    And 清除过期令牌