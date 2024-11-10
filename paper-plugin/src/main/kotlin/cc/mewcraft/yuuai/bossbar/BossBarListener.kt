package cc.mewcraft.yuuai.bossbar

import com.destroystokyo.paper.event.server.ServerTickStartEvent
import org.bukkit.Server
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BossBarListener : Listener, KoinComponent {
    private val server: Server by inject()
    private val bossBarManager: BossBarManager by inject()

    @EventHandler
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        bossBarManager.showBossBar(player)
    }

    @EventHandler
    private fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        bossBarManager.removeBossBar(player)
    }

    @EventHandler
    private fun onServerTick(event: ServerTickStartEvent) {
        if (event.tickNumber % 20 == 0) {
            server.onlinePlayers.forEach { player ->
                bossBarManager.showBossBar(player)
            }
        }
    }
}