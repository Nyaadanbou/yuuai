package cc.mewcraft.yuuai.scoreboard.impl

import cc.mewcraft.yuuai.scoreboard.ScoreboardPartFactory

object ScoreboardParts {
    private val PART_FACTORIES: MutableMap<String, ScoreboardPartFactory<*>> = mutableMapOf()
    val STANDALONE: ScoreboardPartFactory<StandalonePart> = StandalonePart.also { it.register("standalone") }
    val ECONOMY: ScoreboardPartFactory<EconomyPart> = EconomyPart.also { it.register("economy") }

    private fun ScoreboardPartFactory<*>.register(id: String) {
        PART_FACTORIES[id] = this
    }

    fun getPartFactory(id: String): ScoreboardPartFactory<*>? {
        return PART_FACTORIES[id]
    }
}