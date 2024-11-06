package cc.mewcraft.yuuai.scoreboard

import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent

sealed interface SidebarComponentResult {
    data class Success(val value: SidebarComponent) : SidebarComponentResult
    data class InvalidNamespace(val correctNamespace: String) : SidebarComponentResult
    class InvalidKey(vararg val correctKeys: String) : SidebarComponentResult {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as InvalidKey

            return correctKeys.contentEquals(other.correctKeys)
        }

        override fun hashCode(): Int {
            return correctKeys.contentHashCode()
        }
    }
}