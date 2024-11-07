package cc.mewcraft.yuuai.scoreboard

import cc.mewcraft.yuuai.YuuaiPlugin
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import kotlin.collections.forEach
import kotlin.collections.set

class ScoreboardManager : KoinComponent {
    private val plugin: YuuaiPlugin by inject()
    private val config: ScoreboardConfig by inject()

    private val scoreboardLibrary: ScoreboardLibrary

    private val sidebars: Object2ObjectOpenHashMap<UUID, ScoreboardData> = Object2ObjectOpenHashMap()

    init {
        scoreboardLibrary = try {
            ScoreboardLibrary.loadScoreboardLibrary(plugin)
        } catch (e: NoPacketAdapterAvailableException) {
            // If no packet adapter was found, you can fallback to the no-op implementation:
            plugin.componentLogger.warn("No scoreboard packet adapter available!")
            NoopScoreboardLibrary()
        }
    }

    fun createScoreboard(player: Player) {
        val layout = config.layout
        val scoreboardParts = config.scoreboardParts

        val sidebarComponentBuilder = SidebarComponent.builder()

        for (line in layout) {
            val lineKey = Key.key(line)
            val sidebarComponent = scoreboardParts.mapNotNull { part ->
                when (val result = part.sidebarComponent(lineKey, player)) {
                    is SidebarComponentResult.Success -> {
                        result.value
                    }

                    else -> null
                }
            }

            if (sidebarComponent.isEmpty()) {
                plugin.componentLogger.warn("No valid sidebar component found for line $line")
                continue
            }

            if (sidebarComponent.size > 1) {
                plugin.componentLogger.warn("Multiple sidebar components found for line $line")
            }

            sidebarComponentBuilder.addComponent(sidebarComponent.first())
        }
        val titleSidebarComponent = SidebarComponent.builder()
            .addStaticLine(Component.text("Mewcraft"))
            .build()

        val sidebarLayouts = ComponentSidebarLayout(titleSidebarComponent, sidebarComponentBuilder.build())
        val sidebar = scoreboardLibrary.createSidebar(15, player.locale())

        sidebarLayouts.apply(sidebar)
        sidebar.addPlayer(player)
        sidebars[player.uniqueId] = ScoreboardData(sidebarLayouts, sidebar)
    }

    fun removeScoreboard(player: Player) {
        sidebars[player.uniqueId]?.sidebar?.removePlayer(player)
        sidebars.remove(player.uniqueId)
    }

    fun refreshScoreboard(player: Player) {
        val data = sidebars[player.uniqueId]
            ?: return
        data.refresh()
    }

    fun reload() {
        sidebars.values.forEach { it.sidebar.close() }
        sidebars.clear()
        plugin.server.onlinePlayers.forEach { createScoreboard(it) }
    }

    fun close() {
        scoreboardLibrary.close()
    }
}

private data class ScoreboardData(
    val layout: ComponentSidebarLayout,
    val sidebar: Sidebar,
) {
    fun refresh() {
        layout.apply(sidebar)
    }
}