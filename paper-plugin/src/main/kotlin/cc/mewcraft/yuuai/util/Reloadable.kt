package cc.mewcraft.yuuai.util

import cc.mewcraft.yuuai.Injector
import cc.mewcraft.yuuai.YuuaiPlugin
import cc.mewcraft.yuuai.event.YuuaiReloadEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.koin.core.component.get
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T> reloadable(loader: () -> T) = Reloadable(loader)

class Reloadable<T>(private val loader: () -> T) : ReadOnlyProperty<Any?, T> {
    private var value: T? = null

    init {
        Injector.get<YuuaiPlugin>().server.pluginManager.registerEvents(ReloadableListener(), Injector.get())
    }

    fun get(): T {
        val value = this.value
        if (value == null) {
            val createdValue = loader()
            this.value = createdValue
            return createdValue
        }
        return value
    }

    private fun reload() {
        value = null
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return get()
    }

    private inner class ReloadableListener : Listener {
        @EventHandler
        fun onReload(event: YuuaiReloadEvent) {
            reload()
        }
    }
}