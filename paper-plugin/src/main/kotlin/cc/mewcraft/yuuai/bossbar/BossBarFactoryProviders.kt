package cc.mewcraft.yuuai.bossbar

import cc.mewcraft.yuuai.bossbar.impl.OrientationBossBarFactory

object BossBarFactoryProviders {
    private val BOSS_BAR_FACTORIES: MutableMap<String, BossBarFactoryProvider<*>> = mutableMapOf()

    val ORIENTATION_BOSS_BAR_FACTORY = OrientationBossBarFactory.also { it.register(OrientationBossBarFactory.KEY) }

    private fun BossBarFactoryProvider<*>.register(id: String) {
        BOSS_BAR_FACTORIES[id] = this
    }

    fun getBossBarFactoryProvider(id: String): BossBarFactoryProvider<*>? {
        return BOSS_BAR_FACTORIES[id]
    }
}