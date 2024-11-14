package cc.mewcraft.yuuai.component.impl

import cc.mewcraft.economy.api.Currency
import cc.mewcraft.economy.api.EconomyProvider
import cc.mewcraft.yuuai.CheckResult
import cc.mewcraft.yuuai.component.ScoreboardComponent
import cc.mewcraft.yuuai.component.ScoreboardComponentFactory
import cc.mewcraft.yuuai.component.ServerTickRefresher
import cc.mewcraft.yuuai.component.YuuaiRefresher
import cc.mewcraft.yuuai.component.impl.EconomyComponent.Companion.NAMESPACE
import cc.mewcraft.yuuai.component.impl.EconomyComponent.Companion.findCurrency
import cc.mewcraft.yuuai.scoreboard.ScoreboardManager
import cc.mewcraft.yuuai.TextResult
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.spongepowered.configurate.ConfigurationNode

interface EconomyComponent : ScoreboardComponent {
    companion object : AbstractYuuaiComponentFactory<EconomyComponent>(), ScoreboardComponentFactory<EconomyComponent> {
        const val NAMESPACE = "economy"

        override fun check(node: ConfigurationNode): CheckResult {
            val checkResult = checkDependencies("Economy")
            if (!checkResult.isSuccessful) {
                return checkResult
            }
            if (!isEnabled(node)) {
                return CheckResult.Disabled
            }
            return CheckResult.Success
        }

        override fun getComponent(node: ConfigurationNode): EconomyComponent {
            val formats = node.childrenMap()
                .mapNotNull { (key, value) ->
                    val currencyString = key.toString()
                    findCurrency(currencyString)?.let { currencyString to value.rawScalar() as String }
                }
                .toMap()
            return EconomyComponentImpl(formats)
        }

        fun findCurrency(currency: String): Currency? {
            val economy = EconomyProvider.get()
            val currencies = economy.loadedCurrencies
            return currencies.find { it.name.lowercase() == currency }
        }
    }
}

private class EconomyComponentImpl(
    private val formats: Map<String, String>,
) : EconomyComponent, KoinComponent {
    private val scoreboardManager: ScoreboardManager by inject()
    private val miniMessage: MiniMessage by inject()

    override val namespace: String = NAMESPACE
    override val refresher: YuuaiRefresher = ServerTickRefresher(20) { player ->
        scoreboardManager.setLine(player, this)
    }

    override fun text(key: Key, player: Player): TextResult {
        if (key.namespace() != NAMESPACE)
            return TextResult.InvalidNamespace(key.namespace(), NAMESPACE)
        val economy = EconomyProvider.get()

        val currencyString = key.value()
        val currencyFormat = formats[currencyString]
        if (currencyFormat != null) {
            val currencies = economy.loadedCurrencies
            val currency = findCurrency(currencyString) ?: return TextResult.InvalidValues(currencyString, *currencies.mapNotNull { it.name }.toTypedArray())
            val balance = economy.getBalance(player.uniqueId, currency)
            val placeholder = Placeholder.parsed("value", balance.toString())
            return TextResult.Success(
                miniMessage.deserialize(currencyFormat, placeholder)
            )
        }

        return TextResult.InvalidValues(currencyString, *formats.keys.toTypedArray())
    }

    override fun unload() {
        HandlerList.unregisterAll(refresher)
    }
}