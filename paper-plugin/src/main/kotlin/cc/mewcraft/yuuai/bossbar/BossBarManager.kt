package cc.mewcraft.yuuai.bossbar

import org.bukkit.Server
import org.bukkit.entity.Player

class BossBarManager(
    private val config: BossBarConfig,
    private val server: Server,
) {
    fun showBossBar(player: Player) {
        val factories = config.bossBarComponents
        for (factory in factories) {
            factory.showBossBar(player)
        }
    }

    fun reload() {
        // 重载过后这里是新的 factory, 不需要调用卸载逻辑.
        server.onlinePlayers.forEach { player ->
            showBossBar(player)
        }
    }
}