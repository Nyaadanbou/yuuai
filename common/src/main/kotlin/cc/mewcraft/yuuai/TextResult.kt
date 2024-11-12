package cc.mewcraft.yuuai

import net.kyori.adventure.text.Component

/**
 * 代表 [Component] 的结果。
 */
sealed interface TextResult {
    /**
     * 代表成功的结果。
     */
    data class Success(val value: Component) : TextResult

    /**
     * 代表无效的命名空间。
     */
    data class InvalidNamespace(val input: String, val correctNamespace: String) : TextResult

    /**
     * 代表无效的值。
     */
    class InvalidValues(val input: String, vararg val correctValues: String) : TextResult {
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