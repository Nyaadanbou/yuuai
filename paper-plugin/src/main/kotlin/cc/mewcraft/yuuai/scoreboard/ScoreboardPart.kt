package cc.mewcraft.yuuai.scoreboard

import net.kyori.adventure.key.Key
import org.bukkit.entity.Player

interface ScoreboardPart {
    fun sidebarComponent(key: Key, player: Player): SidebarComponentResult
}