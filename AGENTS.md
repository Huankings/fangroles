# AGENTS.md — fangroles 开发协作规范（中文）

本文件是 **fangroles**（基于自改版 Wathe + HarpyModLoader + NoellesRoles 的扩展职业 Mod）的协作规则。
后续接到「新增职业、改职业机制、修 bug、接口公开化、编译联调」等任务时，**先按这里的路径和流程读源码，再决定修改范围**。

---

## 一、当前有效路径

旧提示词或旧 txt 里出现的其它路径只作为历史语境；除非用户明确要求参考旧版，否则以本表为准。

| 项目 | 路径 | 用途 |
| --- | --- | --- |
| **fangroles（本工程）** | `D:\哈比快车最新源码\fangroles\fangroles` | 当前主要修改目标。新增职业落在这里 |
| fangroles 需求 txt | `D:\哈比快车最新源码\fangroles\文档txt` | 用户需求、提示词、复盘材料 |
| Wathe 自改版 | `D:\哈比快车最新源码\wathe\Wathe - 副本1` | MC 1.21.1 Fabric 类狼人杀玩法本体；公共 API 与核心机制都在这里 |
| Wathe 需求 txt | `D:\哈比快车最新源码\wathe\txt要求` | Wathe 历史需求与通用提示词模板 |
| HarpyModLoader 自改版 | `D:\哈比快车最新源码\harpymodloader\HarpyModLoader1` | 加载扩展职业、替换职业池、强制职业、权重、词条分配 |
| NoellesRoles 自改版 | `D:\哈比快车最新源码\noellesroles\NoellesRoles - 副本 - 副本 - 副本5.7.1` | 大型扩展职业源码，**本工程结构直接对齐它** |
| kinssaba | `D:\哈比快车最新源码\kinswathe\kinssaba` | 另一个同时依赖 Wathe + Harpy + NoellesRoles 的扩展，联调范例 |
| StarryExpress | `D:\哈比快车最新源码\starryexpress\StarryExpress1.3.2` | 仅指南书 / 图鉴与装饰；使用 **Mojang 官方映射**，不要和本工程的 Yarn 代码互抄 |

**映射命名**：fangroles / Wathe / Harpy / NoellesRoles / kinssaba 全部使用 **Yarn** 命名；只有 StarryExpress 用 Mojang 官方映射。

---

## 二、接任务时的固定流程

1. 先读用户本次需求。如果用户明确说「先不修改源码 / 先分析方案」，**本轮只分析方案，不改文件**。
2. 读取相关 txt，尤其是用户点名的 txt；需求属于近期类似改动时，也用 `rg` 在 `D:\哈比快车最新源码\fangroles\文档txt` 里找同类材料。
3. 读本工程 `README.md`、`build.gradle`、`gradle.properties`、`fabric.mod.json` 和相关源码入口。**不要只靠历史提示词或记忆判断**。
4. 用 `rg` 搜类名、方法名、物品 id、mixin 名、翻译 key、回放事件 id，再定位最小修改范围。
5. 判断改动是否真的需要动 Wathe / Harpy / NoellesRoles。**能在 fangroles 侧通过 API 或窄 mixin 解决，就优先不动上游。**
6. 能用 Wathe 公开 API 时优先用 API，少写深层 mixin；确实要 mixin 时条件必须尽量窄，并区分服务端 / 客户端环境。
7. 玩法数值、价格、时长、距离、体力、速度、心情惩罚等平衡参数，集中放进对应职业的 `*Constants`。
8. 关键代码必须写详细中文注释（见第九节）。
9. 如果改了 Wathe / Harpy / NoellesRoles 的公开 API：**先编译上游，把新 jar 覆盖进 `fangroles/libs/`，再编译 fangroles**。不能只编译单个工程就结束。
10. 如果用户提到旧会话 id 但当前读不到完整会话内容，不要猜；先说明缺失并询问，或只基于本地未提交改动和源码事实继续。

---

## 三、必读源码入口

### 3.1 Wathe 本体（`Wathe - 副本1`）

**核心机制**

- `README.md`：自改 Wathe 总说明
- `README_SHOP_CURRENCY_API.md`：商店多货币、默认价格读取和扩展接入教程
- `README_ITEM_TOOLTIP_API.md`：物品描述与冷却读秒 API
- `README_MOOD_TASK_API.md`：心情任务 API
- `README_PSYCHO_MODE_API.md`：疯魔加时 / 加护盾 / 暂停恢复 / 图标冷却 API
- `README_CLIENT_INPUT_API.md`：客户端键盘 / 鼠标 / KeyBinding / 最终动作 API
- `src/main/java/dev/doctor4t/wathe/Wathe.java`：总初始化、命令、网络包和系统注册
- `src/main/java/dev/doctor4t/wathe/game/GameConstants.java`：核心常量、默认商店、冷却、体力、移动速度、心情惩罚、被动收入上限
- `src/main/java/dev/doctor4t/wathe/game/GameFunctions.java`：开局、死亡、停局、地图重置、玩家存活判定
- `src/main/java/dev/doctor4t/wathe/game/gamemode/MurderGameMode.java`：Murder 模式胜负、被动收入和循环
- `src/main/java/dev/doctor4t/wathe/cca/GameWorldComponent.java`：对局状态、角色表、跳跃 / 碰撞 / 地图状态
- `src/main/java/dev/doctor4t/wathe/cca/PlayerShopComponent.java`：金币、多货币余额、购买结算
- `src/main/java/dev/doctor4t/wathe/cca/PlayerMoodComponent.java`：心情和任务完成入口
- `src/main/java/dev/doctor4t/wathe/cca/PlayerStaminaComponent.java`：体力、额外上限修正、本局初始化标记

**客户端渲染入口（扩展一般不应再 mixin 这些）**

- `client/gui/StoreRenderer.java`：右上角货币和商店价格渲染
- `client/gui/CrosshairRenderer.java`：局内准心图标、武器锁定、小进度图标
- `client/gui/RoleNameRenderer.java`：准心玩家名 / 同伙提示 / 额外 HUD
- `client/gui/BlackoutOverlayRenderer.java`：停电黑幕 HUD（读服务端同步的 `WorldBlackoutComponent`）
- `client/gui/MoodRenderer.java`、`TimeRenderer.java`：心情和时间 HUD
- `mixin/client/ui/InGameHudMixin.java`：Wathe 统一调度 `HudOverlayApi` 的注入点

### 3.2 Wathe 公开 API（`src/main/java/dev/doctor4t/wathe/api/`）

优先在这里找接口，找不到再考虑 mixin：

| 领域 | 接口 |
| --- | --- |
| 职业与阵营 | `Role`、`Faction`、`WatheRoles`、`WatheGameModes` |
| 商店 | `ShopApi`、`ShopPrice`、`ShopPayment`、`RoleShopProvider`、`ShopPurchaseContext` |
| 经济 | `EconomyApi`、`CurrencyDefinition`、`CurrencyAmount` |
| 心情任务 | `MoodTaskApi`、`MoodTaskDefinition` |
| 任务点透视 | `MoodTaskPointApi`、`TaskPointDefinition`、`TaskPointScanContext` |
| 任务完成 | `TaskCompletionApi` |
| 动态心情类型 | `api/mood/PlayerMoodTypeApi` |
| 结算阵营迁移 | `api/round/RoundEndFactionApi` |
| 胜利 | `VictoryApi` |
| 枪击 / 左轮反火 / 匕首冷却 / 武器目标 | `GunShotApi`、`GunShotContext`、`GunTargetContext`、`RevolverPenaltyContext`、`KnifeCooldownApi`、`KnifeCooldownContext`、`WeaponTargetingApi` |
| 普通掉落物拾取 | `ItemPickupApi`、`ItemPickupContext`、`ItemPickupResult` |
| 击杀 / 死亡阶段 | `DeathApi`、`DeathContext`、`BodySpawnContext` |
| 停电 | `BlackoutApi`、`BlackoutDuration`、`BlackoutEffectContext`、`BlackoutEffectResult` |
| 本能 | `InstinctApi` |
| 玩家存活 | `PlayerLifeStateApi` |
| 体力 / 体力免消耗 | `PlayerStaminaApi`（含 `registerConsumptionRule` / `consumeStamina`）、`PlayerStaminaComponent` |
| 移动速度 | `PlayerMovementApi` |
| 可见 / 可选中 / 可交互 / 可攻击 | `TargetVisibilityApi` |
| 玩家物理碰撞 | `PlayerCollisionApi`、`PlayerCollisionContext`、`PlayerCollisionMode` |
| 外观 | `PlayerAppearanceApi`、`BodyAppearanceApi` |
| 通用屏幕 HUD | `HudOverlayApi`、`HudOverlayContext`、`HudOverlayLayer`、`HudOverlayLayout` |
| 物品描述与冷却读秒 | `ItemTooltipApi` |
| 客户端最终雾效 / Iris 兼容 | `FogOverrideApi` |
| 客户端键盘 / 鼠标 / 最终动作 | `ClientInputApi`；完整说明见 `README_CLIENT_INPUT_API.md` |
| 准心图标 / 小进度条 | `CrosshairHudApi` |
| 准心名字 / 实体名牌 | `RoleNameHudApi` |
| 心情 HUD | `MoodHudApi` |
| 时间 HUD | `TimeHudApi` |
| 疯魔模式 | `PsychoModeApi`、`PsychoModeProfile`、`PsychoModeState`、`PsychoShieldContext`、`PsychoShieldResult`、客户端 `PsychoModeClientApi` |
| 手持物隐藏 | `HeldItemInvisibilityApi` |
| 低心情幻觉手持物 | 客户端 `mood/PsychosisItemApi` |
| 背包按钮 | `InventoryButtonApi`、`InventoryScreenType`、`InventoryButtonContext`、`InventoryPageState`、`InventoryPageSwitchWidget` |
| 床效果（服务端） | `bed/BedEffectRegistry`、`bed/BedEffectHandler` |
| 床粒子（客户端） | `bed/BedParticleRegistry`、`bed/BedParticleContext`、`bed/BedParticleRegistry.Provider` |
| 托盘 / 毒药 / 回放 | `TrayEffectRegistry`、`TrayTakeRegistry`、`TrayParticleRegistry`、`CanSeePoison`、`ReplayRegistry` |
| 生命周期事件 | `GameEvents`（含 `ON_BEGIN_INITIALIZE` / `ON_FINISH_INITIALIZE` / `ON_FINISH_FINALIZE`） |

