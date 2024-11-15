package cc.mewcraft.yuuai.component.impl

import cc.mewcraft.orientation.OrientationProvider
import cc.mewcraft.orientation.novice.NoviceRefreshListener
import cc.mewcraft.yuuai.CheckResult
import cc.mewcraft.yuuai.YuuaiPlugin
import cc.mewcraft.yuuai.component.BossBarComponent
import cc.mewcraft.yuuai.component.BossBarComponentFactory
import cc.mewcraft.yuuai.util.DurationFormatter
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.spongepowered.configurate.ConfigurationNode
import java.time.Duration
import java.time.temporal.ChronoUnit

interface OrientationComponent : BossBarComponent {
    companion object : AbstractYuuaiComponentFactory<OrientationComponent>(), BossBarComponentFactory<OrientationComponent> {
        const val NAMESPACE = "orientation"
        val LISTENER_KEY = Key.key("yuuai", "orientation")

        override fun check(node: ConfigurationNode): CheckResult {
            val checkResult = checkDependencies("Orientation")
            if (!checkResult.isSuccessful) {
                return checkResult
            }
            if (!isEnabled(node)) {
                return CheckResult.Disabled
            }
            return CheckResult.Success
        }

        override fun getComponent(node: ConfigurationNode): OrientationComponent {
            return OrientationComponentImpl(
                color = BossBar.Color.valueOf(node.node("color").getString("WHITE").uppercase()),
                overlay = BossBar.Overlay.valueOf(node.node("overlay").getString("PROGRESS").uppercase()),
                text = node.node("text").getString(""),
            )
        }
    }
}

private class OrientationComponentImpl(
    private val color: BossBar.Color,
    private val overlay: BossBar.Overlay,
    private val text: String,
) : OrientationComponent, KoinComponent {
    private val plugin: YuuaiPlugin by inject()
    private val miniMessage: MiniMessage by inject()

    override val namespace: String = OrientationComponent.NAMESPACE

    private val timeFormatter: DurationFormatter = DurationFormatter(true, ChronoUnit.MINUTES)

    private val bossBars: LoadingCache<Player, BossBar> = CacheBuilder.newBuilder()
        .weakKeys()
        .removalListener<Player, BossBar> { notification ->
            val player = notification.key
            val bossBar = notification.value
            bossBar?.let { player?.hideBossBar(it) }
        }
        .build(CacheLoader.from { _ ->
            BossBar.bossBar(
                miniMessage.deserialize(text, Placeholder.parsed("value", "...")),
                1.0f,
                color,
                overlay,
            )
        })

    override fun showBossBar(player: Player) {
        if (bossBars.asMap().containsKey(player)) {
            // Already shown
            return
        }

        val orientation = OrientationProvider.get()
        val novice = orientation.getNovice(player.uniqueId)
        val refreshListener = NoviceRefreshListener<Long>(
            onRefresh = { timeLeft ->
                val bossBar = bossBars[player]
                if (timeLeft <= 0) {
                    player.hideBossBar(bossBar)
                    return@NoviceRefreshListener
                } else if (!player.activeBossBars().contains(bossBar)) {
                    player.showBossBar(bossBar)
                }
                val title = miniMessage.deserialize(text, Placeholder.parsed("value", timeFormatter.format(Duration.ofMillis(timeLeft))))
                bossBar.name(title)
                bossBar.progress(timeLeft.toFloat() / novice.maxTimeMillSeconds)
            },
            onExpire = {
                val bossBar = bossBars[player]
                bossBar.progress(0.0f)
                player.hideBossBar(bossBar)
                bossBars.invalidate(player)
            },
        )
        novice.addRefreshListener(OrientationComponent.LISTENER_KEY, refreshListener)
        novice.refresh()
    }

    override fun hideBossBar(player: Player) {
        val orientation = OrientationProvider.get()
        val novice = orientation.getNovice(player.uniqueId)
        novice.removeRefreshListener(OrientationComponent.LISTENER_KEY)
        bossBars.invalidate(player)
    }

    override fun load() = Unit

    override fun unload() {
        bossBars.asMap().forEach { hideBossBar(it.key) }
    }
}
