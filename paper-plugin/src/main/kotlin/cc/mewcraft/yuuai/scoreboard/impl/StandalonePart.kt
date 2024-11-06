package cc.mewcraft.yuuai.scoreboard.impl

import cc.mewcraft.yuuai.scoreboard.ScoreboardPart
import cc.mewcraft.yuuai.scoreboard.ScoreboardPartCheckResult
import cc.mewcraft.yuuai.scoreboard.ScoreboardPartFactory
import cc.mewcraft.yuuai.scoreboard.SidebarComponentResult
import cc.mewcraft.yuuai.scoreboard.impl.StandalonePart.Companion.NAMESPACE
import cc.mewcraft.yuuai.scoreboard.impl.StandalonePart.Companion.VALUES
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.spongepowered.configurate.ConfigurationNode

interface StandalonePart : ScoreboardPart {
    companion object : ScoreboardPartFactory<StandalonePart> {
        val NAMESPACE = "standalone"
        val VALUES = arrayOf("server_name", "world_name")

        override fun check(node: ConfigurationNode): ScoreboardPartCheckResult {
            return ScoreboardPartCheckResult.Success
        }

        override fun create(node: ConfigurationNode): StandalonePart {
            val serverName = node.node("server_name").string
                ?: throw IllegalArgumentException("Missing 'server_name' key in standalone scoreboard part")
            val worldName = node.node("world_name").string
                ?: throw IllegalArgumentException("Missing 'world_name' key in standalone scoreboard part")

            return StandalonePartImpl(serverName, worldName)
        }
    }
}

private class StandalonePartImpl(
    private val serverNameFormat: String,
    private val worldNameFormat: String,
) : StandalonePart, KoinComponent {
    private val miniMessage: MiniMessage by inject()

    private val serverNamePlaceHolder: (Player) -> TagResolver = { Placeholder.parsed("value", it.server.name) }
    private val worldNamePlaceholder: (Player) -> TagResolver = { Placeholder.parsed("value", it.world.name) }

    override fun sidebarComponent(key: Key, player: Player): SidebarComponentResult {
        if (key.namespace() != NAMESPACE)
            return SidebarComponentResult.InvalidNamespace(key.namespace(), NAMESPACE)
        if (key.value() !in VALUES)
            return SidebarComponentResult.InvalidValues(key.value(), *VALUES)
        return when (key.value()) {
            "server_name" -> {
                SidebarComponentResult.Success(
                    SidebarComponent.staticLine(miniMessage.deserialize(serverNameFormat, serverNamePlaceHolder(player)))
                )
            }

            "world_name" -> {
                SidebarComponentResult.Success(
                    SidebarComponent.dynamicLine { miniMessage.deserialize(worldNameFormat, worldNamePlaceholder(player)) }
                )
            }

            else -> throw IllegalArgumentException("Invalid key value: ${key.value()}")

        }
    }
}