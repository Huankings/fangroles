# FangRoles 中文说明与开发文档

这是一个基于 **Minecraft 1.21.1 Fabric** 的 Wathe **扩展职业 Mod 模板**。

它本身不是独立玩法，而是依赖自改版 **Wathe** + **HarpyModLoader** + **NoellesRoles** 的职业扩展层。
当前仓库是**模板骨架**：编译、构建环境、目录结构、注册点全部就绪，但**尚未实现任何具体职业**。
后续新增职业时按本文档的流程往对应位置填代码即可。

---

## 一、依赖与构建

### 1.1 运行环境依赖

| 依赖 | 版本 | 说明 |
| --- | --- | --- |
| Minecraft | 1.21.1 | |
| Fabric Loader | ≥ 0.17.2 | |
| Fabric API | 0.116.7+1.21.1 | |
| Java | 21 | 编译和运行都需要 |
| Wathe（自改版） | 1.3.3-1.21.1 | 类狼人杀玩法本体 |
| HarpyModLoader（自改版） | 1.2.3-h1.3 | 扩展职业加载与分配 |
| NoellesRoles（自改版） | 1.6-h1.3 | 扩展职业实现参考与联动 |

可选（编译期已声明，运行期非必需）：
`Ratatouille`、`Yet Another Config Lib`、`Cardinal Components API`。

### 1.2 `libs/` 目录（联调的关键）

三个上游都是**自改版**，不会发布到公共 Maven，所以它们以本地 jar 形式放在 `libs/`：

```
libs/
  wathe-1.3.3-1.21.1.jar
  harpymodloader-1.2.3-h1.3.jar
  noellesroles-1.6-h1.3.jar
```

`build.gradle` 里的 `findLatestLocalModJar(prefix)` 会自动挑选**最后修改时间最新**的那一份。
因此上游改了代码之后，**只要把新 jar 覆盖进 `libs/`，再编译本项目就行**，不需要改任何版本号。

> 只放普通 jar，**不要**把 `-sources.jar` / `-dev.jar` 放进 `libs/`（构建脚本已过滤，但仍建议保持目录干净）。
>
> **2026-10-18 同步**：`libs/` 已再次更新为最新自改版 Wathe 与 NoellesRoles 构建产物，
> 并执行 `gradlew clean build` 验证通过。本轮新增公开 API 与 fangroles 注册入口见本文第八节；
> 2026-10-17 的疯魔 / 网络改动见第七节。

### 1.3 构建

```bash
# Windows
gradlew.bat build

# macOS / Linux
./gradlew build
```

产物：

```
build/libs/fangroles-1.0.0-h1.3.jar           ← 实际使用的模组 jar
build/libs/fangroles-1.0.0-h1.3-sources.jar   ← 源码 jar
```

Gradle 版本为 **9.3.0**（wrapper 已内置，无需本机安装 Gradle）。

### 1.4 上游改动后的编译联调顺序

如果改了 Wathe / HarpyModLoader / NoellesRoles 的公开 API，必须按顺序重编：

0. 如果改的是 Wathe 的公开 API，先确认 NoellesRoles 的 `libs/wathe-*.jar` 也换成了新 Wathe，
   再编译 NoellesRoles；否则 NoellesRoles 仍会链接旧 API。
1. 编译上游工程，得到新的 `build/libs/*.jar`
2. 把新 jar **覆盖**到本工程的 `libs/`
3. 再编译本项目（建议至少跑一次 `gradlew clean build`，避免增量缓存掩盖旧 jar 依赖）

**只编译本项目是不够的** —— Gradle 会继续用旧的本地 jar，表现为「明明加了新接口却编译报错找不到符号」。

---

## 二、源码地图

### 2.1 服务端 / 公共（`src/main/java/org/agmas/fangroles/`）

