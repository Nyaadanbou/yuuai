@file:Suppress("UnstableApiUsage")

package cc.mewcraft.yuuai.command

import io.papermc.paper.command.brigadier.CommandSourceStack
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal fun commandModule(): Module = module {
    single<PaperCommandManager<CommandSourceStack>> {
        PaperCommandManager.builder()
            .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
            .buildOnEnable(get())
    }

    singleOf(::CommandManager)
}