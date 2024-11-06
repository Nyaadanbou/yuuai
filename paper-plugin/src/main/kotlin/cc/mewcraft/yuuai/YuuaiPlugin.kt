package cc.mewcraft.yuuai

import cc.mewcraft.yuuai.event.ScoreboardListener
import cc.mewcraft.yuuai.event.YuuaiReloadEvent
import cc.mewcraft.yuuai.scoreboard.SCOREBOARD_PATH
import cc.mewcraft.yuuai.scoreboard.ScoreboardManager
import cc.mewcraft.yuuai.scoreboard.scoreboardModule
import cc.mewcraft.yuuai.text.textModule
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class YuuaiPlugin : JavaPlugin() {

    private fun Listener.register() {
        server.pluginManager.registerEvents(this, this@YuuaiPlugin)
    }

    override fun onEnable() {
        startKoin {

            modules(
                yuuaiModule(this@YuuaiPlugin),

                scoreboardModule(),
                textModule()
            )
        }
        Injector.get<ScoreboardListener>().register()

        reload()
    }

    override fun onDisable() {
        Injector.get<ScoreboardManager>().close()
        stopKoin()
    }

    fun reload() {
        saveDefaultConfig()
        reloadConfig()
        saveResource(SCOREBOARD_PATH, false)
        Injector.get<ScoreboardManager>().reload()

        YuuaiReloadEvent().callEvent()
    }
}