package cc.mewcraft.yuuai.component

import org.bukkit.entity.Player

/**
 * 表示玩家 ui 内的 BossBar 组件.
 *
 * bossbar 由组件管理. 一个组件之管理一个 bossbar.
 */
interface BossBarComponent : YuuaiComponent {
    /**
     * 将组件管理的 BossBar 显示在 [player] 的 ui.
     */
    fun showBossBar(player: Player)

    fun hideBossBar(player: Player)
}