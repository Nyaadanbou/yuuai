package cc.mewcraft.yuuai.bossbar

import net.kyori.adventure.bossbar.BossBar
import org.bukkit.entity.Player

interface BossBarFactory {
    /**
     * 是否启用 BossBar.
     */
    val isEnable: Boolean

    /**
     * 创建一个 BossBar.
     */
    fun createBossBar(player: Player): BossBar

    fun refreshBossBar(player: Player, old: BossBar)
}