**Wathe 2026-10 新增 / 变更（优先使用）**

- `PsychoModeProfile.Builder#cooldownItem(Item)`：登记 profile 对应的商店图标 / 冷却物品。
  默认 profile 已绑定 `WatheItems.PSYCHO_MODE`；自定义 profile 有独立图标时**必须**登记。
  API 只提供元数据，不会自动设置普通购买冷却。
- `PsychoModeApi.addRemainingTicks(player, profileId, ticks)` / `addArmourLayers(player, profileId, layers)`：
  加时 / 加护盾，都会校验玩家当前仍处于指定 profile 并同步组件；护盾只接受正数并饱和到整数上限。
- `PsychoModeApi.pause(player)` / `resume(player, state)` + `PsychoModeState`：
  可恢复暂停。暂停回收 profile 临时物品、移出活跃计数、**不写结束回放**；恢复重新走统一授予 / 锁栏流程。
- `PsychoModeApi.ensureMinimumCooldown(...)`：把 profile 图标冷却托底到下限，
  **已有更长冷却保持不变**；profile 未登记 `cooldownItem` 时返回 `false`。
- `GameConstants.PLAYER_STATE_SYNC_INTERVAL_TICKS = 5`、`PSYCHO_SYNC_INTERVAL_TICKS = 20`：
  连续数值 / 疯魔倒计时的客户端校准周期。
- 玩家私有组件（体力 / 心情 / 商店 / 中毒 / 便签 / 手雷 / 本能）已用 `shouldSyncWith` 只发本人；
  黑幕归属组件不再同步；`GameWorldComponent` 改为脏标记同步。

**Wathe 2026-10-18 新增**：详见 7.11–7.14（输入 / 匕首 / 拾取 / 心情类型 / 结算阵营 / 重置）。

### 3.3 HarpyModLoader（`HarpyModLoader1`）

- `src/main/java/org/agmas/harpymodloader/Harpymodloader.java`：职业注册表、`OVERWRITE_ROLES`、`ROLE_MAX`、`refreshRoles()`
- `src/main/java/org/agmas/harpymodloader/modded_murder/ModdedMurderGameMode.java`：扩展职业分配主流程
- `src/main/java/org/agmas/harpymodloader/modifiers/HMLModifiers.java`、`Modifier.java`：词条
- `src/main/java/org/agmas/harpymodloader/api/assignment/*`：**分配规则公开 API（优先用）**
- `src/main/java/org/agmas/harpymodloader/events/ModdedRoleAssigned.java`、`ModdedRoleRemoved.java`、`ModifierAssigned.java`、`ModifierRemoved.java`、`ResetPlayerEvent.java`
- `src/main/java/org/agmas/harpymodloader/commands/*`：`/setRole`、`/forceRole`、`/roleWeights` 等
- `README.md`：含 `/roleWeights` 调试指令完整说明

Harpy 按 `role.getFaction()` 分 **平民 / 义警 / 杀手 / 中立** 四个池；
`Harpymodloader.setRoleMaximum(role/id, max)` 控制最大生成数。

### 3.4 NoellesRoles（`NoellesRoles - 副本 - 副本 - 副本5.7.1`）

**入口与注册拆分（本工程结构对齐这一套）**

- `Noellesroles.java`：Fabric 主入口，只调用 `NoellesRolesBootstrap.init()`
- `bootstrap/NoellesRolesBootstrap.java`：总启动编排器，维护初始化顺序
- `registry/NoellesRolesCore.java`：`MOD_ID`、日志器、`Identifier` 工具
- `registry/NoellesRoleIds.java`：职业 / 词条稳定 id
- `registry/NoellesRoleRegistry.java`：`Role` 实例与显式阵营注册
- `registry/NoellesModifierRegistry.java`：Harpy 词条注册
- `registry/NoellesRoleGroups.java`：跨系统共享角色分组
- `bootstrap/NoellesRolesPayloadTypes.java` / `NoellesRolesPacketReceivers.java`：网络包
- `bootstrap/NoellesRolesEventBootstrap.java`：事件、server tick、回合清理
- `combat/NoellesRolesCombatBootstrap.java`：枪击 / 匕首冷却 API 接入总引导
- `pickup/NoellesItemPickupHandlers.java`：掉落物拾取 API 聚合入口
- `reset/NoellesRolesResetBootstrap.java`：玩家 / 世界重置 handler 聚合入口
- `death/NoellesRolesDeathBootstrap.java`：死亡保护与 DeathApi 分阶段接入
- `bootstrap/NoellesRolesMovementBootstrap.java`：体力（含免消耗）与移速接入
- `bootstrap/NoellesRolesEconomyBootstrap.java`：经济接入
- `bootstrap/NoellesRolesPsychoBootstrap.java` / `MoodTaskBootstrap.java` / `BlackoutBootstrap.java`：分发器
- `bootstrap/NoellesRolesMoodTypeBootstrap.java` / `RoundEndBootstrap.java`：动态心情类型 / 结算阵营迁移聚合入口（当前为空）
- `shop/NoellesRolesShopBootstrap.java`：商店
- `roleassign/NoellesRolesRoleAssignedBootstrap.java`：分配后初始化
- `record/NoellesRolesReplayFormatters.java`：回放格式化

**客户端**

- `client/NoellesrolesClient.java`：只做初始化编排
- `client/bootstrap/*`：按职责拆分（Role / Item / Tick / Networking / Connection / EntityRenderer / Particle）
- `client/hud/NoellesHudHandlers.java`、`NoellesHudSupport.java`：HUD 总注册与布局辅助
- `client/input/NoellesClientKeybindings.java`：公共能力键读取 API（**外部扩展必须用 `getAbilityBind()`**）
- `client/input/NoellesClientInputBootstrap.java`：客户端输入 API 聚合入口，只调用各职业 / 词条 `*InputRules.register()`
- `client/roles/<role>/*StatusHud.java`、`client/ui/roles/<role>/*InventoryButtons.java`

**优先参考职业**：服务员 `roles/waiter`、追忆者 `roles/rememberer`、风灵师 `roles/winder`、接线员 `roles/operator`、魔术师 `roles/magician`、时停者 `roles/timekeeper`、杀手皇后 `roles/killer_queen`

**2026-10 新增参考：杀手皇后 `roles/killer_queen`**

- 服务端：`KillerQueenManager`（标记收束 / 招募 / 疯魔压制 / 回合 tick）、`KillerQueenAbility`、
  `KillerQueenDeathHandler`、`KillerQueenGunHandler`、`KillerQueenMovementHandler`、
  `KillerQueenTrayEffectHandler`、`KillerQueenVictoryRule`、`KillerQueenWorldComponent` / `KillerQueenPlayerComponent`。
- 客户端：`client/roles/killer_queen/*`（HUD / 输入 / 音乐 / 托盘粒子）、
  `client/instinct/roles/killer_queen/*`、`client/appearance/roles/killer_queen/*`。
- 所有已有疯魔 profile 都已补 `cooldownItem`；新增疯魔照此登记，才能用 `ensureMinimumCooldown` 做跨职业冷却托底。
- 疯魔被亡命时刻压制时用 Wathe `pause/resume` 保存同一次疯魔；影子小丑第四阶段 / 执照恶棍时刻只压音乐，不暂停服务端机制。

**2026-10-18 API 拆分参考**：`NoellesClientInputBootstrap` + `*InputRules`、
`NoellesItemPickupHandlers` + `*ItemPickupHandler`、`NoellesRolesCombatBootstrap` + `*KnifeCooldownHandler`、
`NoellesRolesMoodTypeBootstrap` / `RoundEndBootstrap`（空入口）、
`NoellesRolesResetBootstrap` + `*ResetHandler`、`CookStaminaHandler`（体力免消耗范例）。

**时停回溯公开 API（跨模组协作时会用到）**

- `api/timekeeper/TimekeeperSnapshotApi.java`：`registerPlayerComponent` / `registerWorldComponent` / `registerParticipant` / `registerRewindListener`
- `api/timekeeper/TimekeeperSnapshotParticipant.java`、`TimekeeperRewindListener.java`
- 外部扩展**不得**直接修改 `TimekeeperSnapshots` 的内部清单
- 所有注册必须在首张历史快照生成前完成

---

## 四、公开 API 优先表（写 Mixin 前先核对）