| 路径 | 职责 |
| --- | --- |
| `Fangroles.java` | Fabric 主入口，**只**调用 `FangRolesBootstrap.init()` |
| `bootstrap/FangRolesBootstrap.java` | **总启动编排器**，所有初始化顺序集中在这里 |
| `FangRolesComponents.java` | CCA 组件登记入口（entity / world） |
| `ModItems.java` / `FangRolesEntities.java` / `FangRolesParticles.java` / `FangRolesSounds.java` | 物品 / 实体 / 粒子 / 声音注册 |
| `FangRolesShops.java` | 商店交付逻辑实现区 |
| `api/FangRolesApi.java` | 对外 API 门面（供下游扩展调用） |
| `registry/FangRolesCore.java` | `MOD_ID`、日志器、`id(String)` |
| `registry/FangRolesIds.java` | 职业 / 词条稳定 id 登记 |
| `registry/FangRolesRoleRegistry.java` | **职业 `Role` 实例与阵营注册** |
| `registry/FangRolesModifierRegistry.java` | Harpy 词条注册 |
| `registry/FangRolesRoleGroups.java` | 跨系统共享职业分组 |
| `registry/FangRolesDeathReasons.java` | 死亡原因 id |
| `registry/FangRolesEventIds.java` | 回放 / 效果 / profile 事件 id |
| `bootstrap/FangRolesLimitsBootstrap.java` | Harpy 职业静态最大生成数 |
| `bootstrap/FangRolesPayloadTypes.java` | 网络包 codec 注册 |
| `bootstrap/FangRolesPacketReceivers.java` | 服务端 C2S receiver 注册 |
| `bootstrap/FangRolesEventBootstrap.java` | 事件监听、server tick、回合清理（重置规则见 `FangRolesResetBootstrap`） |
| `bootstrap/FangRolesMovementBootstrap.java` | 体力（含免消耗规则）与移动速度 API 接入 |
| `bootstrap/FangRolesEconomyBootstrap.java` | 经济 / 收入 API 接入 |
| `bootstrap/FangRolesMoodTaskBootstrap.java` | 心情任务 API 接入 |
| `bootstrap/FangRolesPsychoBootstrap.java` | 疯魔 API 接入 |
| `bootstrap/FangRolesMoodTypeBootstrap.java` | 动态心情类型 resolver 聚合 |
| `bootstrap/FangRolesRoundEndBootstrap.java` | 结算阵营迁移规则聚合 |
| `pickup/FangRolesItemPickupHandlers.java` | 普通掉落物拾取规则聚合 |
| `reset/FangRolesResetBootstrap.java` | 玩家 / 世界重置规则聚合 |
| `bootstrap/FangRolesBlackoutBootstrap.java` | 停电 API 接入 |
| `bootstrap/FangRolesReplayBootstrap.java` | 回放 formatter 注册 |
| `combat/FangRolesCombatBootstrap.java` | 枪击 / 匕首冷却 / 武器目标 API 接入 |
| `death/FangRolesDeathBootstrap.java` | 击杀与死亡分阶段流程接入 |
| `collision/FangRolesPlayerCollisionHandlers.java` | 玩家碰撞 API 接入 |
| `visibility/FangRolesTargetVisibilityHandlers.java` | 目标可见性 API 接入 |
| `victory/FangRolesVictoryRules.java` | 特殊胜利规则接入 |
| `roleassign/FangRolesRoleAssignedBootstrap.java` | Harpy 分配事件与分配规则接入 |
| `shop/FangRolesShopBootstrap.java` | 商店注册 |
| `tray/FangRolesTrayEffects.java` / `bed/FangRolesBedEffects.java` | 托盘效果 / 床效果（服务端 `BedEffectRegistry`） |
| `appearance/FangRolesAppearanceHandlers.java` | 服务端外观（尸体外观 UUID） |
| `record/FangRolesReplayFormatters.java` | 回放格式化器实现区 |
| `roles/<role_id>/` | **每个职业一个包**，见该包 `package-info.java` |
| `modifiers/<modifier_id>/` | 每个词条一个包 |
| `mixin/` | Mixin 实现区（**优先用 API，尽量不要写**） |

### 2.2 客户端（`src/client/java/org/agmas/fangroles/client/`）

| 路径 | 职责 |
| --- | --- |
| `FangrolesClient.java` | Fabric 客户端主入口，**只**做初始化编排 |
| `bootstrap/FangClientRoleBootstrap.java` | 职业客户端初始化聚合（Mood HUD、时间 HUD、雾效） |
| `bootstrap/FangClientItemBootstrap.java` | 物品颜色 / Tooltip / 模型谓词 / 物品输入 |
| `bootstrap/FangClientTickBootstrap.java` | 客户端每 tick 调度 |
| `bootstrap/FangClientNetworkingBootstrap.java` | S2C receiver 注册 |
| `bootstrap/FangClientConnectionBootstrap.java` | JOIN / DISCONNECT 客户端缓存清理 |
| `bootstrap/FangClientParticleBootstrap.java` | 粒子工厂、床粒子与托盘粒子 provider |
| `bootstrap/FangClientEntityRendererBootstrap.java` | 实体渲染器注册 |
| `bootstrap/FangClientPsychoBootstrap.java` | 疯魔皮肤与背景音乐 |
| `bootstrap/FangClientMovementBootstrap.java` | 客户端移动表现 |
| `bed/FangRolesBedParticles.java` | 床粒子 provider 聚合入口（Wathe `BedParticleRegistry`） |
| `hud/FangHudHandlers.java` | 通用屏幕 HUD 总注册入口 |
| `hud/FangHudSupport.java` | 存活过滤、玩家名兜底、右下角布局辅助 |
| `crosshair/FangCrosshairHandlers.java` | 准心图标 / 小进度条 |
| `input/FangClientKeybindings.java` | 能力键读取 API（**默认直接复用 NoellesRoles 的 G 键，不额外注册**） |
| `input/FangClientInputBootstrap.java` | Wathe `ClientInputApi` 输入 / 最终动作规则聚合 |
| `input/FangClientPreAttackHandlers.java` | 左键预攻击接管 |
| `instinct/FangInstinctHandlers.java` | 本能透视 |
| `inventory/FangInventoryButtons.java` | 背包按钮与分页 |
| `inventory/FangInventoryButtonSupport.java` | 目标列表、头像、分页共享工具 |
| `mood/FangPsychosisHandlers.java` | 低心情幻觉手持物 |
| `visibility/FangHeldItemVisibilityHandlers.java` | 手持物隐藏 |
| `appearance/FangAppearanceHandlers.java` | 外观注册与本地原始皮肤缓存 |
| `roles/<role_id>/` | 每个职业的客户端代码 |
| `modifiers/<modifier_id>/` | 每个词条的客户端代码 |
| `ui/roles/<role_id>/` | 背包选人界面按钮 |
| `items/` / `particle/` / `tray/` / `renderer/` | 物品、粒子、托盘粒子、渲染实现 |
| `mixin/` | 客户端 Mixin（**必须**注册到 `fangroles.client.mixins.json`） |

