package cc.mewcraft.yuuai

import cc.mewcraft.yuuai.actionbar.ACTIONBAR_PATH
import cc.mewcraft.yuuai.actionbar.ActionbarManager
import cc.mewcraft.yuuai.actionbar.actionbarModule
import cc.mewcraft.yuuai.bossbar.BOSS_BAR_PATH
import cc.mewcraft.yuuai.bossbar.BossBarManager
import cc.mewcraft.yuuai.bossbar.bossBarModule
import cc.mewcraft.yuuai.command.CommandManager
import cc.mewcraft.yuuai.command.commandModule
import cc.mewcraft.yuuai.event.YuuaiReloadEvent
import cc.mewcraft.yuuai.listener.ControlListener
import cc.mewcraft.yuuai.listener.listenerModule
import cc.mewcraft.yuuai.scoreboard.SCOREBOARD_PATH
import cc.mewcraft.yuuai.scoreboard.ScoreboardManager
import cc.mewcraft.yuuai.scoreboard.scoreboardModule
import cc.mewcraft.yuuai.text.textModule
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import me.lucko.helper.Schedulers
import me.lucko.helper.plugin.KExtendedJavaPlugin
import org.bukkit.event.Listener
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class YuuaiPlugin : KExtendedJavaPlugin() {

    private fun Listener.register() {
        server.pluginManager.registerSuspendingEvents(this, this@YuuaiPlugin)
    }

    override suspend fun load() {
        startKoin {

            modules(
                yuuaiModule(this@YuuaiPlugin),

                actionbarModule(),
                bossBarModule(),
                commandModule(),
                listenerModule(),
                scoreboardModule(),
                textModule()
            )
        }
    }

    override suspend fun enable() {
        // Reload the configuration
        reload()

        /* Initialize managers */

        Injector.get<CommandManager>().init()

        /* Register listeners */

        Injector.get<ControlListener>().register()
    }

    override suspend fun disable() {
        Injector.get<ActionbarManager>().close()
        Injector.get<ScoreboardManager>().close()
        stopKoin()
    }

    internal fun reload() {
        Schedulers.sync().run {
            saveDefaultConfig()
            reloadConfig()
            saveResource(ACTIONBAR_PATH)
            saveResource(BOSS_BAR_PATH)
            saveResource(SCOREBOARD_PATH)

            YuuaiReloadEvent().callEvent()
            Injector.get<ActionbarManager>().reload()
            Injector.get<BossBarManager>().reload()
            Injector.get<ScoreboardManager>().reload()
        }
    }
}