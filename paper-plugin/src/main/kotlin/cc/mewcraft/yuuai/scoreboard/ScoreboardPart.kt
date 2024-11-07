package cc.mewcraft.yuuai.scoreboard

import net.kyori.adventure.key.Key
import org.bukkit.entity.Player

/**
 * 包含一个所有侧边栏组件可能的样子的接口.
 */
interface ScoreboardPart {
    /**
     * 根据输入的信息获取侧边栏组件.
     *
     * @see SidebarComponentResult
     *
     * @param key 组件的键, 如 `standalone:world_name`.
     * @param player 玩家.
     */
    fun sidebarComponent(key: Key, player: Player): SidebarComponentResult
}