package org.agmas.fangroles.roles.poisoner;

import dev.doctor4t.wathe.game.GameConstants;

/**
 * 毒师全部玩法数值。
 *
 * <p>这里集中保存职业色、毒针 / 催化剂 / 毒气弹的冷却与中毒时长、毒气扩散参数、
 * 商店折扣和回放标记。后续调平衡只改本类，不要把这些数字散落到物品、实体或 mixin 里。</p>
 */
public final class PoisonerConstants {
    private PoisonerConstants() {
    }

    /** 职业色：用户指定的 RGB 30,130,30。 */
    public static final int ROLE_COLOR = 0x1E821E;

    /** 毒针开局冷却 30 秒；只影响毒师本人第一次拿到毒针后的使用节奏。 */
    public static final int START_COOLDOWN_TICKS = GameConstants.getInTicks(0, 30);

    /** 每让一个玩家从“未中毒”进入“中毒”状态奖励的金币。 */
    public static final int POISON_REWARD_COINS = 100;

    /** 毒针不可堆叠，和原版匕首一样占一个格子。 */
    public static final int POISON_NEEDLE_MAX_COUNT = 1;

    /** 毒针每次使用的物品冷却 45 秒。 */
    public static final int POISON_NEEDLE_COOLDOWN_TICKS = GameConstants.getInTicks(0, 45);

    /** 毒针给未中毒目标的初始中毒时长 40 秒。 */
    public static final int POISON_NEEDLE_POISON_TICKS = GameConstants.getInTicks(0, 40);

    /** 目标已经中毒时，毒针从剩余时间里再压缩 40 秒，最低保留 1 tick。 */
    public static final int POISON_NEEDLE_REDUCTION_TICKS = GameConstants.getInTicks(0, 40);

    /** 催化剂不可堆叠，一次性消耗品。 */
    public static final int CATALYST_MAX_COUNT = 1;

    /** 催化剂把目标剩余毒压缩到 5 秒。 */
    public static final int CATALYST_POISON_TICKS = GameConstants.getInTicks(0, 5);

    /**
     * 催化剂是否覆盖毒杀归属。
     *
     * <p>开启时沿用 NoellesRolesspark：催化剂使用者成为新的 poisoner，毒发时拿击杀奖励和回放归属。
     * 关闭时保留目标原本的 poisoner；如果原本没有 poisoner 才退回使用者。
     * 默认开启，方便需要时用常量一键切回复刻行为。</p>
     */
    public static final boolean CATALYST_OVERRIDE_POISONER = true;

    /** 毒气弹可堆叠数量。 */
    public static final int POISON_GAS_BOMB_MAX_COUNT = 16;

    /** 毒气弹投掷速度，沿用 NoellesRolesspark 的数值。 */
    public static final float POISON_GAS_BOMB_THROW_SPEED = 1.6875F;

    /** 毒气云最多占据的方块数，防止在开阔地图无限扩散。 */
    public static final int GAS_MAX_BLOCKS = 500;

    /** 毒气云生命周期 30 秒。 */
    public static final int GAS_LIFETIME_TICKS = GameConstants.getInTicks(0, 30);

    /** 每 8 tick 扩散一次，约 10 秒填满普通车厢。 */
    public static final int GAS_SPREAD_INTERVAL_TICKS = 8;

    /** 毒气最大扩散半径 20 格。 */
    public static final double GAS_MAX_RADIUS = 20.0D;

    /** 炸弹落点被不完整方块卡住时，向外寻找安全种子的最大半径。 */
    public static final int GAS_SEED_SEARCH_RADIUS = 3;

    /** 玩家在毒气里连续停留 5 秒才会中毒。 */
    public static final int GAS_EXPOSURE_THRESHOLD_TICKS = GameConstants.getInTicks(0, 5);

    /** 毒气首次中毒 20 秒。 */
    public static final int GAS_POISON_TICKS = GameConstants.getInTicks(0, 20);

    /** 目标已经中毒时，每次毒气判定再从剩余时间里压缩 20 秒。 */
    public static final int GAS_POISON_REDUCTION_TICKS = GameConstants.getInTicks(0, 20);

    /** 毒气粒子每 2 tick 生成一次，避免每 tick 发包。 */
    public static final int GAS_PARTICLE_INTERVAL_TICKS = 2;

    /** 每次粒子数量下限。 */
    public static final int GAS_PARTICLE_COUNT_MIN = 4;

    /** 每次粒子数量上限。 */
    public static final int GAS_PARTICLE_COUNT_MAX = 6;

    /** dust 粒子大小。 */
    public static final float GAS_PARTICLE_SIZE = 1.5F;

    /** 毒气粒子红色分量，和职业色保持一致。 */
    public static final float GAS_PARTICLE_RED = 30.0F / 255.0F;

    /** 毒气粒子绿色分量，和职业色保持一致。 */
    public static final float GAS_PARTICLE_GREEN = 130.0F / 255.0F;

    /** 毒气粒子蓝色分量，和职业色保持一致。 */
    public static final float GAS_PARTICLE_BLUE = 30.0F / 255.0F;

    /** 回放 extra 中标记毒针 / 毒气 / 催化剂来源的 key。 */
    public static final String POISON_SOURCE_EXTRA_KEY = "fangroles_poison_source";

    /** 毒师被动透视的 priority；要高于 Wathe 默认本能的 0。 */
    public static final int INSTINCT_HIGHLIGHT_PRIORITY = 100;

    /** 毒师商店修改器 priority。 */
    public static final int SHOP_MODIFIER_PRIORITY = 0;

    /** Wathe 默认商店读不到匕首价格时的兜底金币价。 */
    public static final int FALLBACK_KNIFE_PRICE = 100;

    /** Wathe 默认商店读不到手雷价格时的兜底金币价。 */
    public static final int FALLBACK_GRENADE_PRICE = 300;

    /** Wathe 默认商店读不到停电价格时的兜底金币价。 */
    public static final int FALLBACK_BLACKOUT_PRICE = 250;

    /** Wathe 默认商店读不到毒药瓶价格时的兜底金币价。 */
    public static final int FALLBACK_POISON_VIAL_PRICE = 70;

    /** Wathe 默认商店读不到蝎子价格时的兜底金币价。 */
    public static final int FALLBACK_SCORPION_PRICE = 40;

    /** 毒气弹价格 = 手雷默认价 - 115 金币。 */
    public static final int GAS_BOMB_PRICE_DISCOUNT = 115;

    /** 停电价格 = 停电默认价 - 100 金币。 */
    public static final int BLACKOUT_PRICE_DISCOUNT = 100;

    /** 毒药瓶 / 蝎子的半价除数。 */
    public static final int HALF_PRICE_DIVISOR = 2;
}
