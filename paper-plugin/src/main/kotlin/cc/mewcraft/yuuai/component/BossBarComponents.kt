package cc.mewcraft.yuuai.component

import cc.mewcraft.yuuai.component.impl.OrientationComponent
import org.koin.core.component.KoinComponent

object BossBarComponents : KoinComponent {
    private val BOSS_BAR_FACTORIES: MutableMap<String, BossBarComponentProvider<*>> = mutableMapOf()

    val ORIENTATION = OrientationComponent.also { it.register(OrientationComponent.NAMESPACE) }

    private fun BossBarComponentProvider<*>.register(id: String) {
        BOSS_BAR_FACTORIES[id] = this
    }

    fun getBossBarFactoryProvider(id: String): BossBarComponentProvider<*>? {
        return BOSS_BAR_FACTORIES[id]
    }
}