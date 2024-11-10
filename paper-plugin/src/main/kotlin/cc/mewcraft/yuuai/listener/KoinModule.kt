package cc.mewcraft.yuuai.listener

import cc.mewcraft.yuuai.YuuaiPlugin
import org.koin.core.module.Module
import org.koin.dsl.module

fun listenerModule(): Module = module {
    single<ControlListener> {
        if (get<YuuaiPlugin>().isPluginPresent("AdventureLevel")) {
            return@single AdventureLevelControlListener(get(), get())
        }
        NormalControlListener(get(), get())
    }
}