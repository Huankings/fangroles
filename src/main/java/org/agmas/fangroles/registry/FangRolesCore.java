package org.agmas.fangroles.registry;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * fangroles 最基础的模组标识入口。
 *
 * <p>所有注册类统一从这里取 {@link #MOD_ID} 和 {@link #id(String)}，
 * 避免把主入口类 {@code Fangroles} 当成公共常量仓库使用。</p>
 *
 * <p>这里的 {@code MOD_ID} 同时决定了：
 * <ul>
 *   <li>资源命名空间：{@code assets/fangroles/...} 与 {@code data/fangroles/...}</li>
 *   <li>CCA 组件 id 前缀：{@code fangroles:xxx}</li>
 *   <li>网络包 id 前缀：{@code fangroles:xxx}</li>
 *   <li>翻译 key 前缀：{@code announcement.role.fangroles.xxx} 等</li>
 * </ul>
 * 所以一旦发布后就不要再改。</p>
 */
public final class FangRolesCore {
    public static final String MOD_ID = "fangroles";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private FangRolesCore() {
    }

    /** 生成本模组命名空间下的 {@link Identifier}，例如 {@code id("some_role")} → {@code fangroles:some_role}。 */
    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
