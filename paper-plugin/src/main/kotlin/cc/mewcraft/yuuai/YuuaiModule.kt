package cc.mewcraft.yuuai

import com.google.gson.Gson
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import me.lucko.helper.plugin.KExtendedJavaPlugin
import me.lucko.helper.plugin.KHelperPlugin
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.bukkit.Server
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.module.Module
import org.koin.core.module.dsl.binds
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.slf4j.Logger
import java.io.File
import java.nio.file.Path

const val PLUGIN_DATA_DIR = "plugin_data_dir"

internal fun yuuaiModule(plugin: YuuaiPlugin): Module = module {
    single<YuuaiPlugin> {
        plugin
    } withOptions {
        createdAtStart()
        binds(listOf(Plugin::class, JavaPlugin::class, KHelperPlugin::class, KExtendedJavaPlugin::class))
    }

    single<Server> { plugin.server }

    single<PluginManager> { plugin.server.pluginManager }

    single<LifecycleEventManager<Plugin>> { plugin.lifecycleManager }

    single<ComponentLogger> { plugin.componentLogger } bind Logger::class

    single<Gson> { Gson() }

    // 配置文件
    single<File>(named(PLUGIN_DATA_DIR)) { plugin.dataFolder }
    single<Path>(named(PLUGIN_DATA_DIR)) { get<File>(named(PLUGIN_DATA_DIR)).toPath() }
}