| 想做的事 | 用什么，不要写什么 Mixin |
| --- | --- |
| 枪击后果 / 左轮目标 / 反火 / 冷却修正 | `GunShotApi`；不要 mixin `GunShootPayload`、`RevolverItem`、`DerringerItem` |
| 匕首刺杀后的冷却修正 | `KnifeCooldownApi`、`KnifeCooldownContext`；不要 mixin `KnifeStabPayload.Receiver` |
| 普通掉落物拾取判定 | `ItemPickupApi`、`ItemPickupContext`、`ItemPickupResult`；不要 mixin `ItemEntity#onPlayerCollision` |
| 客户端武器射线目标 | `WeaponTargetingApi`；不要自己拼 `ProjectileUtil` + 存活 + 可见性 |
| 击杀奖励 / 致死转化 / 确认死亡后清理 / 尸体生成 | `DeathApi`；不要 mixin `GameFunctions.killPlayer` |
| 死亡保护 | `AllowPlayerDeath` 保护链 |
| 普通屏幕 HUD | `HudOverlayApi`；不要 mixin `InGameHud`、`RoleNameRenderer` |
| 准心图标 / 小进度条 | `CrosshairHudApi`；不要 mixin `CrosshairRenderer` |
| 准心名字 / 实体名牌 / 同伙提示 | `RoleNameHudApi` |
| 玩家 / 尸体隐藏、不可选中、不可交互、不可攻击 | `TargetVisibilityApi`；不要 mixin `PlayerBodyEntityRenderer`、`LivingEntity#canHit` |
| 玩家皮肤 / 尸体皮肤 | `PlayerAppearanceApi`、`BodyAppearanceApi` |
| 停电触发 / 恢复 / 时长 / 药水 | `BlackoutApi`；不要 mixin `WorldBlackoutComponent` ticks，也不要客户端监听 `AMBIENT_BLACKOUT` 自己计时 |
| 疯魔形态 / 护盾 / 音效 / 皮肤 | `PsychoModeApi`、`PsychoModeProfile`、`PsychoModeClientApi`；不要直接改 `psychoTicks` / `armour` |
| 疯魔加时 / 加护盾 / 暂停恢复 | `PsychoModeApi.addRemainingTicks` / `addArmourLayers` / `pause` / `resume`、`PsychoModeState`；不要把暂停写成 `stop` |
| 疯魔图标最低冷却 | `PsychoModeProfile.Builder#cooldownItem` + `PsychoModeApi.ensureMinimumCooldown`；不要维护 profile-to-item 硬编码表 |
| 玩家私有状态同步范围 | 组件 `shouldSyncWith`；不要为了客户端方便把本人 HUD / 商店 / 心情广播给所有人 |
| 玩家碰撞 | `PlayerCollisionApi`；不要 mixin `Entity#collidesWith`、`EntityView#getEntityCollisions`、推挤方法 |
| 体力 | `PlayerStaminaApi` / `PlayerStaminaComponent`；不要读旧 `sprintingTicks` NBT |
| 体力免消耗 | `PlayerStaminaApi.registerConsumptionRule` + `consumeStamina(..., CUSTOM)`；不要用巨大上限 + 每 tick 回满 |
| 移动速度 | `PlayerMovementApi`；不要 shadow `forwardSpeed` / `sidewaysSpeed`，不要 mixin `travel()` / `jump()` / `getMovementSpeed()` |
| 雾效 / Iris 兼容 | `FogOverrideApi`；不要注入 `WorldRenderer.render` |
| 心情任务注册 / 发放 / 完成拦截 | `MoodTaskApi`；不要 mixin `PlayerMoodComponent#completeTask`，不要往 `Task` enum 加值 |
| 任务收入 / 完成监听 | `TaskCompletionApi` |
| 商店条目 / 职业商店 | `ShopApi` |
| 金币 HUD / 被动收入 | `EconomyApi` |
| 本能透视 | `InstinctApi` |
| 心情 HUD 样式 | `MoodHudApi` |
| 动态心情类型 | `PlayerMoodTypeApi`；不要改共享 `Role`，HUD 用 `MoodHudContext.effectiveMoodType()` |
| 时间 HUD | `TimeHudApi` |
| 手持物隐藏 | `HeldItemInvisibilityApi` |
| 低心情幻觉手持物 | 客户端 `PsychosisItemApi` |
| 背包按钮 / 分页 | `InventoryButtonApi`；不要 mixin `LimitedInventoryScreen` / `InventoryScreen` |
| 客户端键位 / 鼠标 / 滚轮 / 最终动作 | `ClientInputApi`；不要 mixin `KeyBinding` / `Keyboard` / `Mouse` / `MinecraftClient` 动作方法 |
| 特殊胜利 | `VictoryApi` |
| 结算阵营栏目迁移 | `RoundEndFactionApi`；不要 mixin `RoundTextRenderer` 或改 `RoundEndData` 构造 |
| 托盘效果 / 取物上限 / 托盘粒子 | `TrayEffectRegistry`、`TrayTakeRegistry`、`TrayParticleRegistry`；不要 mixin `BeveragePlateBlockEntity` |
| 床效果（服务端） | `BedEffectRegistry`、`BedEffectHandler`；不要 mixin 床方块或 `TrimmedBedBlockEntity` 的服务端逻辑 |
| 床粒子（客户端） | `BedParticleRegistry`；不要注入 `TrimmedBedBlockEntity.clientTick` |
| 物品描述与冷却读秒 | `ItemTooltipApi`；不要注册自己的全局 `ItemTooltipCallback` |
| 新局分配前清理 / 结算收尾 | `GameEvents.ON_BEGIN_INITIALIZE` / `ON_FINISH_FINALIZE`；不要注入 `GameFunctions.initializeGame` / `finalizeGame` |
| Harpy 职业互斥 / 绑定 / 补位 | `RoleAssignmentApi`；不要 mixin `ModdedMurderGameMode#findAndAssignPlayers` |
| Harpy 词条绑定 / 排斥 / 强制配对 / 动态上限 | `ModifierAssignmentApi`；不要 mixin `assignModifiers` |

---

## 五、职业与阵营注册规则

推荐格式：

```java
// registry/FangRolesIds.java
public static final Identifier SOME_ROLE_ID = FangRolesCore.id("some_role");

// registry/FangRolesRoleRegistry.java
public static final Role SOME_ROLE = WatheRoles.registerCivilianRole(new Role(
        FangRolesIds.SOME_ROLE_ID,
        SomeRoleConstants.ROLE_COLOR,
        true,
        false,
        Role.MoodType.REAL,
        WatheRoles.CIVILIAN.getMaxSprintTime(),
        false
));
```

`Role` 七参构造顺序（**不能错**）：
`Identifier` → `int color` → `boolean isInnocent` → `boolean canUseKiller` →
`MoodType` → `int maxSprintTime` → `boolean canSeeTime`

常见阵营语义：

- **平民好人**：`registerCivilianRole`，通常 `isInnocent=true`、`canUseKiller=false`、`MoodType.REAL`
- **义警**：`registerVigilanteRole`，需要替换原版义警位，**不要混进普通平民池**
- **杀手**：`registerKillerRole`，通常 `MoodType.FAKE`、`canSeeTime=true`
- **中立**：`registerNeutralRole`，**不要让中立靠 `false,false` 被误归类**，否则缺少结算和欢迎文本

**必须显式登记阵营**。旧扩展里还有不少 `WatheRoles.registerRole(...)`；如果本次任务涉及胜利、RoleName 阵营色、左轮冷却、被动收入上限或 Harpy 分配池，就应迁移到显式阵营。

Harpy 会在 `refreshRoles()` 自动给非特殊职业生成 announcement；fangroles 侧**仍要自己补**：

```json
"announcement.role.fangroles.<id>": "…",
"announcement.goals.fangroles.<id>": "…"
```

### 权重账本注意事项

- 账本在 Wathe `ScoreboardRoleSelectorComponent`，开关在 `GameWorldComponent.enableWeights`（默认开启）。
- 原版杀手 / 义警、中立阵营和 Harpy 具体扩展职业**共用同一份** `RoleWeightRecord`。
- `recordRoundAssignments(...)` 必须保持在 `GameEvents.ON_FINISH_INITIALIZE` 之后调用，确保开局强制职业和扩展最终职业计入历史。
- **局内 `/setRole` 调试转职不得写入权重历史。**
- 权重开关只决定是否读取历史，**不应顺手清空历史**；清空请走 `/wathe:gameSettings weights reset` 或 Harpy `/roleWeights reset ...`。
- 长报告和权重查询使用 `sendMessage`，不要依赖 `sendCommandFeedback`。

---

## 六、新增职业的注册点检查清单

按顺序逐项核对，**每一项都要确认是否需要改**：

