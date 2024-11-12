package cc.mewcraft.yuuai.component

import cc.mewcraft.yuuai.component.impl.OrientationComponent
import org.koin.core.component.KoinComponent

object BossBarComponents : KoinComponent {
    private val BOSS_BAR_FACTORIES: MutableMap<String, BossBarComponentFactory<*>> = mutableMapOf()

    val ORIENTATION = OrientationComponent.also { it.register(OrientationComponent.NAMESPACE) }

    private fun BossBarComponentFactory<*>.register(id: String) {
        BOSS_BAR_FACTORIES[id] = this
    }

    fun getBossBarFactory(id: String): BossBarComponentFactory<*>? {
        return BOSS_BAR_FACTORIES[id]
    }
}