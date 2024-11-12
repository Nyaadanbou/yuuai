package cc.mewcraft.yuuai.bossbar

import org.bukkit.Server
import org.bukkit.entity.Player

class BossBarManager(
    private val config: BossBarConfig,
    private val server: Server
) {
    fun showBossBar(player: Player) {
        val factories = config.bossBarComponents
        for (factory in factories) {
            factory.showBossBar(player)
        }
    }

    fun reload() {
        val factories = config.bossBarComponents
        for (factory in factories) {
            server.onlinePlayers.forEach { player ->
                factory.hideBossBar(player)
                factory.showBossBar(player)
            }
        }
    }
}