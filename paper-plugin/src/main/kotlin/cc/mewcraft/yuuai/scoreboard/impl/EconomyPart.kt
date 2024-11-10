package cc.mewcraft.yuuai.scoreboard.impl

import cc.mewcraft.economy.api.EconomyProvider
import cc.mewcraft.yuuai.YuuaiPlugin
import cc.mewcraft.yuuai.scoreboard.ScoreboardPart
import cc.mewcraft.yuuai.CheckResult
import cc.mewcraft.yuuai.scoreboard.ScoreboardPartFactory
import cc.mewcraft.yuuai.scoreboard.SidebarComponentResult
import cc.mewcraft.yuuai.scoreboard.impl.EconomyPart.Companion.NAMESPACE
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.spongepowered.configurate.ConfigurationNode

interface EconomyPart : ScoreboardPart {
    companion object : ScoreboardPartFactory<EconomyPart>, KoinComponent {
        const val NAMESPACE = "economy"

        private val plugin: YuuaiPlugin by inject()

        override fun check(node: ConfigurationNode): CheckResult {
            plugin.server.pluginManager.getPlugin("Economy")
                ?: return CheckResult.MissingDependency("Economy")
            return CheckResult.Success
        }

        override fun create(node: ConfigurationNode): EconomyPart {
            val formats = node.childrenMap()
                .map { (key, value) ->
                    key.toString() to value.rawScalar() as String
                }
                .toMap()
            return EconomyPartImpl(formats)
        }
    }
}

private class EconomyPartImpl(
    private val formats: Map<String, String>,
) : EconomyPart, KoinComponent {
    private val miniMessage: MiniMessage by inject()

    override fun sidebarComponent(key: Key, player: Player): SidebarComponentResult {
        if (key.namespace() != NAMESPACE)
            return SidebarComponentResult.InvalidNamespace(key.namespace(), NAMESPACE)
        val economy = EconomyProvider.get()

        val currencyString = key.value()
        val currencyFormat = formats[currencyString]
        if (currencyFormat != null) {
            val currencies = economy.loadedCurrencies
            val currency = currencies.find { it.symbolOrEmpty.lowercase() == currencyString } ?: return SidebarComponentResult.InvalidValues(currencyString, *currencies.map { it.displayName }.toTypedArray())
            val balance = economy.getBalance(player.uniqueId, currency)
            val placeholder = Placeholder.parsed("value", balance.toString())
            return SidebarComponentResult.Success(
                SidebarComponent.dynamicLine { miniMessage.deserialize(currencyFormat, placeholder) },
            )
        }

        return SidebarComponentResult.InvalidValues(currencyString, *formats.keys.toTypedArray())
    }
}