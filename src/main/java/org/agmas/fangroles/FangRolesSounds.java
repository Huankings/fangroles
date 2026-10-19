package org.agmas.fangroles;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 的声音事件注册入口。
 *
 * <h2>注册写法</h2>
 * <pre>{@code
 * public static final SoundEvent VANISHER_CAST = register("vanisher_cast");
 *
 * private static SoundEvent register(String path) {
 *     Identifier id = FangRolesCore.id(path);
 *     return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
 * }
 * }</pre>
 *
 * <h2>必须补齐的资源</h2>
 * <ol>
 *   <li>{@code assets/fangroles/sounds.json}：声音事件到音频文件的映射</li>
 *   <li>{@code assets/fangroles/sounds/<name>.ogg}：音频文件</li>
 *   <li>需要字幕时补 {@code subtitles.fangroles.<name>} 翻译</li>
 * </ol>
 *
 * <h2>播放位置与收件人</h2>
 * <ul>
 *   <li>只有附近玩家该听到的声音，用 {@code world.playSound(null, pos, ...)}，
 *       不要用广播接口。</li>
 *   <li>只有本人该听到的声音，用
 *       {@code player.playSoundToPlayer(...)} 或 S2C 包定向发送。</li>
 *   <li>全场音乐属于「特殊时刻」范畴，要评估与其它职业特殊时刻的优先级，
 *       详见 {@code AGENTS.md} 的「特殊时刻与优先级让位」。</li>
 * </ul>
 */
public final class FangRolesSounds {
    private FangRolesSounds() {
    }

    /**
     * 注册所有声音事件。
     *
     * <p>由 {@code FangRolesBootstrap.init()} 调用。</p>
     */
    public static void initialize() {
        // 目前没有自定义声音，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 声音注册完成（当前为空）");
    }
}
