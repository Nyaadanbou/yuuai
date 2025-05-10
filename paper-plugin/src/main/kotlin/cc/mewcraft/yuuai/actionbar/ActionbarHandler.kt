package cc.mewcraft.yuuai.actionbar

import org.bukkit.entity.Player

class ActionbarHandler(
    private val actionbarManager: ActionbarManager,
) {

    fun playerInit(player: Player) {
        actionbarManager.showActionbar(player)
    }

    fun playerQuit(player: Player) {
        actionbarManager.hideActionbar(player)
    }
}