package cc.mewcraft.yuuai.actionbar

import cc.mewcraft.yuuai.PLUGIN_DATA_DIR
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.nio.file.Path

const val ACTIONBAR_PATH = "actionbar.yml"

internal fun actionbarModule(): Module = module {
    single<ActionBarConfig> {
        val loader = YamlConfigurationLoader.builder()
            .path(get<Path>(named(PLUGIN_DATA_DIR)).resolve(ACTIONBAR_PATH))
            .nodeStyle(NodeStyle.BLOCK)
            .build()
        ActionBarConfig(loader, get(), get())
    }

    singleOf(::ActionbarManager)
    singleOf(::ActionbarHandler)
}