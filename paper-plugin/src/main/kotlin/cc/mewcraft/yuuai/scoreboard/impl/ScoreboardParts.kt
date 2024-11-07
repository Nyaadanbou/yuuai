package cc.mewcraft.yuuai.scoreboard.impl

import cc.mewcraft.yuuai.scoreboard.ScoreboardPartFactory

object ScoreboardParts {
    private val PART_FACTORIES: MutableMap<String, ScoreboardPartFactory<*>> = mutableMapOf()
    val STANDALONE: ScoreboardPartFactory<StandalonePart> = StandalonePart.also { it.register(StandalonePart.NAMESPACE) }
    val ADVENTURE_LEVEL: ScoreboardPartFactory<AdventureLevelPart> = AdventureLevelPart.also { it.register(AdventureLevelPart.NAMESPACE) }
    val ECONOMY: ScoreboardPartFactory<EconomyPart> = EconomyPart.also { it.register(EconomyPart.NAMESPACE) }

    private fun ScoreboardPartFactory<*>.register(id: String) {
        PART_FACTORIES[id] = this
    }

    fun getPartFactory(id: String): ScoreboardPartFactory<*>? {
        return PART_FACTORIES[id]
    }
}