| # | 位置 | 需要做什么 |
| --- | --- | --- |
| 1 | `registry/FangRolesIds.java` | 登记职业 / 词条稳定 `Identifier` |
| 2 | `registry/FangRolesRoleRegistry.java` | 创建 `Role` 实例并用 `registerCivilianRole` / `registerVigilanteRole` / `registerKillerRole` / `registerNeutralRole` 显式登记阵营 |
| 3 | `registry/FangRolesModifierRegistry.java` | 新增 / 调整 Harpy 词条 |
| 4 | `registry/FangRolesRoleGroups.java` | 新增跨系统共享角色集合（杀手侧中立、本能分组、胜利分组等） |
| 5 | `registry/FangRolesDeathReasons.java` | 新增专属死亡原因 id |
| 6 | `registry/FangRolesEventIds.java` | 新增回放事件 / 托盘 / 床 / 护盾来源 / 疯魔 profile id |
| 7 | `roles/<role_id>/<RoleName>Constants.java` | 该职业全部玩法数值（冷却、时长、距离、速度、阈值） |
| 8 | `roles/<role_id>/<RoleName>RoleAssignmentRules.java` | Harpy 分配互斥 / 单向排斥 / 绑定生成 / 阶段补位 |
| 9 | `roles/<role_id>/<RoleName>RoleAssignedHandler.java` | 分配后发开局物品、重置本局状态、设置开局冷却 |
| 10 | `roles/<role_id>/<RoleName>ShopHandler.java` | 职业商店（或 `ShopApi.registerShopModifier`） |
| 11 | `roles/<role_id>/` 各 handler | 按需：`Interaction` / `Gun` / `GunCooldown` / `KnifeCooldown` / `ItemPickup` / `Death` / `Movement` / `Stamina` / `PlayerCollision` / `Visibility` / `Blackout` / `Psycho` / `MoodTask` / `MoodType` / `RoundEnd` / `Reset` / `Victory` / `ReplayFormatters` |
| 12 | `roles/<role_id>/<RoleName>PlayerComponent.java` | 需要持久 / 同步状态时建 CCA 组件 |
| 13 | `FangRolesComponents.java` | 注册 CCA 组件工厂 |
| 14 | `src/main/resources/fabric.mod.json` | `custom.cardinal-components` 里补组件 id（**漏了组件不会被分配**） |
| 15 | `bootstrap/FangRolesBootstrap.java` | 新增全局初始化器时按顺序补调用 |
| 16 | `bootstrap/FangRolesLimitsBootstrap.java` | `Harpymodloader.setRoleMaximum(role, n)` 静态上限 |
| 17 | `bootstrap/FangRolesEventBootstrap.java` | 事件监听、server tick、人数动态上限；玩家 / 世界重置放 `FangRolesResetBootstrap` |
| 18 | `bootstrap/FangRolesPayloadTypes.java` | 新增 payload codec |
| 19 | `bootstrap/FangRolesPacketReceivers.java` | 新增服务端 packet receiver / 能力分发 |
| 20 | `bootstrap/FangRolesEconomyBootstrap.java` | 金币 HUD、任务收入、被动收入 |
| 21 | `bootstrap/FangRolesReplayBootstrap.java` + `record/FangRolesReplayFormatters.java` | 回放格式化器 |
| 22 | `bootstrap/FangRolesPsychoBootstrap.java` | 疯魔 profile / 护盾规则，调用各职业 `*PsychoHandler.init()` |
| 23 | `bootstrap/FangRolesMoodTaskBootstrap.java` | 心情任务，调用各职业 `*MoodTaskHandler.init()` |
| 24 | `bootstrap/FangRolesBlackoutBootstrap.java` | 停电分组规则与各职业 `*BlackoutHandler.init()` |
| 25 | `bootstrap/FangRolesMovementBootstrap.java` | 体力（含 `*StaminaHandler` 免消耗规则）/ 速度修正 handler |
| 26 | `combat/FangRolesCombatBootstrap.java` | 枪击 / 左轮惩罚 / 枪械冷却 / 匕首冷却 handler |
| 27 | `death/FangRolesDeathBootstrap.java` | 死亡保护、击杀奖励、尸体生成回调 |
| 28 | `collision/FangRolesPlayerCollisionHandlers.java` | 玩家碰撞规则 |
| 29 | `visibility/FangRolesTargetVisibilityHandlers.java` | 玩家 / 尸体可见性规则 |
| 30 | `victory/FangRolesVictoryRules.java` | 特殊胜利规则 |
| 31 | `appearance/FangRolesAppearanceHandlers.java` | 服务端尸体外观 UUID |
| 32 | `tray/FangRolesTrayEffects.java` / `bed/FangRolesBedEffects.java` | 托盘效果 / 床效果（服务端 `BedEffectRegistry`） |
| 33 | `ModItems.java` | 新增物品、数据组件、默认冷却 |
| 34 | `shop/FangRolesShopBootstrap.java` / `FangRolesShops.java` | 商店注册与特殊交付逻辑 |
| 35 | `client/FangrolesClient.java` | 只做编排；新增全局客户端初始化器时补调用（含 `FangClientInputBootstrap`） |
| 36 | `client/bootstrap/FangClient*.java` | 按职责补：物品 / tick / 网络 / 连接清理 / 粒子 / 实体渲染 / 疯魔 / 移动 |
| 37 | `client/input/FangClientKeybindings.java` | 能力键**默认复用 NoellesRoles 的 G 键**，不要额外注册；确实需要独立键时才调 `registerFallbackAbilityBind()` |
| 38 | `client/input/FangClientPreAttackHandlers.java` | 左键预攻击接管 |
| 39 | `client/hud/FangHudHandlers.java` | 新增 HUD provider 后调用该职业 HUD 类的 `register()` |
| 40 | `client/crosshair/FangCrosshairHandlers.java` | 准心 provider |
| 41 | `client/instinct/FangInstinctHandlers.java` | 本能规则 |
| 42 | `client/inventory/FangInventoryButtons.java` | 背包按钮 provider |
| 43 | `client/visibility/FangHeldItemVisibilityHandlers.java` | 手持物隐藏 |
| 44 | `client/mood/FangPsychosisHandlers.java` | 低心情幻觉 |
| 45 | `client/appearance/FangAppearanceHandlers.java` | 客户端外观与原始皮肤缓存 |
| 46 | `client/bootstrap/FangClientConnectionBootstrap.java` | **新增任何客户端静态缓存后必须在这里补 JOIN / DISCONNECT 清理** |
| 47 | `client/bed/FangRolesBedParticles.java` | 新增带视觉表现的床效果后，在这里调用该职业 `*BedParticleHandler.init()` |
| 48 | `fangroles.mixins.json` / `fangroles.client.mixins.json` | 新增 mixin 时注册，环境要正确 |
| 49 | `lang/zh_cn.json` + `lang/en_us.json` | 职业名、欢迎公告、物品名、tooltip、HUD、actionbar、回放、死亡原因 |
| 50 | `assets/fangroles/models/item/*.json`、`textures/item/*.png`、`textures/gui/sprites/hud/*` | 模型与贴图 |
| 51 | `bootstrap/FangRolesMoodTypeBootstrap.java` | 动态心情类型：调用各职业 / 词条 `*MoodTypeHandler.init()` |
| 52 | `bootstrap/FangRolesRoundEndBootstrap.java` | 结算阵营迁移：调用各职业 / 词条 `*RoundEndHandler.init()` |
| 53 | `pickup/FangRolesItemPickupHandlers.java` | 掉落物拾取：调用各职业 / 词条 `*ItemPickupHandler.init()` |
| 54 | `reset/FangRolesResetBootstrap.java` | 玩家 / 世界重置：调用各职业 / 词条 `*ResetHandler.init()` |
| 55 | `client/input/FangClientInputBootstrap.java` | 客户端输入：调用各职业 / 词条 `*InputRules.register()` |

---

## 七、跨领域接入规范

### 7.1 商店与经济

- 完全替换某职业商店：`ShopApi.registerRoleShop(role, provider)`
- 静态职业商店：`ShopApi.registerStaticRoleShop(...)`
- 只追加 / 移除 / 替换默认条目：`ShopApi.registerShopModifier(id, priority, handler)`
- 通用购买逻辑：`ShopApi.defaultPurchase(context)`
- 统一购买冷却判断：`ShopApi.isPurchaseCooldownBlocked(...)`

**扩展 provider 的 `purchase(...)` 只负责判断商品是否交付成功**，不要自己扣钱、播放通用购买音效或写通用购买回放 —— 那些由 `PlayerShopComponent#tryBuy(...)` 统一做。

交付类型必须选对：

- 普通 `new ShopEntry(...)`、`giveToInventory(...)`、`directToHotbar(...)` → `PurchaseType.ITEM`
- 疯魔、停电、恢复电力、刷新冷却等「购买后立即执行能力且不给显示物品」→ `ShopEntry.action(...)` → `PurchaseType.ACTION`
- `/wathe:shopCooldownBypass <true|false>` 默认关闭；开启后**只有 `ITEM`** 可在商品冷却期间继续购买，`ACTION` 始终受冷却限制

**非杀手职业商店不要用默认快捷栏交付逻辑**，优先 `ShopEntry.giveToInventory(...)` / `directToHotbar(...)` / `action(...)`。

多货币价格：

- 纯金币：`new ShopEntry(stack, int, type)` 或 `ShopPrice.money(int)`
- 多货币同时支付：`ShopPrice.allOf(CurrencyAmount.money(...), CurrencyAmount.taskMoney(...))`
- 多方案任选：`ShopPrice.anyOf(ShopPrice.option(...), ShopPrice.option(...))`
- 读默认第 0 组金币：`ShopApi.getDefaultMoneyPrice(item, 0, fallback)`
- 读某组某货币：`ShopApi.getDefaultCurrencyPrice(item, optionIndex, currencyId, fallback)`
- 只有明确要完整继承默认价格结构时才用 `ShopApi.getDefaultShopPrice(item)`

> **中立 / 平民职业通常只能读默认商品的金币价格**，不要完整复制默认杀手 `ShopPrice`，
> 否则后续 Wathe 重新启用任务币时该职业会买不起。

当前任务币状态：`EconomyApi.TASK_MONEY`、`CurrencyAmount.taskMoney(...)` 和 `/wathe:setTaskMoney` 能力保留；
默认杀手商店已恢复纯金币；`GameConstants.TASK_MONEY_PER_KILLER_TASK = 0`、`TASK_MONEY_PER_KILL = 0`；
任务币 HUD 注册逻辑目前注释暂停。

被动收入：

- 非杀手职业要被动收入：`EconomyApi.registerPassiveIncomeRole(s)`
- 动态允许 / 禁止：`EconomyApi.registerPassiveIncomeRule(...)`
- 改数值：`EconomyApi.registerPassiveIncomeModifier(...)`
- 最终上限由 `GameConstants.getPassiveMoneyAmount(faction, currentBalance, baseIncome)` 统一裁剪，职业侧不要自己再截断