### 2.3 资源文件

| 路径 | 说明 |
| --- | --- |
| `src/main/resources/fabric.mod.json` | 模组元数据、entrypoints、依赖、CCA 组件 id 清单 |
| `src/main/resources/fangroles.mixins.json` | 服务端 / 公共 Mixin 注册 |
| `src/client/resources/fangroles.client.mixins.json` | 客户端 Mixin 注册 |
| `src/main/resources/assets/fangroles/lang/zh_cn.json` | 中文翻译 |
| `src/main/resources/assets/fangroles/lang/en_us.json` | 英文翻译 |
| `src/main/resources/assets/fangroles/icon.png` | 32×32 模组图标（占位图，可替换） |
| `src/main/resources/assets/fangroles/…` | 后续补 `models/`、`textures/`、`sounds.json`、`particles/` |

---

## 三、新增一个职业的完整流程

假设要加一个平民阵营职业 `vanisher`（消失者）。

### 第 1 步：登记 id

`registry/FangRolesIds.java`

```java
public static final Identifier VANISHER_ID = FangRolesCore.id("vanisher");
```

### 第 2 步：创建 Role 并显式登记阵营

`registry/FangRolesRoleRegistry.java`

```java
public static final Role VANISHER = WatheRoles.registerCivilianRole(new Role(
        FangRolesIds.VANISHER_ID,
        0x8A2BE2,                                   // 职业色
        true,                                       // 平民：被枪击掉枪
        false,                                      // 没有杀手能力
        Role.MoodType.REAL,
        WatheRoles.CIVILIAN.getMaxSprintTime(),
        false
));
```

四个阵营的注册入口：

- 平民：`WatheRoles.registerCivilianRole(role)`
- 义警：`WatheRoles.registerVigilanteRole(role)`（会替换原版义警位）
- 杀手：`WatheRoles.registerKillerRole(role)`
- 中立：`WatheRoles.registerNeutralRole(role)`

> **必须显式登记阵营。** Harpy 的分配按 `role.getFaction()` 划分四个池子，
> 只靠 `isInnocent` / `canUseKiller` 让 Wathe 推断会导致胜利结算、RoleName 阵营色、
> 左轮冷却、被动收入上限和停电药水规则全部走错分支。

### 第 3 步：创建职业包

`src/main/java/org/agmas/fangroles/roles/vanisher/`

按 `roles/package-info.java` 里列出的文件清单建立需要的类。
不是每个职业都需要全部文件，按实际需求建：

```
VanisherConstants.java              玩法数值
VanisherRoleAssignmentRules.java    Harpy 分配规则
VanisherRoleAssignedHandler.java    分配后发物品 / 重置状态
VanisherShopHandler.java            职业商店
VanisherInteractionHandler.java     右键交互
VanisherPlayerComponent.java        CCA 组件（需要持久状态时）
VanisherVictoryRule.java            特殊胜利（如需要）
```

### 第 4 步：设置最大生成数

`bootstrap/FangRolesLimitsBootstrap.java`

```java
Harpymodloader.setRoleMaximum(FangRolesRoleRegistry.VANISHER, 1);
```

### 第 5 步：写 Harpy 分配规则

`roles/vanisher/VanisherRoleAssignmentRules.java`，用
`org.agmas.harpymodloader.api.assignment.RoleAssignmentApi`：

- 同局互斥 / 单向排斥：`registerMutualExclusion(...)` / `registerOneWayExclusion(...)`
- 绑定生成 / 阶段补位：`registerBeforePhaseHandler(...)` / `registerAfterPhaseHandler(...)`
  （阶段：`CIVILIAN_REPLACEMENT`、`VIGILANTE_REPLACEMENT`、`KILLER_REPLACEMENT`；
  写最终职业用 `RoleAssignmentPhaseContext.assignRole(...)`）

**不要**新增 `ModdedMurderGameMode#findAndAssignPlayers` 或 `assignModifiers` 的 Mixin。

### 第 6 步：在 Bootstrap 里接上

按职责在 `bootstrap/FangRolesBootstrap.java` 里补一行调用，例如：

```java
FangRolesLimitsBootstrap.initStaticLimits();      // 第 4 步已在这里面
FangRolesRoleAssignedBootstrap.init();            // 第 5 步：在这里调用 VanisherRoleAssignmentRules.init()
FangRolesShopBootstrap.init();                    // 商店
```

具体职业的 handler 由对应的聚合类调用，例如
`FangRolesShopBootstrap.init()` 里调用 `VanisherShopHandler.init()`。

### 第 7 步：补翻译

`lang/zh_cn.json` 和 `lang/en_us.json` 都要补：

```json
"announcement.role.fangroles.vanisher": "消失者",
"announcement.goals.fangroles.vanisher": "在被发现之前让所有人消失"
```

Harpy 会自动生成 announcement，但**仍然要自己补翻译**，否则显示成原始 key。

### 第 8 步：客户端部分（如有需要）

- 右下角状态 HUD → `client/roles/vanisher/VanisherStatusHud.java`，
  在 `client/hud/FangHudHandlers.register()` 里调用它的 `register()`
- 准心图标 → `client/roles/vanisher/VanisherCrosshairHandler.java`，
  在 `client/crosshair/FangCrosshairHandlers.register()` 里接上
