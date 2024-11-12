package cc.mewcraft.yuuai.listener

import cc.mewcraft.yuuai.actionbar.ActionbarHandler
import cc.mewcraft.yuuai.bossbar.BossBarHandler
import cc.mewcraft.yuuai.event.ScoreboardHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class NormalControlListener(
    private val actionbarManager: ActionbarHandler,
    private val bossBarHandler: BossBarHandler,
    private val scoreboardHandler: ScoreboardHandler,
) : ControlListener {
    @EventHandler
    private fun onPlayerInit(event: PlayerJoinEvent) {
        val player = event.player
        actionbarManager.playerInit(player)
        bossBarHandler.playerInit(player)
        scoreboardHandler.playerInit(player)
    }

    @EventHandler
    private fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        actionbarManager.playerQuit(player)
        bossBarHandler.playerQuit(player)
        scoreboardHandler.playerQuit(player)
    }
}