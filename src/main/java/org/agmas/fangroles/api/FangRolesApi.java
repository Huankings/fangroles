package org.agmas.fangroles.api;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 的对外 API 门面。
 *
 * <p>本模组自己也是一个可以再被扩展的模组（就像 NoellesRoles 被 kinssaba 依赖）。
 * 因此凡是希望「别的模组能稳定调用」的入口，都收敛到这里，
 * <b>不要</b>让外部代码直接引用 {@code registry/}、{@code roles/} 里的内部实现类。</p>
 *
 * <h2>为什么要单独做门面</h2>
 * <p>NoellesRoles 的经验是：早期其它扩展直接读主入口类的字段和职业注册表，
 * 后来一旦重构，所有下游扩展全部编译失败。
 * 有了门面层后，内部重构只要保持本类签名不变，下游就不受影响。</p>
 *
 * <h2>当前提供的能力</h2>
 * <ul>
 *   <li>{@link #MOD_ID}：本模组 id，供下游拼资源路径和组件 id</li>
 *   <li>{@link #id(String)}：生成 {@code fangroles:xxx} 形式的 {@code Identifier}</li>
 *   <li>{@link #logger()}：统一日志器</li>
 * </ul>
 *
 * <h2>新增对外接口的建议</h2>
 * <ul>
 *   <li>只暴露「读」接口和「注册规则」接口，不要暴露内部可变状态。</li>
 *   <li>返回职业时返回 Wathe 的 {@code Role} 类型，不要返回本模组的包装类。</li>
 *   <li>新增接口后要在 {@code README.md} 和 {@code AGENTS.md} 里补说明，
 *       并在 jar 版本号上做体现。</li>
 * </ul>
 */
public final class FangRolesApi {
    /** 本模组的 mod id。改动它会导致所有资源路径、组件 id、网络包 id 全部失效。 */
    public static final String MOD_ID = FangRolesCore.MOD_ID;

    private FangRolesApi() {
    }

    /**
     * 生成 fangroles 命名空间下的 {@code Identifier}。
     *
     * <p>示例：{@code FangRolesApi.id("my_thing")} → {@code fangroles:my_thing}</p>
     */
    public static net.minecraft.util.Identifier id(String path) {
        return FangRolesCore.id(path);
    }

    /** 供下游扩展复用的统一日志器。 */
    public static org.slf4j.Logger logger() {
        return FangRolesCore.LOGGER;
    }
}
