package cc.mewcraft.yuuai.component.impl

import cc.mewcraft.nettowaku.ServerInfo
import cc.mewcraft.yuuai.CheckResult
import cc.mewcraft.yuuai.TextResult
import cc.mewcraft.yuuai.YuuaiPlugin
import cc.mewcraft.yuuai.component.ActionbarComponent
import cc.mewcraft.yuuai.component.ActionbarComponentFactory
import cc.mewcraft.yuuai.component.ScoreboardComponent
import cc.mewcraft.yuuai.component.ScoreboardComponentFactory
import cc.mewcraft.yuuai.component.impl.StandaloneComponent.Companion.NAMESPACE
import cc.mewcraft.yuuai.component.impl.StandaloneComponent.Companion.PLAYER_HEALTH
import cc.mewcraft.yuuai.component.impl.StandaloneComponent.Companion.SERVER_NAME
import cc.mewcraft.yuuai.component.impl.StandaloneComponent.Companion.VALUES
import cc.mewcraft.yuuai.component.impl.StandaloneComponent.Companion.WORLD_NAME
import cc.mewcraft.yuuai.scoreboard.ScoreboardManager
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.spongepowered.configurate.ConfigurationNode

interface StandaloneComponent : ScoreboardComponent, ActionbarComponent {
    companion object : AbstractYuuaiComponentFactory<StandaloneComponent>(), ScoreboardComponentFactory<StandaloneComponent>, ActionbarComponentFactory<StandaloneComponent> {
        const val NAMESPACE = "standalone"
        const val SERVER_NAME = "server_name"
        const val WORLD_NAME = "world_name"
        const val PLAYER_HEALTH = "player_health"

        val VALUES = arrayOf(SERVER_NAME, WORLD_NAME, PLAYER_HEALTH)

        override fun check(node: ConfigurationNode): CheckResult {
            return CheckResult.Success
        }

        override fun getComponent(node: ConfigurationNode): StandaloneComponent {
            val serverName = node.node("server_name").string ?: "<value>"
            val worldName = node.node("world_name").string ?: "<value>"
            val playerHealthFormat = node.node("player_health").string ?: "<value>"

            return StandaloneComponentImpl(serverName, worldName, playerHealthFormat)
        }
    }
}

private class StandaloneComponentImpl(
    private val serverNameFormat: String,
    private val worldNameFormat: String,
    private val playerHealthFormat: String
) : StandaloneComponent, KoinComponent {
    private val plugin: YuuaiPlugin by inject()
    private val scoreboardManager: ScoreboardManager by inject()
    private val miniMessage: MiniMessage by inject()

    private val serverNamePlaceHolder: () -> TagResolver = { Placeholder.parsed("value", ServerInfo.SERVER_NAME.get()) }
    private val worldNamePlaceholder: (Player) -> TagResolver = { Placeholder.parsed("value", it.world.name) }
    private val playerHealthPlaceholder: (Player) -> TagResolver = { Formatter.number("value", it.health) }
    private val refresher: Listener = Refresher()

    override val namespace: String = NAMESPACE

    private inner class Refresher : Listener {
        @EventHandler
        private fun on(event: PlayerChangedWorldEvent) {
            refresh(event.player, WORLD_NAME)
        }

        fun refresh(player: Player, changedValue: String) {
            scoreboardManager.setLine(player, this@StandaloneComponentImpl, changedValue)
        }
    }

    override fun text(namespace: String, arguments: Array<String>, player: Player): TextResult {
        if (namespace != NAMESPACE)
            return TextResult.InvalidNamespace(namespace, NAMESPACE)
        val value = arguments[0]
        if (value !in VALUES)
            return TextResult.InvalidValues(value, *VALUES)
        return when (value) {
            SERVER_NAME -> {
                TextResult.Success(
                    miniMessage.deserialize(serverNameFormat, serverNamePlaceHolder())
                )
            }

            WORLD_NAME -> {
                TextResult.Success(
                    miniMessage.deserialize(worldNameFormat, worldNamePlaceholder(player))
                )
            }

            PLAYER_HEALTH -> {
                TextResult.Success(
                    miniMessage.deserialize(playerHealthFormat, playerHealthPlaceholder(player))
                )
            }

            else -> throw IllegalArgumentException("Invalid key value: $value")

        }
    }

    override fun load() {
        plugin.registerSuspendListener(refresher)
    }

    override fun unload() {
        HandlerList.unregisterAll(refresher)
    }
}