package cc.mewcraft.yuuai.scoreboard

import cc.mewcraft.yuuai.PLUGIN_DATA_DIR
import cc.mewcraft.yuuai.YuuaiPlugin
import cc.mewcraft.yuuai.event.ScoreboardHandler
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.slf4j.Logger
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.nio.file.Path

const val SCOREBOARD_PATH = "sidebar.yml"

internal fun scoreboardModule(): Module = module {
    single { ScoreboardManager() }

    single<ScoreboardConfig> {
        val loader = YamlConfigurationLoader.builder()
            .path(get<Path>(named(PLUGIN_DATA_DIR)).resolve(SCOREBOARD_PATH))
            .nodeStyle(NodeStyle.BLOCK)
            .build()
        ScoreboardConfig(loader, get())
    }

    single<ScoreboardLibrary> {
        try {
            ScoreboardLibrary.loadScoreboardLibrary(get<YuuaiPlugin>())
        } catch (e: NoPacketAdapterAvailableException) {
            // If no packet adapter was found, you can fallback to the no-op implementation:
            get<Logger>().warn("No scoreboard packet adapter available!")
            NoopScoreboardLibrary()
        }
    }

    singleOf(::ScoreboardHandler)
}