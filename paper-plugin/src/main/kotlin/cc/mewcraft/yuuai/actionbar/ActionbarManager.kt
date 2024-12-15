package cc.mewcraft.yuuai.actionbar

import org.bukkit.Server
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ActionbarManager(
    private val config: ActionBarConfig,
    private val server: Server
) {
    private val actionbars: ConcurrentHashMap<UUID, Actionbar> = ConcurrentHashMap()

    fun showActionbar(player: Player) {
        val components = config.actionBarComponents
        val text = config.format
        if (text.isBlank())
            return

        actionbars.computeIfAbsent(player.uniqueId) { Actionbar(player, text, components) }
            .show()
    }

    fun hideActionbar(player: Player) {
        actionbars.remove(player.uniqueId)?.hide()
    }

    fun reload() {
        val uuids = actionbars.keys.toTypedArray()
        actionbars.values.forEach(Actionbar::hide)
        actionbars.clear()
        for (uuid in uuids) {
            val player = server.getPlayer(uuid) ?: continue
            showActionbar(player)
        }
    }

    fun close() {
        actionbars.values.forEach(Actionbar::cancel)
        actionbars.clear()
    }
}