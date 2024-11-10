package cc.mewcraft.yuuai.bossbar

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.entity.Player
import java.util.*

private data class BossBarData(
    val factory: BossBarFactory,
    val bossBar: BossBar
) {
    fun update(player: Player) {
        factory.refreshBossBar(player, bossBar)
    }
}

class BossBarManager(
    private val config: BossBarConfig
) {
    private val bossBars: Multimap<UUID, BossBarData> = HashMultimap.create()

    fun showBossBar(player: Player) {
        val uniqueId = player.uniqueId

        val oldBossBar = bossBars[uniqueId]
        if (oldBossBar.isNotEmpty()) {
            oldBossBar.forEach { it.update(player) }
            return
        }

        val factories = config.bossBarFactories
        val bossBars = factories.map { factory ->
            val bossBar = factory.createBossBar(player)
            player.showBossBar(bossBar)
            BossBarData(factory, bossBar)
        }

        this.bossBars.putAll(uniqueId, bossBars)
    }

    fun removeBossBar(player: Player) {
        val uniqueId = player.uniqueId
        val bossBars = bossBars[uniqueId]
        if (bossBars.isEmpty()) return

        bossBars.forEach { it.bossBar.removeViewer(player) }
        this.bossBars.removeAll(uniqueId)
    }
}