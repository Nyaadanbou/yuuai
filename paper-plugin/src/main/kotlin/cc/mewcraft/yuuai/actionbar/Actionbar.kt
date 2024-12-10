package cc.mewcraft.yuuai.actionbar

import cc.mewcraft.yuuai.TextResult
import cc.mewcraft.yuuai.YuuaiPlugin
import cc.mewcraft.yuuai.component.ActionbarComponent
import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.scope
import kotlinx.coroutines.*
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger

class Actionbar(
    private val player: Player,
    private val originText: String,
    private val showComponents: List<ActionbarComponent>,
) {
    companion object : KoinComponent {
        private val plugin: YuuaiPlugin by inject()
        private val logger: Logger by inject()
        private val miniMessage: MiniMessage by inject()

        private val ERROR_COLOR: TextColor = NamedTextColor.RED
    }

    private val scope: CoroutineScope = plugin.scope + CoroutineName("player-${player.name}-actionbar") + plugin.asyncDispatcher

    private var showTask: Job? = null

    fun show() {
        if (showTask != null) {
            return
        }

        showTask = scope.launch {
            while (isActive) {
                val actionbar = miniMessage.deserialize(originText, formatTagResolver())
                player.sendActionBar(actionbar)
                delay(1000)
            }
        }
    }

    fun hide() {
        showTask?.cancel()
    }

    fun cancel() {
        scope.cancel()
    }

    /**
     * 解析并格式化 namespace 是 format 的标签, 例如
     * `format:standalone:player_health`.
     */
    private fun formatTagResolver(): TagResolver {
        return TagResolver.resolver("format") { queue, context ->
            val namespace = queue.popOr("Cannot find the namespace argument of format tag.").lowerValue()
            val arguments = arrayListOf<String>()
            while (queue.hasNext()) {
                arguments.add(queue.pop().lowerValue())
            }
            val showComponents = showComponents.find { it.namespace == namespace } ?: error("Cannot find the component with namespace $namespace.")
            val formattedText = showComponents.textComponent(namespace, arguments.toTypedArray())

            Tag.inserting(formattedText)
        }
    }

    private fun ActionbarComponent.textComponent(namespace: String, arguments: Array<String>): Component {
        return when (val textResult = text(namespace, arguments, player)) {
            is TextResult.Success -> textResult.value
            is TextResult.InvalidNamespace -> {
                logger.error("Invalid namespace when getting text for actionbar: $namespace, expected: ${textResult.correctNamespace}")
                Component.text("Error").color(ERROR_COLOR)
            }

            is TextResult.InvalidValues -> {
                logger.warn("Invalid values when getting text for actionbar: $$namespace, expected: ${textResult.correctValues.joinToString()}")
                Component.text("Error").color(ERROR_COLOR)
            }
        }
    }
}