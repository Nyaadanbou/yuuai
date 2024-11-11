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
     * 组件的刷新器, 用于刷新组件的显示内容.
     */
    val refresher: YuuaiRefresher?
}