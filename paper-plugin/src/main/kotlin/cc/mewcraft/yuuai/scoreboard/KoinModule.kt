package cc.mewcraft.yuuai.scoreboard

import cc.mewcraft.yuuai.PLUGIN_DATA_DIR
import cc.mewcraft.yuuai.event.ScoreboardHandler
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
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

    singleOf(::ScoreboardHandler)
}