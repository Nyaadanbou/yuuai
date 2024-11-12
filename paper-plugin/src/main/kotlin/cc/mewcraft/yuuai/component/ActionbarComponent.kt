package cc.mewcraft.yuuai.component

import cc.mewcraft.yuuai.TextResult
import net.kyori.adventure.key.Key
import org.bukkit.entity.Player

/**
 * 表示玩家 ui 内的 ActionBar 组件.
 */
interface ActionbarComponent : YuuaiComponent {
    /**
     * 根据输入的信息获取文字组件.
     *
     * @see TextResult
     *
     * @param key 组件的键, 如 `standalone:world_name`.
     * @param player 玩家.
     */
    fun text(key: Key, player: Player): TextResult
}