- 能力键 → `client/roles/vanisher/VanisherAbilityInputHandler.java`
- 背包选人 → `client/ui/roles/vanisher/VanisherInventoryButtons.java`

### 第 9 步：逐项核对注册点

用 `AGENTS.md` 的「注册点检查清单」逐条过关，再编译。

---

## 四、调试

### 4.1 Harpy 指令

| 指令 | 用途 |
| --- | --- |
| `/setRole <player> <role>` | 局内转职。默认 `reset` 模式：保留 Wathe 本局钥匙和信件，不传送、不播结算，并重新发送开局公告。可显式加 `reset` / `state` / `soft` |
| `/forceRole <player> <role>` | 强制下一次 Harpy 开局的职业，开局后清空。**会计入权重** |
| `/forceModifier <player> <modifier>` | 强制下一次开局的词条。不直接改变职业权重 |
| `/roleWeights list [all\|online\|stored]` | 查看权重账本 |
| `/roleWeights preview faction\|role <target>` | 预览概率，不抽人、不写历史 |
| `/roleWeights enabled <true\|false>` | 权重总开关（全服，存在 Wathe 的 scoreboard） |
| `/roleWeights reset all\|online\|storedOffline\|player\|uuid` | 重置历史 |
| `/roleWeights set player … faction\|role …` | 手动覆盖权重 |
| `/roleWeights clearOverride player …` | 只清覆盖，不清历史 |
| `/listRoles` / `/setEnabledRole` / `/setEnabledModifier` | 查看与启停职业 / 词条 |

> 局内 `/setRole` 调试转职**不计入**权重历史。

### 4.2 Wathe 指令

| 指令 | 用途 |
| --- | --- |
| `/wathe:start harpymodloader:modded` | 用扩展职业模式开局 |
| `/wathe:setMood <0-1> [players]` | 设置心情值 |
| `/wathe:moodStaminaPenalty [mid\|depressive] <true\|false>` | 心情体力惩罚开关 |
| `/wathe:blackout trigger` / `restore` / `overlay <0-100>` / `potionEffects <true\|false>` | 停电调试 |
| `/wathe:playerCollision` / `/wathe:startnoCollision` | 碰撞开关 |
| `/wathe:shopCooldownBypass <true\|false>` | 是否允许 `ITEM` 类商品在冷却期间购买 |
| `/wathe:gameSettings weights reset` | 清空权重历史 |

### 4.3 联调排查顺序

职业没生成时，按这个顺序查：

1. `Harpymodloader.ROLE_MAX` 是否设了（上限 0 或未设都可能不生成）
2. Role 是否真的注册进了 `WatheRoles.ROLES`
3. 阵营是否正确（用 `/roleWeights preview` 看候选池里有没有它）
4. 开局是不是用的 `harpymodloader:modded` 模式
5. 是否被别的职业的分配规则排斥掉了

---

## 五、开发规范（摘要，完整版见 `AGENTS.md`）

1. **能用 Wathe 公开 API 就不要写 Mixin。** 写之前先核对 `AGENTS.md` 的「公开 API 优先表」。
2. **关键代码必须写中文注释**，尤其是：为什么这么接 API、服务端 / 客户端边界、
   同步范围、回合清理、胜利仲裁、商店扣款、外观优先级。
3. **玩法数值放 `*Constants`**，冷却统一用 `GameConstants.getInTicks(min, sec)` 或明确的 tick 常量。
   `*Constants` 里不要写职业 RGB（那是注册参数）。
4. **服务端才是权威。** C2S 包只表达玩家意图，服务端必须重新校验职业、存活、冷却、距离、
   目标合法性和对局状态。S2C 只同步渲染所需状态。
5. **网络带宽是设计约束。** 玩家私有组件默认用 `shouldSyncWith` 只发给本人；
   世界组件只在状态变脏时同步，不要每 tick 无条件 `sync`；倒计时同步「开始 / 结束 / 周期校准」，
   客户端本地递减。连续数值校准优先用 `GameConstants.PLAYER_STATE_SYNC_INTERVAL_TICKS`（5 tick），
   疯魔倒计时校准用 `GameConstants.PSYCHO_SYNC_INTERVAL_TICKS`（20 tick）。
6. **回合结束必须成套清理**：CCA 组件字段、静态 Map、延迟任务、临时物品（按来源标记回收）、客户端缓存。
   玩家级硬重置用 Harpy `ResetPlayerEvent`；新局分配前必须清的世界状态用 `GameEvents.ON_BEGIN_INITIALIZE`；
   结算画面结束后的收尾用 `GameEvents.ON_FINISH_FINALIZE`。三个时机不能混用。
7. **不要用 `Set.of(...)` 装职业名单** —— 重复元素会在启动时抛异常导致服务器起不来，
   要用 `new LinkedHashSet<>(List.of(...))`。
8. **改了上游 API 就按顺序重编并传递 jar**（见 1.4）。
9. **能力键直接复用 NoellesRoles 的 G 键，不要额外注册。** 统一通过
   `FangClientKeybindings.getAbilityBind()` / `wasAbilityPressed()` / `getAbilityBindText()` 读取，
   见 5.1。