任务收入：

- 普通任务完成监听：`TaskCompletionApi.AFTER_TASK_COMPLETE`
- 「任务完成但跳过默认收入」用 `registerTaskIncomeRule(...)` 返回 `SUPPRESS_DEFAULT_INCOME`
- **拥有杀手能力的玩家不会叠加旧任务金币 provider**；「杀手完成任务也额外给金币」这类设计要用 `AFTER_TASK_COMPLETE` 单独补发

### 7.2 心情任务

- 用 `MoodTaskApi.registerTask(...)` 注册 `MoodTaskDefinition`；**不要往 `PlayerMoodComponent.Task` 追加 enum**
- 内置任务默认进入随机池；**扩展任务默认只允许指定发放**，只有明确希望进随机池时才在定义里显式启用
- 指定发任务：`MoodTaskApi.assignTask(player, taskId)`；随机 / 补槽：`assignRandomTasks(...)` / `fillRandomTaskSlots(...)`
- 阻止发放用 `registerAssignmentRule(...)`，**不要**用「发放后同 tick 删除」模拟（客户端会看到任务闪现）
- 阻止完成用 `registerCompletionRule(...)`，**不要** mixin `completeTask(...)`
- 删除任务用 `removeTask(...)`；按流程完成用 `completeTask(...)`（会加心情、写回放、触发任务完成 API）
- 任务槽位由当前世界 `GameWorldComponent` 动态设置（默认 3、最低 1，可高于 3）
- 任务点透视用 `MoodTaskPointApi`
- 随机事件模式：首个自动任务用 `GameConstants.TIME_TO_FIRST_TASK`；后续用 `RANDOM_MOOD_TASK_MIN_COOLDOWN` / `MAX_COOLDOWN`（默认 35~55 秒），任务完成**不**重置计时
- 动态心情类型：判断运行时类型用 `PlayerMoodTypeApi.resolveMoodType(player)` / `hasRealMood` / `hasFakeMood`；
  `Role#getMoodType()` 只是职业注册基础值。任务发放资格已经按有效类型判断，扩展不要另建一套类型表

### 7.3 疯魔模式

- 基础定义：`PsychoModeProfile.copyOf(PsychoModeApi.createDefaultProfile(), id)`，再覆盖 `durationTicks`、`armour`、`grantedItems`、`hitSound`、`shieldSound`、`backgroundSound`、`visualSettings`、`nameTranslationKey`
- 启动 / 结束：`PsychoModeApi.start(player, profileId)` / `stop(player, recordReplay)`；**不要**直接改 `psychoTicks` / `armour`
- 图标冷却登记：自定义 profile 有独立购买图标时**必须** `cooldownItem(item)`；API 不会自动设置普通购买冷却
- 加时：`PsychoModeApi.addRemainingTicks(player, profileId, ticks)`；加护盾：`addArmourLayers(player, profileId, layers)`；
  两者都要求玩家当前仍处于指定 profile，只接受正数增量
- 暂停 / 恢复：`PsychoModeState state = PsychoModeApi.pause(player)`，把 state 存进自己的同步 / 时间线组件；
  `resume(player, state)` 恢复同一次疯魔。快捷栏不足会失败，调用方保留快照重试
- **暂停不是结束**：`pause` 回收 profile 临时物品、移出活跃计数、不写结束回放；真正结束仍用 `stop`
- 跨职业托底冷却：`ensureMinimumCooldown(player, profileId, ticks)`（也有 `profile` 重载）；已有更长冷却保持不变，只延长不缩短
- 同步：疯魔组件按 `GameConstants.PSYCHO_SYNC_INTERVAL_TICKS`（20 tick）校准，启动 / 结束立即同步；
  **不要**再为倒计时每 tick `sync`
- 查询：`isActive(...)`、`getRemainingTicks(...)`、`getArmour(...)`
- 授予物品会被打 `PSYCHO_GRANTED_PROFILE` 标记，结束时只回收该 profile 授予的临时物品
- 护盾穿透 / 强制挡盾：`registerShieldRule(id, priority, handler)` 返回 `BYPASS` / `BLOCK` / `PASS`
- 皮肤：profile 默认皮肤 + 客户端 `PsychoModeClientApi.registerVisualProvider(...)` 按 priority 覆盖
- 音乐：profile 的 `backgroundSound(sound, play)`；客户端新音乐用 `PsychoModeClientApi.registerBackgroundAmbience(sound, intervalTicks)`
- Mood 显示：`MoodHudApi.registerPsychoStyle(...)`；Wathe 按护盾是否 > 0 切换 `body` / `hitBody`，0 护盾 profile 启动即显示破损态
- 回放：profile 要补 `psycho_mode.fangroles.<name>` / `psycho_shield.fangroles.<name>` 翻译

### 7.4 停电

- 触发：`BlackoutApi.trigger(world)`；恢复：`BlackoutApi.restorePower(world)`
- 改时长：`registerDurationModifier(id, priority, handler)` 返回 `BlackoutDuration.of(minTicks, maxTicks)`
- 改药水：`registerEffectRule(id, priority, handler)` 返回 `nightVision()` / `blindness()` / `none()` / `pass()`
- 默认规则：杀手阵营夜视；平民、义警、中立失明。玩家只要拥有夜视，黑幕完全消失，Wathe 自己发的失明也会让位
- 性能：大地图灯光按 `ChunkSection` 分组、预算队列调度；**不要**改回逐灯倒计时；每盏灯自然恢复只写一次最终状态；只有全局随机间隔循环声
- 停电 CCA 的客户端同步只含时间线、黑幕和药水配置，**不要**发完整灯坐标

### 7.5 玩家碰撞 / 体力 / 移速

- 碰撞：`PlayerCollisionApi.registerRule(id, priority, handler)`，返回 `SOLID` / `VANILLA_PUSH` / `NO_COLLISION` / `PASS`
- `PlayerCollisionContext` 是**有方向**的 `self -> other`；只要任一方向返回 `NO_COLLISION`，Wathe 就取消该次推挤
- 体力：`PlayerStaminaApi` 的清空 / 回满 / 增减 / 上限修正
- 有限体力免消耗：`PlayerStaminaApi.registerConsumptionRule(id, priority, rule)` 返回
  `PASS` / `CONSUME` / `SKIP`；普通能力成本用 `consumeStamina(player, amount, CUSTOM)`；
  管理员调试 / 惩罚仍用 `drainStamina` / `clearStamina` / `setStamina`
- 免消耗不提高上限、不自动回满，也不解除低心情禁跑或 0 体力后的自主移动 / 跳跃限制
- 纯消耗规则放 `*StaminaHandler`，由 `FangRolesMovementBootstrap` 聚合；
  **不要**再用“巨大上限 + 每 tick 回满”间接抵消 Wathe 的扣除
- 速度：`PlayerMovementApi.registerSpeedModifier(id, priority, handler)`，返回 `ADD` / `MULTIPLY` / `OVERRIDE` / `PASS`；priority 从高到低累计
- 心情体力惩罚默认关闭，开关在 `GameWorldComponent`，值在 `GameConstants`
- 体力归零时不能自主水平移动和跳跃，但击退、传送、水流、载具等外力位移仍保留

### 7.6 外观 / 可见性 / RoleName HUD

外观优先级经验：

- priority 越大越先执行；返回 `null` / `PASS` 让低优先级继续
- 全局限时伪装应**高于**普通主动变形
- 「只影响自己客户端看到的一切」的视觉覆盖走客户端 handler，且**不改变服务端真实身份 / 尸体 owner**
- 低优先级外观应给主动变形让路
- 用 `PlayerAppearanceApi.resolveOriginalSkinTextures(...)` 防伪装套娃，**不要**读目标当前已被覆盖的皮肤

`TargetVisibilityApi` 面向「某观察者能否看见 / 选中 / 交互 / 攻击某玩家或尸体」。
隐藏了目标**不代表**不能攻击；服务端职业能力和物品**仍必须**在自己的 handler 里调用
`canInteractWithBody(...)`、`canInteractWithPlayer(...)`、`canAttackPlayer(...)` / `canAttackEntity(...)` 重新校验。

`registerEntityName(...)` 只给「非玩家实体也要在准心处显示名字」的窄场景（例如魔术师播放体）使用；
普通玩家名仍用 `registerName(...)`。

### 7.7 HUD / 准心 / Tooltip / 背包按钮

通用屏幕 HUD：

- 普通右下角职业状态：`HudOverlayApi.registerAliveRole(...)`（同时判断职业和 Wathe 存活定义）
- 非职业专属但仍只给活人看：`HudOverlayApi.register(...)` + provider 内显式检查 `context.aliveAndSurvival()`
- `BEFORE_HUD` / `MAIN_HUD` / `AFTER_HUD` 三层；同层内 priority 越大越晚渲染
- 需要复画快捷栏时调用 `context.renderHotbar()`
- **普通 HUD 不要再新增 `InGameHud` / `RoleNameRenderer` mixin**

准心：

- 替换中心准心或下方小图标：`CrosshairHudApi.registerProvider(...)`；`PASS` 继续，`HANDLED` 结束
- 只在默认准心后补进度条：`CrosshairHudApi.registerOverlay(...)`

Tooltip：

- `ItemTooltipApi.registerItem(...)` / `registerItems(...)`；**不要**注册自己的全局 `ItemTooltipCallback`
- 读秒必须读当前 `ItemCooldownManager.Entry` 的 `endTick - tick`；**禁止**用 `GameConstants.ITEM_COOLDOWNS * getCooldownProgress(...)` 反推
- 动态文本用 `registerAppender(...)`
- tooltip 是客户端显示，**不得**作为服务端购买 / 攻击 / 能力合法性判断依据

