package cc.mewcraft.yuuai.util

import cc.mewcraft.yuuai.Injector
import cc.mewcraft.yuuai.YuuaiPlugin
import cc.mewcraft.yuuai.event.YuuaiReloadEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.slf4j.Logger
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T> reloadable(
    onLoad: (T) -> Unit = {},
    onUnload: (T?) -> Unit = {},
    loader: () -> T,
) = Reloadable(onLoad, onUnload, loader)

class Reloadable<T>(
    private val onLoad: (T) -> Unit,
    private val onUnload: (T?) -> Unit,
    private val loader: () -> T,
) : ReadOnlyProperty<Any?, T> {
    companion object : KoinComponent {
        private val logger: Logger by inject()
    }

    private var value: T? = null

    init {
        Injector.get<YuuaiPlugin>().server.pluginManager.registerEvents(ReloadableListener(), Injector.get())
    }

    fun get(): T {
        val value = this.value
        if (value == null) {
            val createdValue = loader()
            this.value = createdValue
            try {
                onLoad(createdValue)
            } catch (e: Exception) {
                logger.error("加载配置时发生了一个错误", e)
            }
            return createdValue
        }
        return value
    }

    private fun reload() {
        try {
            onUnload(value)
        } catch (e: Exception) {
            logger.error("卸载配置时发生了一个错误", e)
        }
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