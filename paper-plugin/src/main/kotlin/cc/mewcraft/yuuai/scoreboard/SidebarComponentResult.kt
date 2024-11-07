package cc.mewcraft.yuuai.scoreboard

import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent

/**
 * 代表 [SidebarComponent] 的结果。
 */
sealed interface SidebarComponentResult {
    /**
     * 代表成功的结果。
     */
    data class Success(val value: SidebarComponent) : SidebarComponentResult

    /**
     * 代表无效的命名空间。
     */
    data class InvalidNamespace(val input: String, val correctNamespace: String) : SidebarComponentResult

    /**
     * 代表无效的值。
     */
    class InvalidValues(val input: String, vararg val correctValues: String) : SidebarComponentResult {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as InvalidValues

            return correctValues.contentEquals(other.correctValues)
        }

        override fun hashCode(): Int {
            return correctValues.contentHashCode()
        }
    }
}