背包按钮：

- 三种 screen：`LIMITED`（局内限制背包，职业选人挂这里）、`VANILLA`（原版背包）、`CREATIVE`
- provider 返回 `null` 表示当前 screen 不需要按钮
- 用 `addWidget(groupId, widget)` 分组；动态变化用 `replaceGroup` / `clearGroup` / `setGroupVisible`
- 「删除 widget」在 API 里是隐藏 + 禁用 + 解除焦点（MC 内部列表没有稳定删除接口）
- 换页用 `InventoryButtonLayout` / `InventoryPageState` / `InventoryPageSwitchWidget`；Wathe 会在开局、停局、结算完成、断线时统一 reset 页码
- 需要禁止 E 键关背包时在 `allowInventoryKeyClose(...)` 返回 `false`，并在 `close(...)` 清理静态输入状态

### 7.8 胜利、死亡与回放

`VictoryApi` 返回值：

- 不参与：`VictoryResult.pass()`
- 拖住普通结算：`VictoryResult.keepRunning()`
- 普通阵营共胜：`VictoryResult.vanillaWin(...)`
- 独立胜利：`VictoryResult.customWin(...)`
- 主动立即结束：`VictoryApi.endGameWithCustomVictory(...)`

新增独立胜利必须明确 5 件事：只剩自己是否独胜、是否允许杀手 / 乘客共胜、
是否需要拖住 `TIME` 超时结算、死亡队友 / 伴侣是否写进胜利阵营、结算页颜色与翻译 key。

`DeathApi` 优先级约定（越大越先执行，同 priority 后注册的先执行）：
重复死亡保护 → 特殊存活保护 → 死亡流程状态 → 回放上下文 → 致死确认前拦截 →
普通逻辑 → 确认死亡后的奖励 / 二段机制 → 最终清理。

**击杀奖励必须放在确认死亡阶段**（`confirmedDeath()` 或等价的 `afterMarkedDead`），
并检查攻击者是当前真实存活玩家；**禁止**在 `beforeAttempt` 按死亡请求次数发钱，
否则胶带残留、环境伤害和递归死亡会造成重复金币。

需要新死因或技能事件时同时补 5 项：事件 / 死因 id、`GameRecordManager` 记录、
`ReplayRegistry` 格式化器、`zh_cn.json` / `en_us.json` 翻译、回合结束与玩家重置清理。

回放数据尽量存稳定 id、UUID 和必要显示名兜底；展示文字用 `Text.translatable(...)`，
**不要把中文句子硬编码进记录数据**。

**回溯职业修正**：时停 / 时间回溯类机制在最终历史帧落地后，如果只静默修改运行时职业表，
回放仍会沿用初始职业快照。此时必须调用
`GameRecordManager.recordRoleCorrection(world, playerUuid, oldRole, newRole)`。
约束：先恢复自己的 CCA / 运行时职业映射再记录；correction 只用于回放时间线，
**不**触发 `ModdedRoleAssigned`、不发开局物品、不初始化任务；
正常时间线中的真实转职仍用 `recordRoleChange(...)` 或 `GameWorldComponent.addRole(...)`。

### 7.9 能力键：默认复用 NoellesRoles，不要额外注册

fangroles 与 NoellesRoles 共用同一个「能力」语义，因此**默认不注册自己的按键**，
直接读取上游的 `NoellesClientKeybindings.getAbilityBind()`
（翻译 key `key.noellesroles.ability`，默认 **G** 键，分类 `category.wathe.keybinds`）。

统一通过 `FangClientKeybindings` 的这几个方法访问，**不要**直接引用上游字段或自己注册按键：

| 方法 | 用途 |
| --- | --- |
| `getAbilityBind()` | 取当前应当使用的能力键（默认即 NoellesRoles 那一个，可能为 `null`） |
| `getNoellesAbilityBind()` | 单独取 NoellesRoles 的原始按键对象 |
| `wasAbilityPressed()` | 边沿触发，一次按下只返回一次 true；无键时返回 false |
| `isAbilityPressed()` | 电平触发，按住期间每 tick true；**不要**用它做「按一次触发一次」 |
| `getAbilityBindText()` | HUD 上「按 X 使用能力」的按键名，玩家改键后自动跟随 |
| `matchesAbilityBind(KeyBinding)` | 输入锁场景判断某个键是否是能力键 |
| `registerFallbackAbilityBind()` | **仅在确实需要独立按键时**显式调用，默认不调用 |

必须遵守的三条：

1. **不要额外注册按键。** 重复注册会让「选项 → 按键绑定」出现两个「能力」项，
   而且两个键都发出同一个能力包，玩家按哪个都可能触发两次。
2. **必须懒解析，不要缓存。** `NoellesClientKeybindings.getAbilityBind()` 返回的是上游静态字段，
   由 NoellesRoles 自己的 `onInitializeClient()` 赋值。Fabric 不保证 fangroles 排在它之后，
   在 `onInitializeClient()` 里缓存会因加载顺序拿到永久 `null`（表现为按键完全没反应且难排查）。
   `FangClientKeybindings` 已按此实现，每个方法在被调用时现读一次。
3. **`wasPressed()` 只能读一次。** 它会消费按键事件，同一 tick 内多处调用只有第一处能拿到 `true`。
   所以要在唯一的能力分发器里读一次，再分发到各职业；不要在多个职业 handler 里各读一次。

后续确实需要独立按键时（例如需要独立第二能力键，或 G 键已被别处占用）：

1. 在 `FangClientKeybindings.register()` 里调用 `registerFallbackAbilityBind()`；
2. 在 `lang/zh_cn.json` 与 `lang/en_us.json` 补回 `"key.fangroles.ability"` 翻译
   （当前已移除，因为默认不注册该键；不补会显示成原始 key）；
3. 在 `AGENTS.md` 与 `README.md` 里说明为什么不能再复用上游按键。

### 7.10 床效果与床粒子（两层分工）

Wathe 的床系统被拆成**服务端效果**和**客户端粒子**两层，两者通过**同一个效果 id 字符串**串起来。
新写床相关机制时必须分清自己在哪一层，不要把两件事混在一个类里。

| 层 | 入口 | 负责什么 | 不要做什么 |
| --- | --- | --- | --- |
| 服务端 | `bed/BedEffectRegistry`（如 `bed/FangRolesBedEffects`） | 识别塞床物品、能否放置、把效果与归属者写进床头方块实体、睡觉后 40 tick 统一结算、给回放保留 `bed_effect` 字段 | 不要 mixin 床方块或 `TrimmedBedBlockEntity` 的服务端逻辑 |
| 客户端 | `bed/BedParticleRegistry`（如 `client/bed/FangRolesBedParticles`） | 只负责「这张床上显示什么粒子」，纯视觉 | 不要注入 `TrimmedBedBlockEntity.clientTick`；不要试图在这里改玩法结果 |

**id 必须共用同一常量。** `BedEffectRegistry.applyStandardEffect(...)` 会把
`effectId.toString()` 写进床方块实体的 `bed_effect` 字段；
客户端 provider 用 `context.bed().getBedEffect()` 读回来比较（返回 `String`，可能为 `null`）。
所以效果 id **必须**登记在 `registry/FangRolesEventIds`，
服务端注册处和客户端判断处都引用那一个常量 —— 手写字符串不一致是「注册了但没生效」最常见的原因。

#### 服务端接入要点

- 实现 `BedEffectHandler`（`effectId()`、`additiveItem()`、`canApply(...)`、`applyToBed(...)`、`onSleepTrigger(...)`），
  用 `BedEffectRegistry.register(handler)` 注册。
- **标准塞床逻辑直接复用**，不要自己写扣物品 / 音效 / 回放：
  `BedEffectRegistry.applyStandardEffect(player, heldStack, bed, pos, effectId, replaceExistingEffect)`；
  需要额外回放字段时用带 `replayExtra` 的重载。
- 查床用 `BedEffectRegistry.resolveHead(world, pos)`（把任意一半床解析成床头）
  或 `findTriggeredBedEffect(world, centerPos)`（复用与本体一致的半径 2 + 3D Bresenham 视线检查）。
- 「塞床」按**主手物品**识别（`getByAdditiveItem(...)` 走 `IdentityHashMap`），
  所以塞床这个动作用真实物品；只有「购买后直接生效」才用 `ShopEntry.action(...)`，两者不要混淆。

#### 客户端接入要点

- provider 放 `client/roles/<role_id>/<RoleName>BedParticleHandler`，
  由 `client/bed/FangRolesBedParticles.init()` 聚合（后者由 `FangClientParticleBootstrap` 调用）。
- **返回语义（最容易写错）**：provider 按 priority 从高到低执行；
  返回 `true` = 已处理，Wathe 停止链路；返回 `false` = 不是我的效果，继续问更低优先级。
  判断出「是我的效果但观察者无权看到」时必须返回 `true`（已识别、故意不画），
  **不能**返回 `false` —— 那会让链路继续走到更低优先级 provider，可能泄露别人的床效果信息。
- Wathe 原生蝎子床粒子注册在 `Integer.MIN_VALUE`（最低优先级），扩展可以直接覆盖或补充它的表现。
- **每 tick 调用 + 必须稀疏刷新**：provider 由床方块实体客户端 tick 调用，
  内部要先比字符串、再判权限、最后才考虑生成粒子。
  沿用 Wathe 与 NoellesRoles 的做法 `world().getRandom().nextBetween(0, 20) < 17`（约 15%）。
