package cc.mewcraft.yuuai.component

/**
 * 代表了一个作为玩家 ui 显示的组件, 提供了一些基本的方法
 */
interface YuuaiComponent {
    /**
     * 组件允许的的命名空间.
     */
    val namespace: String

    /**
     * 加载逻辑。
     */
    fun load()

    /**
     * 卸载逻辑.
     */
    fun unload()
}