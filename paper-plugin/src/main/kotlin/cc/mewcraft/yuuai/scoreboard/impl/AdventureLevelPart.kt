package cc.mewcraft.yuuai.scoreboard.impl

import cc.mewcraft.adventurelevel.level.category.LevelCategory
import cc.mewcraft.adventurelevel.plugin.AdventureLevelProvider
import cc.mewcraft.yuuai.YuuaiPlugin
import cc.mewcraft.yuuai.scoreboard.ScoreboardPart
import cc.mewcraft.yuuai.CheckResult
import cc.mewcraft.yuuai.scoreboard.ScoreboardPartFactory
import cc.mewcraft.yuuai.scoreboard.SidebarComponentResult
import cc.mewcraft.yuuai.scoreboard.impl.AdventureLevelPart.Companion.NAMESPACE
import cc.mewcraft.yuuai.scoreboard.impl.AdventureLevelPart.Companion.VALUES
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.spongepowered.configurate.ConfigurationNode

interface AdventureLevelPart : ScoreboardPart {
    companion object : ScoreboardPartFactory<AdventureLevelPart>, KoinComponent {
        const val NAMESPACE = "adventure_level"
        val VALUES = arrayOf("level")

        private val plugin: YuuaiPlugin by inject()

        override fun check(node: ConfigurationNode): CheckResult {
            plugin.server.pluginManager.getPlugin("AdventureLevel")
                ?: return CheckResult.MissingDependency("AdventureLevel")
            return CheckResult.Success
        }

        override fun create(node: ConfigurationNode): AdventureLevelPart {
            val levelFormat = node.node("level").string
                ?: throw IllegalArgumentException("Missing 'level' key in AdventureLevelPart configuration")
            return AdventureLevelPartImpl(levelFormat)
        }
    }
}

private class AdventureLevelPartImpl(
    private val levelFormat: String,
) : AdventureLevelPart, KoinComponent {
    private val miniMessage: MiniMessage by inject()

    private val levelFormatPlaceHolder: (Player) -> TagResolver = {
        val adventureLevel = AdventureLevelProvider.get()
        val dataManager = adventureLevel.playerDataManager()
        val data = dataManager.load(it)
        val level = data.getLevel(LevelCategory.PRIMARY)
        Placeholder.parsed("value", level.level.toString())
    }

    override fun sidebarComponent(key: Key, player: Player): SidebarComponentResult {
        if (key.namespace() != NAMESPACE)
            return SidebarComponentResult.InvalidNamespace(key.namespace(), NAMESPACE)
        if (key.value() !in VALUES)
            return SidebarComponentResult.InvalidValues(key.value(), *VALUES)
        return when (key.value()) {
            "level" -> SidebarComponentResult.Success(
                SidebarComponent.dynamicLine { miniMessage.deserialize(levelFormat, levelFormatPlaceHolder(player)) }
            )
            
            else -> SidebarComponentResult.InvalidValues(key.value(), *VALUES)
        }
    }
}