package cc.mewcraft.yuuai.bossbar

import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent

class BossBarHandler(
    private val bossBarManager: BossBarManager,
) : KoinComponent {

    fun playerInit(player: Player) {
        bossBarManager.showBossBar(player)
    }

    fun playerQuit(player: Player) {
//        bossBarManager.removeBossBar(player)
    }
}