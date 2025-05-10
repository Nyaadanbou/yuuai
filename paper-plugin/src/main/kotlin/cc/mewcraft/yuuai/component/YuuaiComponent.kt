package cc.mewcraft.yuuai.component

import cc.mewcraft.yuuai.Loadable
import cc.mewcraft.yuuai.TextResult
import org.bukkit.entity.Player

/**
 * 代表了一个作为玩家 ui 显示的组件, 提供了一些基本的方法
 */
interface YuuaiComponent : Loadable {
    /**
     * 组件允许的的命名空间.
     */
    val namespace: String

    /**
     * 根据输入的信息获取文字组件.
     *
     * @see TextResult
     *
     * @param namespace 组件的命名空间.
     * @param arguments 参数.
     * @param player 玩家.
     */
    fun text(namespace: String, arguments: Array<String>, player: Player): TextResult
}