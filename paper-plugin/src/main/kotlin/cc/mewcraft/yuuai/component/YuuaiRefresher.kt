package cc.mewcraft.yuuai.component

import com.destroystokyo.paper.event.server.ServerTickStartEvent
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * 代表一个用于刷新组件内系统的对象.
 */
interface YuuaiRefresher : Listener

class ServerTickRefresher(
    private val tickPerRefresh: Int,
    private val action: (Player) -> Unit
) : YuuaiRefresher {
    companion object : KoinComponent {
        private val server: Server by inject()
    }

    @EventHandler
    private fun onTick(e: ServerTickStartEvent) {
        if (e.tickNumber % tickPerRefresh == 0) {
            server.onlinePlayers.forEach { action(it) }
        }
    }
}