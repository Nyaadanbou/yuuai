package cc.mewcraft.yuuai.command

import cc.mewcraft.yuuai.Injector
import cc.mewcraft.yuuai.YuuaiPlugin
import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.scope
import kotlinx.coroutines.CoroutineScope
import org.incendo.cloud.Command
import org.incendo.cloud.kotlin.MutableCommandBuilder
import org.incendo.cloud.kotlin.coroutines.SuspendingExecutionHandler
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler
import org.koin.core.component.get
import kotlin.coroutines.CoroutineContext

/**
 * Specify a suspending command execution handler.
 */
fun <C : Any> MutableCommandBuilder<C>.suspendingHandler(
    scope: CoroutineScope = Injector.get<YuuaiPlugin>().scope, // use our own scope
    context: CoroutineContext = Injector.get<YuuaiPlugin>().asyncDispatcher, // use our own dispatcher
    handler: SuspendingExecutionHandler<C>,
): MutableCommandBuilder<C> = mutate {
    it.suspendingHandler(scope, context, handler)
}

/**
 * Builds this command and adds it to the given [commands].
 *
 * @param C the sender type
 * @param commands the command list
 */
fun <C : Any> MutableCommandBuilder<C>.buildAndAdd(commands: MutableList<Command<out C>>) {
    commands += this.build()
}