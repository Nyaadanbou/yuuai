package cc.mewcraft.yuuai.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class YuuaiReloadEvent : Event() {

    override fun getHandlers() = HANDLERS

    companion object {
        @JvmStatic
        val HANDLERS = HandlerList()
        @JvmStatic
        fun getHandlerList() = HANDLERS
    }
}