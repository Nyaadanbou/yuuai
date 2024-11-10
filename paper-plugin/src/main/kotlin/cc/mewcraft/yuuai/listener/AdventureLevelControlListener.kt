package cc.mewcraft.yuuai.listener

import cc.mewcraft.adventurelevel.event.AdventureLevelDataLoadEvent
import cc.mewcraft.yuuai.bossbar.BossBarHandler
import cc.mewcraft.yuuai.event.ScoreboardHandler
import com.destroystokyo.paper.event.server.ServerTickStartEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent

class AdventureLevelControlListener(
    private val bossBarHandler: BossBarHandler,
    private val scoreboardHandler: ScoreboardHandler,
) : ControlListener {
    @EventHandler
    private fun onPlayerInit(event: AdventureLevelDataLoadEvent) {
        val player = event.playerData.player ?: return
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