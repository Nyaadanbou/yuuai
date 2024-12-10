package cc.mewcraft.yuuai.bossbar

import cc.mewcraft.yuuai.CheckResult
import cc.mewcraft.yuuai.component.BossBarComponent
import cc.mewcraft.yuuai.component.BossBarComponents
import cc.mewcraft.yuuai.util.reloadable
import org.slf4j.Logger
import org.spongepowered.configurate.loader.ConfigurationLoader

class BossBarConfig(
    loader: ConfigurationLoader<*>,
    private val logger: Logger,
) {
    private val root by reloadable { loader.load() }

    val bossBarComponents: List<BossBarComponent> by reloadable(
        onLoad = { it.forEach { bossBarComponent -> bossBarComponent.load() } },
        onUnload = { it?.forEach { bossBarComponent -> bossBarComponent.unload() } }
    ) {
        val bossBars = mutableListOf<BossBarComponent>()
        for ((key, node) in root.childrenMap()) {
            val bossBarFactory = BossBarComponents.getBossBarFactory(key.toString())
            if (bossBarFactory == null) {
                logger.warn("Unknown bossbar factory key: $key")
                continue
            }

            when (val checkResult = bossBarFactory.check(node)) {
                is CheckResult.Success -> Unit
                is CheckResult.Disabled -> {
                    logger.info("Bossbar factory is disabled: $key")
                    continue
                }
                is CheckResult.MissingDependency -> {
                    logger.warn("Missing dependency for bossbar factory: $key, dependencies: ${checkResult.missingDependencies.joinToString()}")
                    continue
                }

            }

            runCatching { bossBarFactory.getComponent(node) }
                .onFailure { logger.warn("Failed to get bossbar factory: $key", it) }
                .onSuccess { bossBarComponent -> bossBars.add(bossBarComponent) }
        }
        bossBars
    }
}