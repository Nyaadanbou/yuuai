package cc.mewcraft.yuuai.component

import cc.mewcraft.yuuai.scoreboard.ScoreboardTextResult
import net.kyori.adventure.key.Key
import org.bukkit.entity.Player

/**
 * 包含一个所有侧边栏组件可能的样子的接口.
 */
interface ScoreboardComponent : YuuaiComponent {
    /**
     * 根据输入的信息获取侧边栏组件.
     *
     * @see ScoreboardTextResult
     *
     * @param key 组件的键, 如 `standalone:world_name`.
     * @param player 玩家.
     */
    fun text(key: Key, player: Player): ScoreboardTextResult
}