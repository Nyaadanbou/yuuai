package cc.mewcraft.yuuai.scoreboard

import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent

sealed interface SidebarComponentResult {
    data class Success(val value: SidebarComponent) : SidebarComponentResult
    data class InvalidNamespace(val input: String, val correctNamespace: String) : SidebarComponentResult
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