10. **本轮新公开的 API 优先使用**：客户端键位 / 鼠标 / 最终动作走 `ClientInputApi`，匕首冷却走
    `KnifeCooldownApi`，普通掉落物拾取走 `ItemPickupApi`，动态心情类型走 `PlayerMoodTypeApi`，
    结算栏目迁移走 `RoundEndFactionApi`，体力免消耗走 `PlayerStaminaApi.registerConsumptionRule`。
    具体语义与 fangroles 注册入口见第八节。

### 5.1 能力键：复用 NoellesRoles（不额外注册）

fangroles 与 NoellesRoles 共用同一个「能力」语义，所以**默认不注册自己的按键**，
直接读取 NoellesRoles 的 `NoellesClientKeybindings.getAbilityBind()`
（翻译 key `key.noellesroles.ability`，默认 **G** 键，分类 `category.wathe.keybinds`）。

| 方法 | 用途 |
| --- | --- |
| `FangClientKeybindings.getAbilityBind()` | 取当前能力键（默认即 NoellesRoles 那一个，可能为 `null`） |
| `FangClientKeybindings.wasAbilityPressed()` | 边沿触发，一次按下只返回一次 true；无键时返回 false |
| `FangClientKeybindings.isAbilityPressed()` | 电平触发，按住期间每 tick true（用于按住蓄力） |
| `FangClientKeybindings.getAbilityBindText()` | HUD 上「按 X 使用能力」的按键名，玩家改键自动跟随 |
| `FangClientKeybindings.matchesAbilityBind(KeyBinding)` | 输入锁场景判断某键是否是能力键 |
| `FangClientKeybindings.registerFallbackAbilityBind()` | **仅在确实需要独立按键时**调用，默认不调用 |

这样做的效果：玩家只按一次 G 就能触发能力；在 NoellesRoles 那条按键上改键后 fangroles 自动跟随；
「选项 → 按键绑定」里也不会出现两条重复的「能力」项。

两点必须注意：

- **不要缓存按键实例。** 上游静态字段由 NoellesRoles 自己的客户端入口赋值，
  Fabric 不保证 fangroles 排在它之后；`FangClientKeybindings` 已改为每次调用现读，不要改回缓存。
- **`wasPressed()` 一个 tick 只能读一次。** 它会消费按键事件，所以要在唯一的能力分发器里读一次再分发给各职业。

后续确实需要独立按键时：在 `FangClientKeybindings.register()` 里调用 `registerFallbackAbilityBind()`，
并在 `lang/zh_cn.json` 与 `lang/en_us.json` 补回 `"key.fangroles.ability"` 翻译。

---

## 六、目录结构总览

```
fangroles/
├── build.gradle                 构建脚本（含 libs 本地 jar 自动拾取）
├── settings.gradle
├── gradle.properties            版本与依赖坐标
├── gradlew / gradlew.bat        Gradle 9.3.0 wrapper
├── gradle/wrapper/
├── LICENSE.txt                  LGPL-3.0
├── README.md                    本文件
├── AGENTS.md                    开发协作规范（中文）
├── clean-idea-cache.bat/.ps1    IDEA 缓存清理
├── libs/                        三个上游自改版 jar
└── src/
    ├── main/
    │   ├── java/org/agmas/fangroles/    服务端与公共逻辑
    │   └── resources/                   fabric.mod.json / mixins / assets
    └── client/
        ├── java/org/agmas/fangroles/client/  客户端逻辑
        └── resources/                       fangroles.client.mixins.json
```

---

## 七、上游最新改动与接入要点（2026-10-17 同步）

本轮已把 **Wathe 1.3.3-1.21.1** 与 **NoellesRoles 1.6-h1.3** 的最新自改版 jar 覆盖到 `libs/`，
并执行 `gradlew clean build` 验证通过。下面是本次上游更新中会直接影响后续职业开发的部分。

### 7.1 Wathe：疯魔 API 扩展

完整说明见上游 `README_PSYCHO_MODE_API.md`。新增能力：

| API | 作用 | 约束 |
| --- | --- | --- |
| `PsychoModeProfile.Builder#cooldownItem(Item)` | 声明该 profile 对应的商店图标 / 冷却物品 | 可选；默认 profile 已绑定 `WatheItems.PSYCHO_MODE`；API 不会自动设置普通购买冷却 |
| `PsychoModeApi.addRemainingTicks(player, profileId, ticks)` | 给指定 profile 加时，并同步扩大 HUD 最大时间 | 只对当前仍处于该 profile 的玩家生效；`ticks <= 0` 返回 `false` |
| `PsychoModeApi.addArmourLayers(player, profileId, layers)` | 动态增加护盾层数 | 只接受正数；整数上限饱和；只对当前 profile 生效 |
| `PsychoModeApi.ensureMinimumCooldown(player, profileId, ticks)`（另有 `profile` 重载） | 把 profile 图标的剩余冷却托底到指定时间 | 已有冷却更长时保持不变，**只延长不缩短**；profile 未登记 `cooldownItem` 时返回 `false` |
| `PsychoModeApi.pause(player)` → `PsychoModeState` | 暂停当前疯魔并返回可恢复快照 | 回收 profile 临时物品、移出活跃计数、**不写结束回放** |
| `PsychoModeApi.resume(player, state)` | 恢复同一次疯魔 | 重新走统一授予 / 锁栏流程，再恢复时间和护盾；快捷栏不足会失败，调用方需保留快照重试 |

