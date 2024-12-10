package cc.mewcraft.yuuai

sealed interface CheckResult {
    val isSuccessful: Boolean
        get() = this is Success

    data object Success : CheckResult

    data object Disabled : CheckResult

    class MissingDependency(vararg val missingDependencies: String) : CheckResult {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MissingDependency

            return missingDependencies.contentEquals(other.missingDependencies)
        }

        override fun hashCode(): Int {
            return missingDependencies.contentHashCode()
        }
    }
}