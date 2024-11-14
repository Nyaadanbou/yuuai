package cc.mewcraft.yuuai.component.impl

import cc.mewcraft.adventurelevel.event.AdventureLevelDataLoadEvent
import cc.mewcraft.adventurelevel.level.category.LevelCategory
import cc.mewcraft.adventurelevel.plugin.AdventureLevelProvider
import cc.mewcraft.yuuai.CheckResult
import cc.mewcraft.yuuai.YuuaiPlugin
import cc.mewcraft.yuuai.component.ScoreboardComponent
import cc.mewcraft.yuuai.component.ScoreboardComponentFactory
import cc.mewcraft.yuuai.component.YuuaiRefresher
import cc.mewcraft.yuuai.component.impl.AdventureLevelComponent.Companion.NAMESPACE
import cc.mewcraft.yuuai.component.impl.AdventureLevelComponent.Companion.VALUES
import cc.mewcraft.yuuai.scoreboard.ScoreboardManager
import cc.mewcraft.yuuai.TextResult
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import it.unimi.dsi.fastutil.objects.Object2IntFunction
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.spongepowered.configurate.ConfigurationNode

interface AdventureLevelComponent : ScoreboardComponent {
    companion object : AbstractYuuaiComponentFactory<AdventureLevelComponent>(), ScoreboardComponentFactory<AdventureLevelComponent> {
        const val NAMESPACE = "adventure_level"
        val VALUES = arrayOf("level")

        override fun check(node: ConfigurationNode): CheckResult {
            val checkResult = checkDependencies("AdventureLevel")
            if (!checkResult.isSuccessful) {
                return checkResult
            }
            if (!isEnabled(node)) {
                return CheckResult.Disabled
            }
            return CheckResult.Success
        }

        override fun getComponent(node: ConfigurationNode): AdventureLevelComponent {
            val levelFormat = node.node("level").string
                ?: throw IllegalArgumentException("Missing 'level' key in AdventureLevelComponent configuration")
            return AdventureLevelComponentImpl(levelFormat)
        }
    }
}

private class AdventureLevelComponentImpl(
    private val levelFormat: String,
) : AdventureLevelComponent, KoinComponent {
    private val plugin: YuuaiPlugin by inject()
    private val scoreboardManager: ScoreboardManager by inject()

    private val levelTextCached: LoadingCache<Player, AdventureLevelTextData> = CacheBuilder.newBuilder()
        .weakKeys()
        .build(
            CacheLoader.from { player -> AdventureLevelTextData(player, levelFormat) }
        )

    override val refresher: YuuaiRefresher = Refresher()
    override val namespace: String = NAMESPACE

    override fun text(key: Key, player: Player): TextResult {
        if (key.namespace() != NAMESPACE)
            return TextResult.InvalidNamespace(key.namespace(), NAMESPACE)
        if (key.value() !in VALUES)
            return TextResult.InvalidValues(key.value(), *VALUES)
        return when (key.value()) {
            "level" -> TextResult.Success(levelTextCached[player].component())

            else -> TextResult.InvalidValues(key.value(), *VALUES)
        }
    }

    private inner class Refresher : YuuaiRefresher {
        @EventHandler
        fun onLevelLoad(event: AdventureLevelDataLoadEvent) {
            val playerData = event.playerData
            val player = playerData.player ?: return
            levelTextCached.invalidate(player)
            scoreboardManager.setLine(player, this@AdventureLevelComponentImpl)
        }
    }

    override fun unload() {
        HandlerList.unregisterAll(refresher)
    }
}

private class AdventureLevelTextData(
    private val player: Player,
    private val levelFormat: String,
) {
    companion object : KoinComponent {
        private val miniMessage: MiniMessage by inject()
    }

    private val levelCached: Object2IntOpenHashMap<LevelCategory> = Object2IntOpenHashMap<LevelCategory>()

    fun component(): Component {
        val adventureLevel = AdventureLevelProvider.get()
        val dataManager = adventureLevel.playerDataManager()
        val data = dataManager.load(player)
        val level = levelCached.computeIfAbsent(LevelCategory.PRIMARY, Object2IntFunction { data.getLevel(LevelCategory.PRIMARY).level })
        val levelComponentParsed = miniMessage.deserialize(levelFormat, Placeholder.parsed("value", level.toString()))
        return levelComponentParsed
    }

}