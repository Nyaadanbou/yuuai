package cc.mewcraft.yuuai.component.impl

import cc.mewcraft.adventurelevel.event.AdventureLevelDataLoadEvent
import cc.mewcraft.adventurelevel.level.category.LevelCategory
import cc.mewcraft.adventurelevel.plugin.AdventureLevelProvider
import cc.mewcraft.yuuai.CheckResult
import cc.mewcraft.yuuai.TextResult
import cc.mewcraft.yuuai.YuuaiPlugin
import cc.mewcraft.yuuai.component.ScoreboardComponent
import cc.mewcraft.yuuai.component.ScoreboardComponentFactory
import cc.mewcraft.yuuai.component.impl.AdventureLevelComponent.Companion.NAMESPACE
import cc.mewcraft.yuuai.component.impl.AdventureLevelComponent.Companion.VALUES
import cc.mewcraft.yuuai.scoreboard.ScoreboardManager
import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.scope
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import kotlinx.coroutines.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.spongepowered.configurate.ConfigurationNode
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

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
    private val levelTextCached: LoadingCache<Player, AdventureLevelTextData> = CacheBuilder.newBuilder()
        .weakKeys()
        .build(
            CacheLoader.from { player -> AdventureLevelTextData(player.uniqueId, levelFormat, this) }
        )

    private val playersCanCheckLevel: MutableSet<UUID> = ConcurrentHashMap.newKeySet()
    private val playerAdder: Listener = PlayerAdder()

    override val namespace: String = NAMESPACE

    override fun text(namespace: String, arguments: Array<String>, player: Player): TextResult {
        if (namespace != NAMESPACE)
            return TextResult.InvalidNamespace(namespace, NAMESPACE)
        val value = arguments[0]
        if (value !in VALUES)
            return TextResult.InvalidValues(value, *VALUES)
        return when (value) {
            "level" -> TextResult.Success(levelTextCached[player].component())

            else -> TextResult.InvalidValues(value, *VALUES)
        }
    }

    override fun load() {
        AdventureLevelSupport.plugin.registerSuspendListener(playerAdder)
    }

    override fun unload() {
        HandlerList.unregisterAll(playerAdder)
        for (levelData in levelTextCached.asMap()) {
            levelData.value.unload()
        }
        levelTextCached.invalidateAll()
    }

    private inner class PlayerAdder : Listener {
        @EventHandler
        private fun onLevelLoad(e: AdventureLevelDataLoadEvent) {
            playersCanCheckLevel.add(e.userData.uuid)
        }

        @EventHandler
        private fun onQuit(e: PlayerQuitEvent) {
            playersCanCheckLevel.remove(e.player.uniqueId)
        }
    }
}

private class AdventureLevelTextData(
    private val uniqueId: UUID,
    private val levelFormat: String,
    private val component: AdventureLevelComponent,
) {
    companion object : KoinComponent {
        private val miniMessage: MiniMessage by inject()
        private val server: Server by inject()
    }

    private val player: Player?
        get() = server.getPlayer(uniqueId)

    private val scope: CoroutineScope = AdventureLevelSupport.plugin.scope + CoroutineName("yuuai-adventure-level-refresh-scope") + AdventureLevelSupport.plugin.asyncDispatcher

    private var job: Job = scope.launch {
        while (isActive) {
            val player = player ?: return@launch
            AdventureLevelSupport.scoreboardManager.setLine(player, component)
            delay(1000)
        }
    }

    fun component(): Component {
        val adventureLevel = AdventureLevelProvider.get()
        val userRepository = adventureLevel.userDataRepository
        val data = userRepository.getCached(uniqueId)
        val level = data?.getLevel(LevelCategory.PRIMARY)?.level ?: 0
        val levelComponentParsed = miniMessage.deserialize(levelFormat, Formatter.number("value", level))
        return levelComponentParsed
    }

    fun unload() {
        job.cancel()
    }
}

private object AdventureLevelSupport : KoinComponent {
    val plugin: YuuaiPlugin by inject()
    val scoreboardManager: ScoreboardManager by inject()
}