- **必须放 `src/client/java`**：`BedParticleRegistry` / `BedParticleContext` 引用了
  `MinecraftClient` / `ClientWorld` / `ClientPlayerEntity` 等客户端专属类，
  `src/main` 里的类**绝对不要**引用它们，否则服务端启动会崩。

参考实现：Wathe `api/bed/BedParticleRegistry#registerDefaultProvider()`（蝎子床）、
NoellesRoles `client/bed/NoellesRolesBedParticles` + `client/roles/bomber/BomberBedParticleHandler`（炸弹客床黑烟）。

### 7.11 客户端输入：`ClientInputApi`

- 统一 KeyBinding / 键盘 / 鼠标 / 滚轮 / 输入 tick 与 `ATTACK` / `ITEM_USE` / `ITEM_PICK` / `BLOCK_BREAKING`。
- priority 越大越先；`PASS` 继续、`ALLOW` 保留原版、`BLOCK` 取消；KeyBinding 可 `override`，动作可 `handled`。
- 短按用 `clearPressQueue`，离散动作用 `releaseKey`，真实物理键用 `isPhysicallyPressed`；服务端仍须校验。
- 规则放 `*InputRules`，由 `FangClientInputBootstrap` 聚合；不再 mixin 原版输入类。

### 7.12 匕首冷却与掉落物拾取

- `KnifeCooldownApi` 是流水线，后续 modifier 读前一步 `currentCooldown`；只缩短用 `Math.min`。
  不要 mixin `KnifeStabPayload.Receiver`，handler 由 `FangRolesCombatBootstrap` 聚合。
- `ItemPickupApi`：`PASS` 回退默认，`DENY` 取消，`ALLOW` 仅覆盖 Wathe 枪械限制且不跳原版副作用；
  强制 `DENY` 的 priority 要高于普通 `ALLOW`。不要 mixin `ItemEntity#onPlayerCollision`。

### 7.13 动态心情类型与结算阵营

- 运行时读 `PlayerMoodTypeApi.resolveMoodType(player)`；`Role#getMoodType()` 只是基础值，不能改。
  resolver 两端执行，条件必须来自已同步状态；HUD 用 `MoodHudContext.effectiveMoodType()`。
- `RoundEndFactionApi` 只迁移结算栏目，同时影响 `didWin`、音效、回放 `is_winner`；
  不改变实时职业 / 阵营 / 能力 / 商店 / 结束条件，死者也要按有效词条迁移。
- 不要 mixin `RoundTextRenderer`；规则分别由 `FangRolesMoodTypeBootstrap` / `FangRolesRoundEndBootstrap` 聚合。

### 7.14 重置生命周期

- 玩家级用 `ResetPlayerEvent`；世界级开局前用 `ON_BEGIN_INITIALIZE`（必须早于清背包和分配）；
  结算收尾用 `ON_FINISH_FINALIZE`。规则放 `*ResetHandler`，由 `FangRolesResetBootstrap` 聚合。
- 不要注入 `GameFunctions.resetPlayer` / `initializeGame` / `finalizeGame`，也不要恢复
  `*ReseterMixin` / `registerRoundCleanup`。

---

## 八、CCA、网络与客户端分层

### 8.1 组件

需要跨 tick、死亡、掉线、回合重置、客户端显示的状态，优先建 CCA 组件：

- 玩家状态 → entity component
- 世界共享状态 → world component
- 跨维度 / 全服唯一 → scoreboard component
- 只给本人看 → `shouldSyncWith` 限制收件人为本人
- 所有人可见的标记 / 外观 / HUD 状态 → 可同步给所有人，再由客户端 handler 判断观看者身份

新增组件三步：建组件类 → `FangRolesComponents` 注册工厂 →
`fabric.mod.json` 的 `custom.cardinal-components` 补 id（**漏第三步组件不会被分配**）。

重置入口：玩家级用 Harpy `ResetPlayerEvent`，世界级开局前用 `GameEvents.ON_BEGIN_INITIALIZE`，
结算收尾用 `ON_FINISH_FINALIZE`；具体 handler 由 `FangRolesResetBootstrap` 聚合。

### 8.2 网络

- C2S 只表达玩家意图；服务端必须重新校验职业、存活、冷却、距离、目标合法性和对局状态
- S2C 只同步客户端渲染所需状态；不要让客户端决定服务端玩法结果
- 新增包要补：payload record、codec 注册、接收器注册、客户端 receiver、初始化顺序

### 8.3 网络带宽硬约束

> 背景：此前扩展环境在 20 人以上挂机时出现持续高发送带宽，
> 主要原因是 CCA 世界 / 玩家组件每 tick 全量同步，以及回溯中每个历史帧重复同步大量数据。
> **后续新增职业或机制必须把网络开销当作和 TPS、内存一样的设计约束。**

1. 先判断状态是服务端裁定、本人 HUD、全员可见外观，还是只在事件边界显示；
   **不要因为客户端"方便读取"就每 tick 同步完整 NBT**。
2. 玩家组件默认实现 `shouldSyncWith`；只在确实需要他人观察时扩大范围，并在注释里写明原因。
3. 倒计时优先同步「开始 / 结束 / 每秒校准」，客户端本地递减或插值；
   只有会改变模型、名字、伪装或攻击目标的状态才考虑中间帧同步。
4. 世界组件**不得**在 `ServerTickingComponent#serverTick` 中无条件 `KEY.sync(world)`；
   配置、词条表、配对表和全局标记应在值变化后批量同步一次。
5. 服务端粒子、声音和实体位置更新要估算收件人数；
   可本地渲染的效果优先发送开始 / 结束 / 参数包，否则设置粒子预算和刷新间隔。
6. C2S / S2C 只传客户端必需字段；布尔值用 bit mask；角度 / 坐标按需量化；
   输入包要有变化检测或心跳间隔。
7. 不要在组件 setter 已经同步后又无条件再 `sync()`；一次状态变化尽量只产生一个组件包。

**Wathe 2026-10 已落地的同步基线（fangroles 不要反着写）：**

- 玩家私有组件（体力 / 心情 / 商店 / 中毒 / 便签 / 手雷 / 本能）只同步给本人；
  `PlayerBlackoutEffectComponent` 纯服务端，`shouldSyncWith` 恒为 `false`。
- `GameWorldComponent` 只在角色表等状态变脏时同步，不再每 20 tick 无条件广播。
- 体力 / 心情连续值按 `GameConstants.PLAYER_STATE_SYNC_INTERVAL_TICKS = 5` 校准；
  归零 / 满值 / 任务结构变化仍立即同步。
- 疯魔倒计时按 `GameConstants.PSYCHO_SYNC_INTERVAL_TICKS = 20` 校准；启动 / 结束立即同步。

网络回归检查（新增职业 / 机制至少做一遍）：

- 19~26 名玩家开局后静置 1~2 分钟，确认没有与状态变化无关的持续发送
- 激活能力前后分别记录服务器发送 Mbps、接收 Mbps、TPS 和自定义包数量
- 检查玩家加入、退出、死亡、旁观、断线重连和回合结束时是否**补发一次必要状态**，
  而不是重新开启全量 tick 同步

### 8.4 客户端分层

- HUD、屏幕、相机、模型谓词、按键、渲染 mixin → `src/client/java`
- 客户端键位 / 鼠标 / 滚轮 / 最终动作规则 → `client/input/*InputRules`，统一走 `ClientInputApi`，
  由 `FangClientInputBootstrap` 聚合；不要 mixin 原版输入类
- 服务端逻辑、物品行为、死亡、任务、胜利、组件注册 → `src/main/java`
- client mixin 注册到 `fangroles.client.mixins.json`，common / server mixin 注册到 `fangroles.mixins.json`
- **`src/main` 里的类禁止 import `src/client` 里的类**，否则服务端启动会崩

---

## 九、中文注释规范

以下位置**必须**写详细中文注释，说明「为什么」而不只是「做什么」：

1. **API 接入理由**：为什么用这个 API 而不是写 mixin；这个 API 的 priority 语义。
2. **服务端 / 客户端边界**：为什么这段逻辑必须在服务端裁定，或者为什么只是视觉。
3. **同步策略**：为什么这个组件用 `shouldSyncWith` 收窄；为什么用变化边界同步而不是每 tick。
4. **回合清理**：为什么要在这个时机清理，不清理会出现什么现象。
5. **死亡 / 复活 / 断线**：为什么要用确认死亡阶段而不是 `beforeAttempt`。
6. **胜利仲裁**：为什么返回 `keepRunning()`，与哪些职业互斥。
7. **商店扣款**：为什么用 `ACTION` 而不是给假物品。
8. **外观优先级**：为什么这个 priority 要高于 / 低于另一个。
9. **数值来源**：`*Constants` 里的每个数值代表什么设计意图。
10. **容易误解的边界**：例如「隐藏 TARGET 不等于 ATTACK 免疫」
    「可见性不是权限」「假死玩家不算真实存活」。

注释写「为什么」，不要写「这行给变量赋值」。

---

## 十、映射命名差异

本工程使用 **Yarn** 命名。跨项目复制逻辑时（尤其是从 StarryExpress 这类
Mojang 官方映射工程）必须先转换：

| Yarn / Wathe / fangroles | Mojang 官方映射 |
| --- | --- |
| `Identifier` | `ResourceLocation` |
| `Text` / `MutableText` | `Component` / `MutableComponent` |
| `ServerPlayerEntity` | `ServerPlayer` |
| `PlayerEntity` | `Player` |
| `World` / `ServerWorld` | `Level` / `ServerLevel` |
| `getDefaultStack()` | `getDefaultInstance()` |
| `getWorld()` | `level()` |
| `getUuid()` | `getUUID()` |
| `getStackInHand(...)` | `getItemInHand(...)` |
| `ItemStack` | `ItemStack`（同名，但包路径不同） |

