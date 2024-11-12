package cc.mewcraft.yuuai.listener

import cc.mewcraft.adventurelevel.event.AdventureLevelDataLoadEvent
import cc.mewcraft.yuuai.actionbar.ActionbarHandler
import cc.mewcraft.yuuai.bossbar.BossBarHandler
import cc.mewcraft.yuuai.event.ScoreboardHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent

class AdventureLevelControlListener(
    private val actionbarManager: ActionbarHandler,
    private val bossBarHandler: BossBarHandler,
    private val scoreboardHandler: ScoreboardHandler,
) : ControlListener {
    @EventHandler
    private fun onPlayerInit(event: AdventureLevelDataLoadEvent) {
        val player = event.playerData.player ?: return
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