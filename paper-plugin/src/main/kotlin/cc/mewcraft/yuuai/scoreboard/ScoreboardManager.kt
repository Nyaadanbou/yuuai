package cc.mewcraft.yuuai.scoreboard

import cc.mewcraft.yuuai.Injector
import cc.mewcraft.yuuai.YuuaiPlugin
import cc.mewcraft.yuuai.component.ScoreboardComponent
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.util.*
import kotlin.collections.set

class ScoreboardManager : KoinComponent {
    private val plugin: YuuaiPlugin by inject()
    private val config: ScoreboardConfig by inject()

    private val scoreboards: Object2ObjectOpenHashMap<UUID, Scoreboard> = Object2ObjectOpenHashMap()

    fun showScoreboard(player: Player) {
        val layout = config.layout
        val scoreboardComponents = config.scoreboardComponents

        val lines = mutableMapOf<Key, ScoreboardComponentData>()

        for ((index, line) in layout.withIndex()) {
            val lineKey = Key.key(line)
            val scoreboardComponent = scoreboardComponents.find { it.namespace == lineKey.namespace() }

            if (scoreboardComponent == null) {
                plugin.componentLogger.warn("No scoreboard component found for $lineKey")
                continue
            }

            lines[lineKey] = ScoreboardComponentData(index, scoreboardComponent)
        }

        val scoreboard = scoreboards.computeIfAbsent(player.uniqueId, Object2ObjectFunction { _ -> Scoreboard(player, lines) })

        scoreboard.title(Component.text("Mewcraft"))
        scoreboard.show()
    }

    fun removeScoreboard(player: Player) {
        scoreboards[player.uniqueId]?.hide()
        scoreboards.remove(player.uniqueId)
    }

    fun setLine(player: Player, scoreboardComponent: ScoreboardComponent, changedValue: String? = null) {
        scoreboards[player.uniqueId]?.line(scoreboardComponent, changedValue)
    }

    fun reload() {
        scoreboards.values.forEach { it.hide() }
        scoreboards.clear()
        plugin.server.onlinePlayers.forEach { showScoreboard(it) }
    }

    fun close() {
        scoreboards.values.forEach { it.hide() }
        scoreboards.clear()
        Injector.get<ScoreboardLibrary>().close()
    }
}