`PsychoModeState` 只保存 `profileId`、剩余时间、HUD 最大时间、当前护盾和初始护盾；
**不包含**背包、物品冷却和扩展职业自己的组件，这些仍由调用方管理。

接入要求：

- 自定义疯魔只要有自己的购买图标，就必须登记 `cooldownItem`，否则跨职业托底冷却找不到目标物品。
- 加时、加护盾、暂停、恢复全部走上述 API，**不要**直接改 `PlayerPsychoComponent#psychoTicks` / `armour`。
- `pause` 不是结束。只是被更高优先级时刻临时压制时用 `pause`；真正结束仍用 `stop(player, recordReplay)`。
- 暂停快照必须存进自己的同步组件 / 时间线组件，并在恢复失败时保留重试。

### 7.2 Wathe：网络同步收敛（重要基线）

本次 Wathe 把「谁需要收到组件」收窄了一层。fangroles 新增组件时应直接沿用：

- 玩家私有组件默认只发给本人：`PlayerStaminaComponent`、`PlayerMoodComponent`、`PlayerShopComponent`、
  `PlayerPoisonComponent`、`PlayerNoteComponent`、`PlayerGrenadeComponent`、`PlayerInstinctComponent`
  都实现了 `shouldSyncWith(recipient) { return recipient.equals(this.player); }`。
- `PlayerBlackoutEffectComponent` 是纯服务端归属状态，当前 `shouldSyncWith` 恒为 `false` 且不再 `KEY.sync`；
  客户端没有读取它的 HUD / 玩法。
- `GameWorldComponent` 不再每 20 tick 无条件广播；`addRole` / `resetRole` / `clearRoleMap` 只标记脏状态，
  在 `serverTick` 末尾合并同步一次。
- 疯魔组件服务端每秒校准一次（`GameConstants.PSYCHO_SYNC_INTERVAL_TICKS = 20`），启动 / 结束仍然立即同步。
- 体力、心情的连续数值按 `GameConstants.PLAYER_STATE_SYNC_INTERVAL_TICKS = 5` 校准；
  归零、到达上限、任务结构变化等关键边界仍立即同步。

对 fangroles 的约束：

- 新增玩家组件先问「别人真的需要看到吗」；只有确实需要他人观察时才扩大 `shouldSyncWith`，并写注释说明。
- 不要为了客户端方便就重新每 tick `sync`；服务端 API 读取任意玩家真实状态仍然可用。
- 新增长倒计时优先「开始 / 结束 / 周期校准」，客户端本地递减。
- 新增连续数值同步时优先复用这两个常量，不要写死 5 / 20。

### 7.3 NoellesRoles：杀手皇后（`killer_queen`）

NoellesRoles 本次新增杀手侧中立职业 **杀手皇后**，是研究「疯魔 + 招募 + 专属胜利 + 时停回溯」的好参考：

- 职业：`roles/killer_queen/`，杀手侧中立，职业色 RGB(80,180,250)，`MoodType.FAKE`，无限冲刺，`MAX_ROLE_COUNT = 1`。
- 标记：酸液（托盘）或疯魔期间的标记法杖给好人 / 独立中立添加标记；主动本能把已标记目标显示为独立紫色。
- 招募：G 键 2.5 格内目标；对杀手阵营花 100 金币洗牌职业，对已标记好人 / 独立中立花 150 金币招募
  （模仿者可免标记和费用）；被招募者被迫掉落左轮、获得刀，并被限制只能击杀指定目标，
  完成后转成随机杀手并获得 200 金币。招募机会是整局共享资源，杀手阵营死亡会增加机会；
  被招募者死亡会返还机会，目标失效会重新选择。
- 酸液：50 金币；杀手皇后或机器人喝下获得 2.1 倍移速 15 秒，其他玩家获得反胃 + 失明 10 秒；
  好人 / 独立中立消费者本人和投放酸液的杀手皇后各得 50 金币。
- 标记法杖：疯魔 profile 临时授予，1.0 攻击速度，命中不致死而是定身 5 秒；
  首次标记合法目标 +10 秒疯魔，每累计 2 个首次标记 +1 层护盾；重复命中只刷新定身。
- 疯魔：52 秒、1 层护盾、350 金币、4 分 30 秒图标冷却；疯魔音乐为 `ambient.killer_queen`。
  压制其他杀手侧疯魔并托底其图标冷却 1 分 30 秒；召集者能力冷却持续托底 4 秒。
  亡命时刻用 `PsychoModeApi.pause/resume` 暂停同一次疯魔；
  影子小丑第四阶段 / 执照恶棍时刻只压音乐，不暂停服务端机制。
- 胜利：只要有一名杀手皇后仍存活，且所有存活好人 / 独立中立都被自己标记（排除时间狭缝），立即独胜；
  胜者包含自己、所有杀手阵营、杀手中立和已招募玩家；未完成标记时返回 `pass()`，不拖住正常结算。
- 组件：`KillerQueenWorldComponent` 保存按杀手皇后 UUID 分开的标记、招募、疯魔暂停快照和压制 profile；
  `KillerQueenPlayerComponent` 保存酸液移速 / 晕眩计时；两者都进入时停者快照，回合结束清理。
