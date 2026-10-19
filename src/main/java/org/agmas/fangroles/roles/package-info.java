/**
 * fangroles 的职业实现区。
 *
 * <h2>目录约定：一个职业一个子包</h2>
 * <pre>{@code
 * roles/<role_id>/
 *   <RoleName>Constants.java              玩法数值：冷却、时长、距离、速度、颜色以外的平衡参数
 *   <RoleName>RoleAssignmentRules.java    Harpy 分配规则：同局互斥、绑定生成、阶段补位
 *   <RoleName>RoleAssignedHandler.java    分配后初始化：发开局物品、重置状态、设置开局冷却
 *   <RoleName>ShopHandler.java            该职业商店
 *   <RoleName>InteractionHandler.java     右键 / 交互逻辑
 *   <RoleName>GunHandler.java             枪击接管（如需要）
 *   <RoleName>GunCooldownHandler.java     开火冷却修正（如需要）
 *   <RoleName>DeathHandler.java           死亡保护 / 击杀奖励 / 确认死亡后清理
 *   <RoleName>MovementHandler.java        体力与移动速度修正
 *   <RoleName>PlayerCollisionHandler.java 玩家碰撞模式
 *   <RoleName>VisibilityHandler.java      目标可见性规则
 *   <RoleName>BlackoutHandler.java        停电夜视 / 失明规则
 *   <RoleName>PsychoHandler.java          疯魔 profile 与护盾规则
 *   <RoleName>MoodTaskHandler.java        心情任务注册
 *   <RoleName>VictoryRule.java            特殊胜利规则
 *   <RoleName>PlayerComponent.java        CCA 玩家组件（需要持久 / 同步状态时）
 *   <RoleName>WorldComponent.java         CCA 世界组件（共享状态时）
 *   <RoleName>Manager.java                复杂状态机总管（参考魔术师 / 时停者）
 *   <RoleName>ReplayFormatters.java       该职业的回放格式化器
 * }</pre>
 *
 * <h2>为什么要拆这么细</h2>
 * <p>NoellesRoles 的实践结论是：一个职业的所有逻辑塞进一个类后，
 * 一旦超过 500 行就无法维护，而且不同 API 的接入点（枪击 / 死亡 / 移动 / 可见性）
 * 混在一起会让「改一个数值影响三处机制」。
 * 按 API 领域拆文件后，每个文件只对接一个上游 API，回归测试范围也清晰。</p>
 *
 * <h2>客户端对应目录</h2>
 * <pre>{@code
 * src/client/java/org/agmas/fangroles/client/roles/<role_id>/
 *   <RoleName>StatusHud.java              普通屏幕 HUD provider（HudOverlayApi）
 *   <RoleName>CrosshairHandler.java       准心图标 / 小进度条（CrosshairHudApi）
 *   <RoleName>AbilityInputHandler.java    能力键目标解析与输入状态
 *   <RoleName>FogHandler.java             职业专属雾效（FogOverrideApi）
 *   <RoleName>ClientState.java            纯客户端缓存（不进时停快照）
 * }</pre>
 *
 * <h2>不要做的事</h2>
 * <ul>
 *   <li>不要把多个职业的规则合并到一个公共大类里。</li>
 *   <li>不要为了省一个文件就把逻辑写进 {@code bootstrap/} 的聚合类。</li>
 *   <li>客户端专属渲染 / 输入代码不要放在本包（{@code src/main}），
 *       那会导致服务端加载不到类而崩溃。</li>
 * </ul>
 */
package org.agmas.fangroles.roles;
