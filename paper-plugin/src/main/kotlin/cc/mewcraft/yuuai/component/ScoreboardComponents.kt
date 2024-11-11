package cc.mewcraft.yuuai.component

import cc.mewcraft.yuuai.component.impl.AdventureLevelComponent
import cc.mewcraft.yuuai.component.impl.EconomyComponent
import cc.mewcraft.yuuai.component.impl.StandaloneComponent

object ScoreboardComponents {
    private val COMPONENT_FACTORIES: MutableMap<String, ScoreboardComponentFactory<*>> = mutableMapOf()
    val STANDALONE: ScoreboardComponentFactory<StandaloneComponent> = StandaloneComponent.also { it.register(StandaloneComponent.NAMESPACE) }
    val ADVENTURE_LEVEL: ScoreboardComponentFactory<AdventureLevelComponent> = AdventureLevelComponent.also { it.register(AdventureLevelComponent.NAMESPACE) }
    val ECONOMY: ScoreboardComponentFactory<EconomyComponent> = EconomyComponent.also { it.register(EconomyComponent.NAMESPACE) }

    private fun ScoreboardComponentFactory<*>.register(id: String) {
        COMPONENT_FACTORIES[id] = this
    }

    fun getPartFactory(id: String): ScoreboardComponentFactory<*>? {
        return COMPONENT_FACTORIES[id]
    }
}