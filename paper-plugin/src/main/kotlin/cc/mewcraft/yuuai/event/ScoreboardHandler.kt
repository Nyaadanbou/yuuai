package cc.mewcraft.yuuai.event

import cc.mewcraft.yuuai.scoreboard.ScoreboardManager
import org.bukkit.entity.Player

class ScoreboardHandler(
    private val scoreboardManager: ScoreboardManager
) {

    fun playerInit(player: Player) {
        scoreboardManager.showScoreboard(player)
    }

    fun playerQuit(player: Player) {
        scoreboardManager.removeScoreboard(player)
    }
}