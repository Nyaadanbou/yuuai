package cc.mewcraft.yuuai.bossbar

import cc.mewcraft.yuuai.PLUGIN_DATA_DIR
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.nio.file.Path

const val BOSS_BAR_PATH = "bossbar.yml"

internal fun bossBarModule(): Module = module {
    single<BossBarConfig> {
        val loader = YamlConfigurationLoader.builder()
            .path(get<Path>(named(PLUGIN_DATA_DIR)).resolve(BOSS_BAR_PATH))
            .nodeStyle(NodeStyle.BLOCK)
            .build()
        BossBarConfig(loader, get(), get())
    }

    singleOf(::BossBarHandler)
    singleOf(::BossBarManager)
}