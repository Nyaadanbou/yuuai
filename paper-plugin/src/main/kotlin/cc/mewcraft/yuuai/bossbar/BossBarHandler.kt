package cc.mewcraft.yuuai.bossbar

import org.bukkit.Server
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent

class BossBarHandler(
    private val server: Server,
    private val bossBarManager: BossBarManager,
) : KoinComponent {

    fun playerInit(player: Player) {
        bossBarManager.showBossBar(player)
    }

    fun playerQuit(player: Player) {
        bossBarManager.removeBossBar(player)
    }

    fun serverTick(tickNumber: Int) {
        if (tickNumber % 20 == 0) {
            server.onlinePlayers.forEach { player ->
                bossBarManager.showBossBar(player)
            }
        }
    }
}