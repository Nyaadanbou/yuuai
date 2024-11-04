package cc.mewcraft.yuuai

import cc.mewcraft.yuuai.scoreboard.ScoreboardManager
import cc.mewcraft.yuuai.scoreboard.scoreboardModule
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class YuuaiPlugin : JavaPlugin() {
    override fun onEnable() {
        startKoin {

            modules(
                yuuaiModule(this@YuuaiPlugin),

                scoreboardModule()
            )
        }
    }

    override fun onDisable() {
        Injector.get<ScoreboardManager>().close()
        stopKoin()
    }
}