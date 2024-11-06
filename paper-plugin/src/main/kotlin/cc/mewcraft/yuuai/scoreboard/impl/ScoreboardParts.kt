package cc.mewcraft.yuuai.scoreboard.impl

import cc.mewcraft.yuuai.scoreboard.ScoreboardPartFactory

object ScoreboardParts {
    private val PART_FACTORIES: MutableMap<String, ScoreboardPartFactory<*>> = mutableMapOf()
    val STANDALONE: ScoreboardPartFactory<StandalonePart> = StandalonePart.also { it.register() }

    private fun ScoreboardPartFactory<*>.register() {
        PART_FACTORIES["standalone"] = this
    }

    fun getPartFactory(id: String): ScoreboardPartFactory<*>? {
        return PART_FACTORIES[id]
    }
}