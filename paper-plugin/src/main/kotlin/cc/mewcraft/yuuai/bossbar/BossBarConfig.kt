package cc.mewcraft.yuuai.bossbar

import cc.mewcraft.yuuai.CheckResult
import cc.mewcraft.yuuai.util.reloadable
import org.slf4j.Logger
import org.spongepowered.configurate.loader.ConfigurationLoader

class BossBarConfig(
    loader: ConfigurationLoader<*>,
    private val logger: Logger,
) {
    private val root by reloadable { loader.load() }

    val bossBarFactories: List<BossBarFactory> by reloadable {
        val bossBars = mutableListOf<BossBarFactory>()
        for ((key, node) in root.childrenMap()) {
            val bossBarFactory = BossBarFactoryProviders.getBossBarFactoryProvider(key.toString())
            if (bossBarFactory == null) {
                logger.warn("Unknown bossbar factory key: $key")
                continue
            }

            when (val checkResult = bossBarFactory.check(node)) {
                is CheckResult.Success -> Unit
                is CheckResult.MissingDependency -> {
                    logger.warn("Missing dependency for bossbar factory: $key, dependencies: ${checkResult.missingDependencies}")
                    continue
                }
            }

            runCatching { bossBarFactory.getBossBarFactory(node) }
                .onFailure { logger.warn("Failed to get bossbar factory: $key", it) }
                .onSuccess {
                    if (it.isEnable)
                        bossBars.add(it)
                }
        }
        bossBars
    }
}