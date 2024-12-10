@file:Suppress("UnstableApiUsage")

package cc.mewcraft.yuuai.command

import cc.mewcraft.yuuai.command.command.YuuaiCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.incendo.cloud.paper.PaperCommandManager

class CommandManager(
    private val manager: PaperCommandManager<CommandSourceStack>
) {
    fun init() {
        with(manager) {
            command(YuuaiCommand)
        }
    }
}