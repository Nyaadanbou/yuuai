package cc.mewcraft.yuuai.component.impl

import cc.mewcraft.nettowaku.ServerInfo
import cc.mewcraft.yuuai.CheckResult
import cc.mewcraft.yuuai.component.*
import cc.mewcraft.yuuai.TextResult
import cc.mewcraft.yuuai.YuuaiPlugin
import cc.mewcraft.yuuai.component.impl.StandaloneComponent.Companion.NAMESPACE
import cc.mewcraft.yuuai.component.impl.StandaloneComponent.Companion.VALUES
import cc.mewcraft.yuuai.component.impl.StandaloneComponent.Companion.WORLD_NAME
import cc.mewcraft.yuuai.scoreboard.ScoreboardManager
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.attribute.Attribute
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
        const val PLAYER_MAX_HEALTH = "player_max_health"

        val VALUES = arrayOf(SERVER_NAME, WORLD_NAME, PLAYER_HEALTH, PLAYER_MAX_HEALTH)

        override fun check(node: ConfigurationNode): CheckResult {
            return CheckResult.Success
        }

        override fun getComponent(node: ConfigurationNode): StandaloneComponent {
            val serverName = node.node("server_name").string ?: "<value>"
            val worldName = node.node("world_name").string ?: "<value>"
            val playerHealth = node.node("player_health").string ?: "<value>"
            val playerMaxHealth = node.node("player_max_health").string ?: "<value>"

            return StandaloneComponentImpl(serverName, worldName, playerHealth, playerMaxHealth)
        }
    }
}

private class StandaloneComponentImpl(
    private val serverNameFormat: String,
    private val worldNameFormat: String,
    private val playerHealthFormat: String,
    private val playerMaxHealthFormat: String,
) : StandaloneComponent, KoinComponent {
    private val plugin: YuuaiPlugin by inject()
    private val scoreboardManager: ScoreboardManager by inject()
    private val miniMessage: MiniMessage by inject()

    private val serverNamePlaceHolder: () -> TagResolver = { Placeholder.parsed("value", ServerInfo.SERVER_NAME.get()) }
    private val worldNamePlaceholder: (Player) -> TagResolver = { Placeholder.parsed("value", it.world.name) }
    private val playerHealthPlaceholder: (Player) -> TagResolver = { Formatter.number("value", it.health) }
    private val playerMaxHealthPlaceholder: (Player) -> TagResolver = {
        val maxHealth = it.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0
        Formatter.number("value", maxHealth)
    }
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

    override fun text(key: Key, player: Player): TextResult {
        if (key.namespace() != NAMESPACE)
            return TextResult.InvalidNamespace(key.namespace(), NAMESPACE)
        if (key.value() !in VALUES)
            return TextResult.InvalidValues(key.value(), *VALUES)
        return when (key.value()) {
            "server_name" -> {
                TextResult.Success(
                    miniMessage.deserialize(serverNameFormat, serverNamePlaceHolder())
                )
            }

            "world_name" -> {
                TextResult.Success(
                    miniMessage.deserialize(worldNameFormat, worldNamePlaceholder(player))
                )
            }

            "player_health" -> {
                TextResult.Success(
                    miniMessage.deserialize(playerHealthFormat, playerHealthPlaceholder(player))
                )
            }

            "player_max_health" -> {
                TextResult.Success(
                    miniMessage.deserialize(playerMaxHealthFormat, playerMaxHealthPlaceholder(player))
                )
            }

            else -> throw IllegalArgumentException("Invalid key value: ${key.value()}")

        }
    }

    override fun load() {
        plugin.registerSuspendListener(refresher)
    }

    override fun unload() {
        HandlerList.unregisterAll(refresher)
    }
}