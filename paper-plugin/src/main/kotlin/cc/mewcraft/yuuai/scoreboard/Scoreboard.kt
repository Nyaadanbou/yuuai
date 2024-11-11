package cc.mewcraft.yuuai.scoreboard

import cc.mewcraft.yuuai.component.ScoreboardComponent
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger

class Scoreboard(
    private val player: Player,
    /**
     * Key: 行的 key
     * Value: 用于解析上方 key 的对象
     */
    lines: Map<Key, ScoreboardComponentData>,
) {
    private val lines: MutableMap<Key, ScoreboardComponentData> = lines.toMutableMap()
    private val sidebar: Sidebar = ScoreboardSupport.scoreboardLibrary.createSidebar(Sidebar.MAX_LINES)

    fun title(title: Component) {
        sidebar.title(title)
    }

    fun show() {
        for ((lineKey, data) in lines) {
            val text = data.component.textComponent(lineKey)
            sidebar.line(data.index, text)
        }
        sidebar.addPlayer(player)
    }

    fun hide() {
        sidebar.removePlayer(player)
        sidebar.clearLines()
    }

    fun line(new: ScoreboardComponent) {
        val namespace = new.namespace
        val linesIterator = lines.iterator()

        while (linesIterator.hasNext()) {
            val (lineKey, data) = linesIterator.next()
            // 如果行的命名空间与要改变的侧边栏组件的命名空间相同，那么更新这一行
            if (lineKey.namespace() == namespace) {
                val text = new.textComponent(lineKey)
                sidebar.line(data.index, text)
            }
        }
    }

    private fun ScoreboardComponent.textComponent(lineKey: Key): Component {
        return when (val textResult = text(lineKey, player)) {
            is ScoreboardTextResult.Success -> textResult.value
            is ScoreboardTextResult.InvalidNamespace -> {
                ScoreboardSupport.logger.error("Invalid namespace when getting text for scoreboard: ${lineKey.namespace()}, expected: ${textResult.correctNamespace}")
                Component.text("Error").color(ScoreboardSupport.ERROR_COLOR)
            }

            is ScoreboardTextResult.InvalidValues -> {
                ScoreboardSupport.logger.warn("Invalid values when getting text for scoreboard: ${lineKey.namespace()}, expected: ${textResult.correctValues.joinToString()}")
                Component.text("Error").color(ScoreboardSupport.ERROR_COLOR)
            }
        }
    }
}

private object ScoreboardSupport : KoinComponent {
    val logger: Logger by inject()
    val scoreboardLibrary: ScoreboardLibrary by inject()

    val ERROR_COLOR: TextColor = NamedTextColor.RED
}