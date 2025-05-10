package cc.mewcraft.yuuai.component

import cc.mewcraft.yuuai.component.impl.StandaloneComponent
import cc.mewcraft.yuuai.component.impl.WakameComponent
import org.koin.core.component.KoinComponent

object ActionbarComponents : KoinComponent {
    private val ACTIONBAR_FACTORIES: MutableMap<String, ActionbarComponentFactory<*>> = mutableMapOf()

    val STANDALONE = StandaloneComponent.also { it.register(StandaloneComponent.NAMESPACE) }
    val WAKAME = WakameComponent.also { it.register(WakameComponent.NAMESPACE) }

    private fun ActionbarComponentFactory<*>.register(id: String) {
        ACTIONBAR_FACTORIES[id] = this
    }

    fun getActionBarFactory(id: String): ActionbarComponentFactory<*>? {
        return ACTIONBAR_FACTORIES[id]
    }
}