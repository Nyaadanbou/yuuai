package cc.mewcraft.yuuai.event

import cc.mewcraft.yuuai.scoreboard.ScoreboardManager
import org.bukkit.Server
import org.bukkit.entity.Player

class ScoreboardHandler(
    private val scoreboardManager: ScoreboardManager,
    private val server: Server
) {

    fun playerInit(player: Player) {
        scoreboardManager.createScoreboard(player)
    }

    fun playerQuit(player: Player) {
        scoreboardManager.removeScoreboard(player)
    }

    fun serverTick(tickNumber: Int) {
        if (tickNumber % 20 == 0) {
            for (player in server.onlinePlayers) {
                scoreboardManager.refreshScoreboard(player)
            }
        }
    }
}