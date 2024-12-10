@file:Suppress("UnstableApiUsage")

package cc.mewcraft.yuuai.command.command

import cc.mewcraft.yuuai.Injector
import cc.mewcraft.yuuai.YuuaiPlugin
import cc.mewcraft.yuuai.command.CommandConstants
import cc.mewcraft.yuuai.command.CommandPermissions
import cc.mewcraft.yuuai.command.buildAndAdd
import cc.mewcraft.yuuai.command.suspendingHandler
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.incendo.cloud.Command
import org.incendo.cloud.CommandFactory
import org.incendo.cloud.CommandManager
import org.incendo.cloud.description.Description
import org.incendo.cloud.kotlin.extension.commandBuilder
import org.koin.core.component.get
import kotlin.system.measureTimeMillis

object YuuaiCommand : CommandFactory<CommandSourceStack> {
    private const val RELOAD_LITERAL = "reload"

    override fun createCommands(commandManager: CommandManager<CommandSourceStack>): List<Command<out CommandSourceStack>> {
        return buildList {
            // <root> reload
            commandManager.commandBuilder(
                name = CommandConstants.ROOT_COMMAND,
                description = Description.of("Reload the plugin"),
            ) {
                permission(CommandPermissions.YUUAI_RELOAD)
                literal(RELOAD_LITERAL)
                suspendingHandler { context ->
                    val sender = context.sender().sender
                    val reloadTime = measureTimeMillis {
                        Injector.get<YuuaiPlugin>().reload()
                    }

                    sender.sendMessage("Reloaded in $reloadTime ms")
                }
            }.buildAndAdd(this)
        }
    }
}