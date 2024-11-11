package cc.mewcraft.yuuai.component.impl

import cc.mewcraft.yuuai.component.ScoreboardComponent
import cc.mewcraft.yuuai.CheckResult
import cc.mewcraft.yuuai.component.ScoreboardComponentFactory
import cc.mewcraft.yuuai.component.YuuaiRefresher
import cc.mewcraft.yuuai.scoreboard.ScoreboardTextResult
import cc.mewcraft.yuuai.component.impl.StandaloneComponent.Companion.NAMESPACE
import cc.mewcraft.yuuai.component.impl.StandaloneComponent.Companion.VALUES
import cc.mewcraft.yuuai.scoreboard.ScoreboardManager
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.spongepowered.configurate.ConfigurationNode

interface StandaloneComponent : ScoreboardComponent {
    companion object : AbstractYuuaiComponentProvider<StandaloneComponent>(), ScoreboardComponentFactory<StandaloneComponent> {
        const val NAMESPACE = "standalone"
        val VALUES = arrayOf("server_name", "world_name")

        override fun check(node: ConfigurationNode): CheckResult {
            return CheckResult.Success
        }

        override fun create(node: ConfigurationNode): StandaloneComponent {
            val serverName = node.node("server_name").string
                ?: throw IllegalArgumentException("Missing 'server_name' key in standalone scoreboard part")
            val worldName = node.node("world_name").string
                ?: throw IllegalArgumentException("Missing 'world_name' key in standalone scoreboard part")

            return StandaloneComponentImpl(serverName, worldName)
        }
    }
}

private class StandaloneComponentImpl(
    private val serverNameFormat: String,
    private val worldNameFormat: String,
) : StandaloneComponent, KoinComponent {
    private val scoreboardManager: ScoreboardManager by inject()
    private val miniMessage: MiniMessage by inject()

    private val serverNamePlaceHolder: (Player) -> TagResolver = { Placeholder.parsed("value", it.server.name) }
    private val worldNamePlaceholder: (Player) -> TagResolver = { Placeholder.parsed("value", it.world.name) }

    override val namespace: String = NAMESPACE
    override val refresher: YuuaiRefresher = Refresher()

    private inner class Refresher : YuuaiRefresher {
        @EventHandler
        private fun on(event: PlayerChangedWorldEvent) {
            refresh(event.player)
        }

        fun refresh(player: Player) {
            scoreboardManager.setLine(player, this@StandaloneComponentImpl)
        }
    }

    override fun text(key: Key, player: Player): ScoreboardTextResult {
        if (key.namespace() != NAMESPACE)
            return ScoreboardTextResult.InvalidNamespace(key.namespace(), NAMESPACE)
        if (key.value() !in VALUES)
            return ScoreboardTextResult.InvalidValues(key.value(), *VALUES)
        return when (key.value()) {
            "server_name" -> {
                ScoreboardTextResult.Success(
                    miniMessage.deserialize(serverNameFormat, serverNamePlaceHolder(player))
                )
            }

            "world_name" -> {
                ScoreboardTextResult.Success(
                    miniMessage.deserialize(worldNameFormat, worldNamePlaceholder(player))
                )
            }

            else -> throw IllegalArgumentException("Invalid key value: ${key.value()}")

        }
    }
}