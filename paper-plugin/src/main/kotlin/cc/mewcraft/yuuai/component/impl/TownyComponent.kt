package cc.mewcraft.yuuai.component.impl

import cc.mewcraft.yuuai.CheckResult
import cc.mewcraft.yuuai.TextResult
import cc.mewcraft.yuuai.YuuaiPlugin
import cc.mewcraft.yuuai.component.ScoreboardComponent
import cc.mewcraft.yuuai.component.ScoreboardComponentFactory
import cc.mewcraft.yuuai.component.ServerTickRefresher
import cc.mewcraft.yuuai.component.impl.TownyComponent.Companion.NAMESPACE
import cc.mewcraft.yuuai.component.impl.TownyComponent.Companion.VALUES
import cc.mewcraft.yuuai.scoreboard.ScoreboardManager
import com.palmergames.bukkit.towny.TownyAPI
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.spongepowered.configurate.ConfigurationNode

interface TownyComponent : ScoreboardComponent {
    companion object : AbstractYuuaiComponentFactory<TownyComponent>(), ScoreboardComponentFactory<TownyComponent> {
        const val NAMESPACE = "towny"
        val VALUES = arrayOf("town_name", "nation_name")

        override fun check(node: ConfigurationNode): CheckResult {
            val checkResult = checkDependencies("Towny")
            if (!checkResult.isSuccessful) {
                return checkResult
            }
            if (!isEnabled(node)) {
                return CheckResult.Disabled
            }
            return CheckResult.Success
        }

        override fun getComponent(node: ConfigurationNode): TownyComponent {
            val townName = node.node("town_name").string ?: "<value>"
            val nationName = node.node("nation_name").string ?: "<value>"

            return TownyComponentImpl(townName, nationName)
        }
    }
}

private class TownyComponentImpl(
    private val townName: String,
    private val nationName: String,
) : TownyComponent, KoinComponent {
    private val plugin: YuuaiPlugin by inject()
    private val scoreboardManager: ScoreboardManager by inject()
    private val miniMessage: MiniMessage by inject()

    override val namespace: String = NAMESPACE
    private val refresher: Listener = ServerTickRefresher(20) { player ->
        scoreboardManager.setLine(player, this@TownyComponentImpl)
    }

    override fun text(key: Key, player: Player): TextResult {
        if (key.namespace() != NAMESPACE)
            return TextResult.InvalidNamespace(key.namespace(), NAMESPACE)
        if (key.value() !in VALUES)
            return TextResult.InvalidValues(key.value(), *VALUES)

        val townyAPI = TownyAPI.getInstance()
        val resident = townyAPI.getResident(player)

        return when (key.value()) {
            "town_name" -> {
                val town = resident?.townOrNull
                val placeholder = if (town != null) {
                    Placeholder.parsed("value", town.name)
                } else {
                    Placeholder.parsed("value", "无")
                }
                TextResult.Success(
                    miniMessage.deserialize(townName, placeholder)
                )
            }

            "nation_name" -> {
                val nation = resident?.nationOrNull
                val placeholder = if (nation != null) {
                    Placeholder.parsed("value", nation.name)
                } else {
                    Placeholder.parsed("value", "无")
                }
                TextResult.Success(
                    miniMessage.deserialize(nationName, placeholder)
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