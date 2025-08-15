# language: zh-CN
Feature: 问题管理
  GitHub Android App的问题(Issues)管理功能测试

  Background:
    Given 用户已登录
    And 网络连接正常

  @issues @list
  Scenario: 查看仓库问题列表
    Given 用户在仓库详情页面
    When 用户点击"问题"选项卡
    Then 显示问题列表页面
    And 默认显示"开放"状态的问题
    And 每个问题显示：
      | 字段     | 示例内容               |
      | 标题     | Fix login bug         |
      | 编号     | #123                   |
      | 状态     | 开放                   |
      | 作者     | octocat               |
      | 时间     | 2天前                 |
      | 标签     | bug, high-priority     |
      | 评论数   | 5条评论               |

  @issues @filter
  Scenario Outline: 筛选问题
    Given 用户在问题列表页面
    When 用户选择"<filter_type>"为"<filter_value>"
    Then 显示符合筛选条件的问题
    And 显示应用的筛选标签
    Examples:
      | filter_type | filter_value |
      | 状态        | 已关闭       |
      | 作者        | octocat     |
      | 标签        | bug         |
      | 排序        | 最新        |

  @issues @search
  Scenario: 搜索问题
    Given 用户在问题列表页面
    When 用户在搜索框输入"login"
    And 点击搜索按钮
    Then 显示包含"login"的问题
    And 高亮显示匹配的文本
    And 显示搜索结果数量

  @issues @details
  Scenario: 查看问题详情
    Given 用户在问题列表页面
    When 用户点击问题"#123"
    Then 显示问题详情页面
    And 显示以下详细信息：
      | 信息类型 | 示例内容                          |
      | 标题     | Fix login button not responding   |
      | 描述     | When clicking the login button... |
      | 作者     | octocat                          |
      | 创建时间 | 2024-01-15 10:30                 |
      | 状态     | 开放                              |
      | 标签     | bug, high-priority               |
      | 指派给   | developer1                       |
      | 里程碑   | v2.0                             |

  @issues @comments
  Scenario: 查看问题评论
    Given 用户在问题详情页面
    When 用户向下滑动到评论区域
    Then 显示所有评论
    And 每个评论显示：
      | 字段   | 示例内容              |
      | 作者   | developer2           |
      | 头像   | 用户头像              |
      | 时间   | 1小时前               |
      | 内容   | I can reproduce this... |
      | 反应   | 👍 5, ❤️ 2            |

  @issues @create
  Scenario: 创建新问题
    Given 用户在仓库详情页面
    And 用户有权限创建问题
    When 用户点击"新建问题"按钮
    Then 显示创建问题表单
    And 表单包含以下字段：
      | 字段   | 类型     | 必填 |
      | 标题   | 文本输入 | 是   |
      | 描述   | 文本区域 | 否   |
      | 标签   | 多选     | 否   |
      | 指派给 | 下拉选择 | 否   |
      | 里程碑 | 下拉选择 | 否   |

  @issues @create-success
  Scenario: 成功创建问题
    Given 用户在创建问题页面
    When 用户填写标题"发现新的UI bug"
    And 填写描述"在黑暗模式下，按钮文字不可见"
    And 选择标签"bug"和"ui"
    And 点击"提交新问题"按钮
    Then 成功创建问题
    And 显示成功提示"问题已创建"
    And 跳转到新创建的问题页面
    And 问题编号为"#124"

  @issues @validation
  Scenario Outline: 创建问题表单验证
    Given 用户在创建问题页面
    When 用户尝试提交无效数据
    Then 显示相应的错误消息
    Examples:
      | 条件         | 错误消息         |
      | 标题为空     | 标题不能为空     |
      | 标题过长     | 标题不能超过256字符 |
      | 描述过长     | 描述不能超过4000字符 |

  @issues @edit
  Scenario: 编辑问题
    Given 用户是问题的创建者
    And 用户在问题详情页面
    When 用户点击"编辑"按钮
    Then 显示编辑表单
    And 预填充当前问题信息
    When 用户修改标题为"更新：登录问题已修复"
    And 点击"保存更改"按钮
    Then 成功更新问题
    And 显示更新提示
    And 页面显示更新后的信息

  @issues @close
  Scenario: 关闭问题
    Given 用户有权限关闭问题
    And 用户在问题详情页面
    When 用户点击"关闭问题"按钮
    And 选择关闭原因"已修复"
    And 添加关闭评论"已在v2.1版本中修复"
    And 确认关闭操作
    Then 问题状态变为"已关闭"
    And 显示关闭时间和关闭者
    And 显示关闭评论

  @issues @reopen
  Scenario: 重新开放问题
    Given 问题状态为"已关闭"
    And 用户有权限重新开放
    When 用户点击"重新开放"按钮
    And 添加重开评论"问题仍然存在"
    And 确认重开操作
    Then 问题状态变为"开放"
    And 显示重开时间和操作者

  @issues @assign
  Scenario: 指派问题给开发者
    Given 用户是仓库维护者
    And 用户在问题详情页面
    When 用户点击"指派给"下拉菜单
    And 选择开发者"developer1"
    Then 问题被指派给developer1
    And developer1收到通知
    And 显示指派记录

  @issues @label
  Scenario: 添加和移除标签
    Given 用户在问题详情页面
    When 用户点击"标签"编辑按钮
    And 选择新标签"enhancement"
    And 取消选择标签"bug"
    And 保存标签更改
    Then 问题显示新的标签组合
    And 显示标签变更记录

  @issues @reaction
  Scenario: 对问题和评论添加反应
    Given 用户在问题详情页面
    When 用户点击问题描述下方的"👍"反应按钮
    Then 反应计数增加1
    And 按钮显示为已选中状态
    When 用户再次点击"👍"按钮
    Then 反应计数减少1
    And 按钮恢复未选中状态

  @issues @empty
  Scenario: 空问题列表
    Given 仓库没有任何问题
    When 用户查看问题列表
    Then 显示"该仓库没有问题"
    And 显示"创建第一个问题"按钮
    And 提供问题创建指南

  @issues @pagination
  Scenario: 问题列表分页加载
    Given 仓库有超过30个问题
    When 用户滚动到列表底部
    Then 自动加载下一页问题
    And 显示加载指示器
    And 平滑添加新内容
    And 保持滚动位置

  @issues @offline
  Scenario: 离线查看缓存的问题
    Given 用户之前查看过问题列表
    And 网络连接已断开
    When 用户打开问题列表
    Then 显示缓存的问题列表
    And 显示"离线模式"提示
    And 禁用创建和编辑功能

  @issues @error
  Scenario: 问题加载错误
    Given 用户尝试查看问题
    When GitHub API返回500错误
    Then 显示"加载失败，请重试"
    And 提供"重试"按钮
    And 保留缓存的问题数据