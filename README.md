# SimpleChat

一个简单但功能丰富的 Minecraft 聊天管理插件。

## 功能
- 聊天颜色代码支持
- 物品展示功能 ([i] 或 [item])
- 玩家禁言系统
- 告示牌颜色代码
- 铁砧颜色代码
- PlaceholderAPI 支持
- @玩家提醒功能

## 命令
- `/mute <玩家> <时长> <原因>` - 禁言玩家
  - 时长格式: 1s(秒), 1m(分钟), 1h(小时), 1d(天), 1w(周), 1mo(月), 1y(年)
- `/unmute <玩家>` - 解除玩家禁言

## 权限
- `simplechat.chat.color` - 允许在聊天中使用颜色代码
- `simplechat.chat.mentionall` - 允许使用 @all 功能
- `simplechat.sign.color` - 允许在告示牌上使用颜色代码
- `simplechat.anvil.color` - 允许在铁砧上使用颜色代码
- `simplechat.mute` - 允许使用禁言相关命令

## 配置
插件配置文件位于 `plugins/SimpleChat/config.yml`

## 依赖
- PlaceholderAPI (可选)

## 支持
- 支持 Folia
- 适配 1.21+ 
