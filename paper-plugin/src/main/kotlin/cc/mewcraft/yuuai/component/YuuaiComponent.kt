package cc.mewcraft.yuuai.component

import cc.mewcraft.yuuai.Loadable

/**
 * 代表了一个作为玩家 ui 显示的组件, 提供了一些基本的方法
 */
interface YuuaiComponent : Loadable {
    /**
     * 组件允许的的命名空间.
     */
    val namespace: String
}