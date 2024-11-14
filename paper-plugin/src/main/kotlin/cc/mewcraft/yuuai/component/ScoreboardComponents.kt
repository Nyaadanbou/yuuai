package cc.mewcraft.yuuai.component

import cc.mewcraft.yuuai.component.impl.AdventureLevelComponent
import cc.mewcraft.yuuai.component.impl.EconomyComponent
import cc.mewcraft.yuuai.component.impl.StandaloneComponent
import cc.mewcraft.yuuai.component.impl.TownyComponent

object ScoreboardComponents {
    private val COMPONENT_FACTORIES: MutableMap<String, ScoreboardComponentFactory<*>> = mutableMapOf()
    val ADVENTURE_LEVEL: ScoreboardComponentFactory<AdventureLevelComponent> = AdventureLevelComponent.also { it.register(AdventureLevelComponent.NAMESPACE) }
    val ECONOMY: ScoreboardComponentFactory<EconomyComponent> = EconomyComponent.also { it.register(EconomyComponent.NAMESPACE) }
    val STANDALONE: ScoreboardComponentFactory<StandaloneComponent> = StandaloneComponent.also { it.register(StandaloneComponent.NAMESPACE) }
    val TOWNY: ScoreboardComponentFactory<TownyComponent> = TownyComponent.also { it.register(TownyComponent.NAMESPACE) }

    private fun ScoreboardComponentFactory<*>.register(id: String) {
        COMPONENT_FACTORIES[id] = this
    }

    fun getComponentFactory(id: String): ScoreboardComponentFactory<*>? {
        return COMPONENT_FACTORIES[id]
    }
}