**不要**把 StarryExpress 指南书的 Mojang 命名代码直接粘进 fangroles，也不要反过来硬粘。

---

## 十一、编译联调流程

1. 改动只在 fangroles 内 → 直接 `gradlew.bat build`。
2. 改了 Wathe / HarpyModLoader / NoellesRoles 的**公开 API**：
   0. 如果改的是 Wathe API，先确认 NoellesRoles 的 `libs/wathe-*.jar` 也换成新 Wathe，再编译 NoellesRoles；
      否则 NoellesRoles 仍会链接旧 API。
   1. 先编译上游工程，得到新的 `build/libs/*.jar`
   2. 把新 jar **覆盖**到 `fangroles/libs/`
   3. 再编译 fangroles（建议至少跑一次 `gradlew clean build`，避免增量缓存掩盖旧 jar 依赖）
   - Gradle 会通过 `findLatestLocalModJar(prefix)` 自动拾取**最后修改时间最新**的那份 jar。
   - **只编译 fangroles 是不够的**，会继续用旧 jar，表现为「明明加了新接口却找不到符号」。
3. `libs/` 里只放普通 jar，**不要**放 `-sources.jar` / `-dev.jar`。
4. 需要在 IDEA 里刷新时：先执行 `clean-idea-cache.bat`（本项目自带），再重新打开工程等 Gradle 同步。

---

## 十二、特殊时刻与优先级让位

后续新增或修改「特殊时刻 / 最终阶段 / 全场事件」时，只要它包含全场音乐、阶段专属物品、
枪械冷却、移动属性、护盾、视野或其它持续强化，**必须评估是否会与其它特殊时刻同时存在**。

当前默认优先级（若上游工程有更新，以上游为准）：
**亡命时刻最高 → 影子小丑第四阶段其次 → 执照恶棍时刻再次**。
除非用户明确要求改变，否则新的特殊时刻必须在更高优先级时刻开始后让位。

- 让位**不能只在客户端停止音乐**。服务端必须同步暂停该时刻新增的物品、冷却、属性和其它实际机制，
  否则会出现「音乐停了但能力仍生效」的状态分裂。
- 暂停时只撤销「该特殊时刻新增」的部分，不要破坏进入时刻之前已有的职业阶段和基础机制。
- 世界状态必须区分「从未进入」「正在生效」「已进入但被更高优先级时刻暂停」。
  **不能把暂停直接写成结束**，否则无法恢复原时刻，也可能重复记录进入回放。
- 暂停后的恢复仍属于同一次特殊时刻，默认不重复记录「进入时刻」回放。
- 特殊时刻临时发放的物品**必须带来源专属数据组件标记**；
  暂停或结束时只回收带该标记的副本，并同时检查在线玩家背包和地面掉落实体。
  **不要按物品类型宽泛删除。**
- 如果以「某阵营已经死亡」为触发条件，**必须使用 `Role#getFaction()` / `Faction` 的显式阵营语义**，
  不要用 `isInnocent()` / `canUseKiller()` 代替阵营。
  判断「已经死亡」时还要排除处于特殊存活状态（时间狭缝）的玩家。
- 如果让位对象是疯魔，优先用 `PsychoModeApi.pause/resume` + `PsychoModeState`，**不要**用 `stop` 模拟暂停。
  暂停快照必须进入自己的同步 / 时间线组件；恢复失败（快捷栏不足）时保留快照重试。
- 参考实现：NoellesRoles 杀手皇后在亡命时刻 `pause/resume` 同一次疯魔；
  影子小丑第四阶段 / 执照恶棍时刻只压音乐，不暂停其服务端机制。

---

## 十三、常见需求类型处理模板

### 13.1 新增职业

按第六节的 50 项清单逐项核对；先写 `*Constants`，再写逻辑，最后补翻译与资源。

### 13.2 Wathe / Harpy 接口公开化

1. 找出现有扩展的重复 mixin / helper。
2. 在 Wathe 或 Harpy 设计窄 API：注册 handler、priority、PASS 语义、上下文 record。
3. 上游原逻辑改成先询问 API，再走默认行为。
4. 把 NoellesRoles / kinssaba / fangroles 中相关逻辑接入 API。
5. 删除或停用已不需要的扩展 mixin，并同步 mixin json。
6. 同步更新上游与本工程的 README / AGENTS。
7. 编译上游 → 复制 jar 到 `libs/` → 再编译本工程。

### 13.3 修 bug

1. 先用 `rg` 搜相关类名、方法名、翻译 key、事件 id，定位最小范围。
2. 判断问题在服务端裁定、客户端显示、同步范围还是回合清理。
3. 修完检查第六节清单里与该机制相关的项（尤其是客户端缓存清理与回合清理）。
4. 编译验证。

---

## 十四、快速自检清单（提交前过一遍）

- [ ] 该用的 Wathe / Harpy 公开 API 都用了吗？有没有多余的 mixin？
- [ ] 新职业是否**显式登记阵营**？`setRoleMaximum` 设了吗？
- [ ] 新增 CCA 组件是否三步都做了（组件类、工厂注册、`fabric.mod.json`）？
- [ ] 新增客户端静态缓存是否补了 JOIN / DISCONNECT 清理？
- [ ] 回合结束清理是否成套（组件字段、静态 Map、延迟任务、临时物品、客户端缓存）？
- [ ] `*Constants` 里是否包含了全部玩法数值？有没有散落的 magic number？
- [ ] 组件的 `shouldSyncWith` 是否收窄？世界组件有没有每 tick 无条件 `sync`？
- [ ] 玩家私有组件（HUD / 商店 / 心情 / 体力 / 便签等）是否只发给本人，而不是广播给所有人？
- [ ] 连续数值是否复用 `PLAYER_STATE_SYNC_INTERVAL_TICKS`（5 tick），没有新增每 tick 同步？
- [ ] 疯魔 profile 是否登记 `cooldownItem`？跨职业托底冷却是否用 `ensureMinimumCooldown`（只延长不缩短）？
- [ ] 疯魔加时 / 加护盾 / 暂停恢复是否用 `addRemainingTicks` / `addArmourLayers` / `pause` / `resume`，
      没有直接改 `psychoTicks` / `armour`，也没有把暂停写成 `stop`？
- [ ] 新 API 是否优先使用：`ClientInputApi` / `KnifeCooldownApi` / `ItemPickupApi` /
      `PlayerMoodTypeApi` / `RoundEndFactionApi` / `PlayerStaminaApi.registerConsumptionRule`，
      没有新增对应的原版 mixin？
- [ ] 玩家 / 世界重置是否分别用 `ResetPlayerEvent` / `ON_BEGIN_INITIALIZE` / `ON_FINISH_FINALIZE`，
      没有注入 `GameFunctions.resetPlayer` / `initializeGame` / `finalizeGame`？
- [ ] C2S 包服务端是否重新校验了职业 / 存活 / 冷却 / 距离 / 目标合法性 / 对局状态？
- [ ] 能力键是否**直接复用 NoellesRoles**（`FangClientKeybindings`）而没有额外注册按键？
- [ ] 床效果 id 是否在 `FangRolesEventIds` 共用同一常量（服务端注册与客户端粒子判断都用它）？
- [ ] 床粒子 provider 是否「已识别但无权限」时返回 `true`（而不是 `false` 漏给低优先级）？
- [ ] 床粒子是否放在 `src/client/java`，且做了稀疏刷新（约 15%）？
- [ ] 翻译是否 `zh_cn.json` 和 `en_us.json` 都补了？
- [ ] 客户端 mixin 是否注册到 `fangroles.client.mixins.json`？
- [ ] `src/main` 里有没有误 import `src/client` 的类？
- [ ] 是否用了 `Set.of(...)` 装职业名单？（改成 `new LinkedHashSet<>(List.of(...))`）
- [ ] `gradlew.bat build` 是否通过？

---

## 十五、上游同步记录

### 2026-10-18

- **Wathe**：新增 `ClientInputApi`、`KnifeCooldownApi`、`ItemPickupApi`、`PlayerMoodTypeApi`、
  `RoundEndFactionApi`、`PlayerStaminaApi` 免消耗规则、`GameEvents.ON_BEGIN_INITIALIZE`、
  `MoodHudContext.effectiveMoodType()`。
- **NoellesRoles**：新增输入 / 拾取 / 重置 / 心情类型 / 结算阵营聚合入口，删除对应旧 mixin。
- **fangroles**：新增 5 个聚合入口并接入 bootstrap / 客户端入口；jar 已更新且编译通过。

### 2026-10-17

- **Wathe**：新增疯魔扩展 API `cooldownItem`、`addRemainingTicks`、`addArmourLayers`、
  `ensureMinimumCooldown`、`pause/resume` 与 `PsychoModeState`，并补充 `README_PSYCHO_MODE_API.md`；
  玩家私有组件改用 `shouldSyncWith` 只发本人，`GameWorldComponent` 改为脏标记同步，
  新增 `PLAYER_STATE_SYNC_INTERVAL_TICKS = 5` / `PSYCHO_SYNC_INTERVAL_TICKS = 20`。
- **NoellesRoles**：新增杀手皇后 `killer_queen`（标记 / 招募 / 酸液 / 疯魔 / 专属胜利 / 时停快照）；
  所有已有疯魔 profile 补 `cooldownItem`；赏金猎人德林加距离 7→15；
  `libs/wathe-*.jar` 同步为新 Wathe。
- **fangroles**：以上 jar 已覆盖 `libs/`，并执行 `gradlew clean build` 通过。
