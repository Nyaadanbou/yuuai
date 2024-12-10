package cc.mewcraft.yuuai

interface Loadable {

    /**
     * 加载逻辑。
     */
    fun load()

    /**
     * 卸载逻辑.
     */
    fun unload()
}

fun Loadable(
    onLoad: () -> Unit = {},
    onUnload: () -> Unit = {}
): Loadable {
    return object : Loadable {
        override fun load() {
            onLoad()
        }

        override fun unload() {
            onUnload()
        }
    }
}