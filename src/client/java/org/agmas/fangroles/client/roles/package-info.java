/**
 * fangroles 的职业客户端实现区。
 *
 * <h2>目录约定：一个职业一个子包</h2>
 * <pre>{@code
 * client/roles/<role_id>/
 *   <RoleName>ClientBootstrap.java    该职业客户端初始化的聚合入口
 *   <RoleName>StatusHud.java          普通屏幕 HUD provider（HudOverlayApi）
 *   <RoleName>CrosshairHandler.java   准心图标 / 小进度条（CrosshairHudApi）
 *   <RoleName>AbilityInputHandler.java 能力键目标解析与按下边沿状态
 *   <RoleName>PreAttackHandler.java   左键预攻击接管（如需要）
 *   <RoleName>FogHandler.java         职业专属雾效（FogOverrideApi）
 *   <RoleName>MovementClientHandler.java 移动表现（FOV / 步伐 / 屏幕效果）
 *   <RoleName>ClientState.java        纯客户端缓存（不进时停快照）
 *   <RoleName>ClientTickHandler.java  需要按 tick 推进的客户端状态
 * }</pre>
 *
 * <h2>职责边界（很重要）</h2>
 * <ul>
 *   <li>本包<b>只</b>放渲染、输入、客户端缓存。
 *       任何影响玩法结果（伤害、判定、奖励）的逻辑必须在 {@code src/main} 的服务端侧。</li>
 *   <li>本包的类<b>禁止</b>被 {@code src/main} 里的类 import，
 *       否则服务端会因为找不到客户端类而启动失败。</li>
 *   <li>本包的类如果需要读服务端状态，只能通过 CCA 同步组件读，
 *       <b>不要</b>自己推演服务端逻辑。</li>
 * </ul>
 *
 * <h2>纯客户端缓存的清理要求</h2>
 * <p>本包内任何静态缓存都必须在
 * {@code client/bootstrap/FangClientConnectionBootstrap} 的
 * JOIN / DISCONNECT 清理中处理，否则换服务器会残留上一个服务器的显示状态。</p>
 */
package org.agmas.fangroles.client.roles;
