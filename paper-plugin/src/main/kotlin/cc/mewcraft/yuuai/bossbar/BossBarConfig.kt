package cc.mewcraft.yuuai.bossbar

import cc.mewcraft.yuuai.CheckResult
import cc.mewcraft.yuuai.YuuaiPlugin
import cc.mewcraft.yuuai.component.BossBarComponent
import cc.mewcraft.yuuai.component.BossBarComponents
import cc.mewcraft.yuuai.util.reloadable
import org.slf4j.Logger
import org.spongepowered.configurate.loader.ConfigurationLoader

class BossBarConfig(
    loader: ConfigurationLoader<*>,
    private val plugin: YuuaiPlugin,
    private val logger: Logger,
) {
    private val root by reloadable { loader.load() }

    val bossBarFactories: List<BossBarComponent> by reloadable {
        val bossBars = mutableListOf<BossBarComponent>()
        for ((key, node) in root.childrenMap()) {
            val bossBarFactory = BossBarComponents.getBossBarFactoryProvider(key.toString())
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
                    logger.warn("Missing dependency for bossbar factory: $key, dependencies: ${checkResult.missingDependencies}")
                    continue
                }

            }

            runCatching { bossBarFactory.getBossBarFactory(node) }
                .onFailure { logger.warn("Failed to get bossbar factory: $key", it) }
                .onSuccess { bossBarComponent ->
                    bossBarComponent.refresher?.let { plugin.registerSuspendListener(it) }
                    bossBars.add(bossBarComponent)
                }
        }
        bossBars
    }
}