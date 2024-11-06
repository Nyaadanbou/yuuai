package cc.mewcraft.yuuai.event

import cc.mewcraft.yuuai.scoreboard.ScoreboardManager
import com.destroystokyo.paper.event.server.ServerTickStartEvent
import org.bukkit.Server
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class ScoreboardListener(
    private val scoreboardManager: ScoreboardManager,
    private val server: Server
) : Listener {
    @EventHandler
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        scoreboardManager.createScoreboard(event.player)
    }

    @EventHandler
    private fun onPlayerQuit(event: PlayerQuitEvent) {
        scoreboardManager.removeScoreboard(event.player)
    }

    @EventHandler
    private fun onServerTick(event: ServerTickStartEvent) {
        if (event.tickNumber % 20 == 0) {
            for (player in server.onlinePlayers) {
                scoreboardManager.refreshScoreboard(player)
            }
        }
    }
}