- 回放：新增 14 个 `killer_queen_*` 全局事件、1 个酸液托盘效果 id，并补酸液托盘放置回放句式；`zh_cn.json` / `en_us.json` 均已补。
- 客户端：`client/roles/killer_queen/*` 提供状态 HUD、G 键输入、疯魔音乐与托盘粒子；
  `client/instinct/roles/killer_queen/*` 负责已标记目标的紫色本能，`client/appearance/roles/killer_queen/*` 负责同伙 HUD。

### 7.4 NoellesRoles：疯魔图标冷却统一登记

NoellesRoles 给所有已有疯魔 profile 补了 `cooldownItem`：
`BOUNTY_MODE`、`PSYCHO_COOK`、`PSYCHO_JASON`、`PSYCHO_LICH`、`EVIL_POSSESS`、`SPRING_TRAP`、`PSYCHO_VECNA`。
新增自定义疯魔时照此登记，才能使用 `ensureMinimumCooldown` 做跨职业冷却托底。

其他同期小改动：

- 赏金猎人德林加有效距离从 7 格调整为 15 格（`BountyHunterConstants.BOUNTY_DERRINGER_RANGE_BLOCKS`）。
- 杀手皇后调试购买允许旁观 / 创造模式绕过即时商品冷却。

### 7.5 fangroles 侧落地检查

- 新增疯魔职业：profile 写 `*Constants` + `cooldownItem`；加时 / 护盾 / 暂停全部走 API。
- 新增「特殊时刻压制疯魔」：优先 `pause/resume`，不要把暂停写成 `stop`。
- 新增玩家组件：先实现 `shouldSyncWith`，再考虑是否需要同步。
- 要参考完整实现时读 NoellesRoles 的 `roles/killer_queen/` 和 `client/roles/killer_queen/`；
  不要复制它的具体平衡数值，只复用结构。

---

## 八、上游最新 API 增量与注册入口（2026-10-18 同步）

本轮再次把 Wathe / NoellesRoles 最新构建覆盖到 `libs/`，并执行 `gradlew clean build` 通过。
新增内容以公开 API 为主，fangroles 已按 NoellesRoles 的拆分方式增加对应聚合入口。

### 8.1 客户端输入与最终动作：`ClientInputApi`

完整说明见 Wathe `README_CLIENT_INPUT_API.md`。

- 统一调度 KeyBinding、键盘、鼠标按钮、滚轮、输入 tick，以及 `ATTACK` / `ITEM_USE` /
  `ITEM_PICK` / `BLOCK_BREAKING` 最终动作。
- 规则按 priority 从高到低执行；同优先级后注册者先执行；相同 id 再次注册会替换。
- `PASS` 继续、`ALLOW` 保留原版并停止低优先级、`BLOCK` 取消；
  KeyBinding 可用 `KeyResult.override(boolean)`，最终动作可用 `ActionResult.handled(boolean)`。
- 输入残留必须用 `clearPressQueue(key)` / `releaseKey(key)` 清理；
  特殊相机读取真实物理按键用 `isPhysicallyPressed(...)`，不要自行 accessor `timesPressed` / `boundKey`。
- 只负责客户端体验和竞态兜底；攻击、物品使用、职业能力和 C2S 包仍必须由服务端校验。
- 扩展按「一个客户端输入聚合类 + 多个职业 / 词条 `*InputRules`」拆分，
  不要再 mixin `KeyBinding` / `Keyboard` / `Mouse` / `MinecraftClient` 的输入方法。

### 8.2 匕首冷却：`KnifeCooldownApi`

- `registerCooldownModifier(id, priority, modifier)` 注册流水线；后一个 modifier 读取前一步的
  `currentCooldown`，最终自动钳制为非负数。
- `KnifeCooldownContext` 提供攻击者、目标、服务端物品快照、对局组件、基础冷却和
  `defaultCooldownApplies`。
- 只缩短冷却时用 `Math.min(currentCooldown, targetTicks)`，不要覆盖其它规则更短的结果。
- 不要 mixin `KnifeStabPayload.Receiver`。
- 职业 / 词条规则放 `roles/<role>/<RoleName>KnifeCooldownHandler` 或对应 modifier 包，
  由 `FangRolesCombatBootstrap` 聚合。

### 8.3 普通掉落物拾取：`ItemPickupApi`

- `registerRule(id, priority, rule)` 返回 `PASS` / `ALLOW` / `DENY`。
- `PASS` 继续，全部 PASS 后回退 Wathe 默认枪械拾取限制；`DENY` 取消本次拾取；
  `ALLOW` 只覆盖 Wathe 职业 / 枪械限制，仍然进入原版拾取流程。
- `ALLOW` 不会跳过拾取延迟、owner、背包空间、统计、进度准则和 Wathe 拾取回放。
- 强制缴械 / 重伤 / 招募等 `DENY` 必须使用高于普通 `ALLOW` 特权的 priority。
- 只接管普通 `ItemEntity`，不接管箭、三叉戟和扩展自定义投射物。
- 不要 mixin `ItemEntity#onPlayerCollision`；规则放 `*ItemPickupHandler`，
  由 `FangRolesItemPickupHandlers` 聚合。

### 8.4 动态心情类型：`PlayerMoodTypeApi`

- `Role#getMoodType()` 是职业注册基础值，不能用来判断某个玩家运行时类型，也不能直接改共享 Role。
- `PlayerMoodTypeApi.registerResolver(id, priority, resolver)`：`pass()` 继续，
  `override(REAL|FAKE|NONE)` 接管；无职业玩家固定 NONE。
