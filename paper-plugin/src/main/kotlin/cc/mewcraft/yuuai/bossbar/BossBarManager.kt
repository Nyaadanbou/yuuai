package cc.mewcraft.yuuai.bossbar

import org.bukkit.entity.Player

class BossBarManager(
    private val config: BossBarConfig
) {
    fun showBossBar(player: Player) {
        val factories = config.bossBarFactories
        for (factory in factories) {
            factory.showBossBar(player)
        }
    }
}