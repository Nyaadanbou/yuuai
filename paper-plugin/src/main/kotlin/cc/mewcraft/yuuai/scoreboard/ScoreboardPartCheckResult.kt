package cc.mewcraft.yuuai.scoreboard

sealed interface ScoreboardPartCheckResult {
    data object Success : ScoreboardPartCheckResult

    class MissingDependency(vararg val missingDependencies: String) : ScoreboardPartCheckResult {
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