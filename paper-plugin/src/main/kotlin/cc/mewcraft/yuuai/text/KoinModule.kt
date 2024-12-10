package cc.mewcraft.yuuai.text

import net.kyori.adventure.text.minimessage.MiniMessage
import org.koin.core.module.Module
import org.koin.dsl.module

internal fun textModule(): Module = module {
    single<MiniMessage> { MiniMessage.miniMessage() }
}