- resolver 同时运行在服务端和客户端 HUD，条件必须来自两端已同步状态；
  不得改组件、发任务、发包或递归解析类型。
- 服务端掉心情、任务奖励、阈值补槽、精神崩溃、任务发放资格和客户端预测 / HUD 都读取有效类型。
- FAKE 会把底层心情归一为 1，转回 REAL 从满值开始；已有任务保留，转换不会冒充完成或发金币。
- 自定义 HUD 用 `MoodHudContext.effectiveMoodType()`，不要读 `role().getMoodType()`。
- 规则放 `*MoodTypeHandler`，由 `FangRolesMoodTypeBootstrap` 聚合（当前空入口）。

### 8.5 结算阵营迁移：`RoundEndFactionApi`

- `registerRule(id, priority, rule)` 返回 `pass()` 或 `moveTo(Faction)`；
  在服务端生成结算快照时执行。
- 迁移同时影响普通阵营栏目、`didWin`、个人胜负音效和回放 `is_winner`；
  保留真实职业名 / 颜色、头像和死亡状态。
- 不改变实时职业、阵营、杀手能力、商店、同伙关系、分配池或对局结束条件；
  独立胜利 / 共胜仍由 `VictoryApi` / `CustomVictory` 仲裁。
- 规则不要以「玩家仍存活」为前提，否则带词条的死者会错误回到原阵营。
- 不要 mixin `RoundTextRenderer`，也不要改 `RoundEndData` 构造；
  规则放 `*RoundEndHandler`，由 `FangRolesRoundEndBootstrap` 聚合（当前空入口）。

### 8.6 有限体力免消耗：`PlayerStaminaApi.registerConsumptionRule`

- 规则返回 `PASS` / `CONSUME` / `SKIP`；`SKIP` 跳过正常消耗，但不回满、不提高上限。
- Wathe 疾跑 / 低落走路分别以 `SPRINT` / `WALK` 调用；
  扩展能力成本用 `consumeStamina(player, amount, CUSTOM)`。
- `drainStamina` / `clearStamina` / `setStamina` 是强制改值入口，
  不受免消耗规则影响，适合调试和惩罚技能。
- 免消耗不解除低心情禁跑，也不解除体力归零后的自主移动 / 跳跃限制。
- 纯消耗规则放 `*StaminaHandler`，由 `FangRolesMovementBootstrap` 聚合；
  不要再用「巨大上限 + 每 tick 回满」抵消 Wathe 的扣除。

### 8.7 新局初始化前事件：`GameEvents.ON_BEGIN_INITIALIZE`

- 在 Wathe 清背包、分配职业和扩展模式初始化之前触发，
  专门清理上一局还留在世界组件里的配对、倒计时、标记等状态。
- 不要后移到 `ON_FINISH_INITIALIZE`，也不要再注入 `GameFunctions.initializeGame`。
- 玩家级清理仍用 Harpy `ResetPlayerEvent`；结算后的临时实体 / 管理器收尾用 `ON_FINISH_FINALIZE`。
- fangroles 统一由 `reset/FangRolesResetBootstrap` 聚合各 `*ResetHandler`。

### 8.8 fangroles 新增的注册入口

本轮新增的聚合类默认都是**空入口**，只负责后续调用各职业 / 词条 handler 的 `init()`：

| 入口 | 负责的 Wathe API | 未来规则放置位置 |
| --- | --- | --- |
| `bootstrap/FangRolesMoodTypeBootstrap.java` | `PlayerMoodTypeApi` | `roles/<role>/<RoleName>MoodTypeHandler` / `modifiers/<modifier>/*MoodTypeHandler` |
| `bootstrap/FangRolesRoundEndBootstrap.java` | `RoundEndFactionApi` | `*RoundEndHandler` |
| `pickup/FangRolesItemPickupHandlers.java` | `ItemPickupApi` | `*ItemPickupHandler` |
| `reset/FangRolesResetBootstrap.java` | `ResetPlayerEvent` / `ON_BEGIN_INITIALIZE` / `ON_FINISH_FINALIZE` | `*ResetHandler` |
| `client/input/FangClientInputBootstrap.java` | `ClientInputApi` | `client/roles/<role>/*InputRules` / `client/modifiers/<modifier>/*InputRules` |

此外：

- `FangRolesMovementBootstrap` 已补 `registerConsumptionRule` / `consumeStamina` 说明，
  后续 `*StaminaHandler.init()` 也在这里调用。
- `FangRolesCombatBootstrap` 已补 `KnifeCooldownApi` 说明，
  后续 `*KnifeCooldownHandler.init()` 也在这里调用。
- `FangRolesBootstrap` 已按顺序接入上述聚合类；`FangrolesClient` 已接入 `FangClientInputBootstrap`。

NoellesRoles 侧的对应参考实现：

- `client/input/NoellesClientInputBootstrap` + 各职业 `*InputRules`
- `pickup/NoellesItemPickupHandlers` + 各职业 `*ItemPickupHandler`
- `combat/NoellesRolesCombatBootstrap` + 各职业 `*KnifeCooldownHandler`
- `bootstrap/NoellesRolesMoodTypeBootstrap`、`bootstrap/NoellesRolesRoundEndBootstrap`
- `reset/NoellesRolesResetBootstrap` + 各职业 `*ResetHandler`
- `roles/cook/CookStaminaHandler`（体力免消耗的完整范例）


