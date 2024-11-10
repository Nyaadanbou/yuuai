package cc.mewcraft.yuuai.bossbar.impl

import cc.mewcraft.orientation.OrientationProvider
import cc.mewcraft.yuuai.bossbar.BossBarFactory
import cc.mewcraft.yuuai.bossbar.BossBarFactoryProvider
import cc.mewcraft.yuuai.CheckResult
import cc.mewcraft.yuuai.YuuaiPlugin
import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.scope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.spongepowered.configurate.ConfigurationNode

interface OrientationBossBarFactory : BossBarFactory {
    companion object : BossBarFactoryProvider<OrientationBossBarFactory>, KoinComponent {
        const val KEY = "orientation"

        private val plugin: YuuaiPlugin by inject()

        override fun check(node: ConfigurationNode): CheckResult {
            plugin.server.pluginManager.getPlugin("Orientation")
                ?: return CheckResult.MissingDependency("Orientation")
            return CheckResult.Success
        }

        override fun getBossBarFactory(node: ConfigurationNode): OrientationBossBarFactory {
            return OrientationBossBarFactoryImpl(
                isEnable = node.node("enabled").getBoolean(true),
                color = BossBar.Color.valueOf(node.node("color").getString("WHITE").uppercase()),
                overlay = BossBar.Overlay.valueOf(node.node("overlay").getString("PROGRESS").uppercase()),
                text = node.node("text").getString(""),
            )
        }
    }
}

private class OrientationBossBarFactoryImpl(
    override val isEnable: Boolean,
    private val color: BossBar.Color,
    private val overlay: BossBar.Overlay,
    private val text: String,
) : OrientationBossBarFactory, KoinComponent {
    private val plugin: YuuaiPlugin by inject()
    private val miniMessage: MiniMessage by inject()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val textFormat: (Player) -> TagResolver = { player ->
        val orientation = OrientationProvider.get()
        val novice = orientation.getNovice(player.uniqueId)
        Placeholder.component("value") {
            val timeLeft = plugin.scope.async(plugin.asyncDispatcher) {
                novice.timeLeftMillSeconds()
            }
            // TODO: format
            if (timeLeft.isCompleted) {
                Component.text(timeLeft.getCompleted())
            } else {
                Component.text("...")
            }
        }
    }

    override fun createBossBar(player: Player): BossBar {
        return BossBar.bossBar(
            miniMessage.deserialize(text, textFormat(player)),
            1.0f,
            color,
            overlay,
        )
    }

    override fun refreshBossBar(player: Player, old: BossBar) {
        old.name(miniMessage.deserialize(text, textFormat(player)))
    }
}