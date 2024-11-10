package cc.mewcraft.yuuai.listener

import cc.mewcraft.yuuai.bossbar.BossBarHandler
import cc.mewcraft.yuuai.event.ScoreboardHandler
import com.destroystokyo.paper.event.server.ServerTickStartEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class NormalControlListener(
    private val bossBarHandler: BossBarHandler,
    private val scoreboardHandler: ScoreboardHandler,
) : ControlListener {
    @EventHandler
    private fun onPlayerInit(event: PlayerJoinEvent) {
        val player = event.player
        bossBarHandler.playerInit(player)
        scoreboardHandler.playerInit(player)
    }

    @EventHandler
    private fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        bossBarHandler.playerQuit(player)
        scoreboardHandler.playerQuit(player)
    }

    @EventHandler
    private fun onServerTick(event: ServerTickStartEvent) {
        val tickNumber = event.tickNumber
        bossBarHandler.serverTick(tickNumber)
        scoreboardHandler.serverTick(tickNumber)
    }
}