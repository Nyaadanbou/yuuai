package cc.mewcraft.yuuai.scoreboard

import cc.mewcraft.yuuai.CheckResult
import cc.mewcraft.yuuai.component.ScoreboardComponent
import cc.mewcraft.yuuai.component.ScoreboardComponents
import cc.mewcraft.yuuai.util.reloadable
import org.slf4j.Logger
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.loader.ConfigurationLoader

class ScoreboardConfig(
    loader: ConfigurationLoader<*>,
    private val logger: Logger,
) {
    private val root by reloadable { loader.load() }

    val layout: List<String> by reloadable { root.node("layout").get<List<String>>(emptyList()) }
    val scoreboardComponents: List<ScoreboardComponent> by reloadable {
        val components = mutableListOf<ScoreboardComponent>()
        for ((key, node) in root.node("formats").childrenMap()) {
            val partFactory = ScoreboardComponents.getComponentFactory(key.toString())
            if (partFactory == null) {
                logger.warn("Unknown scoreboard part: $key")
                continue
            }

            when (val checkResult = partFactory.check(node)) {
                is CheckResult.Success -> Unit
                is CheckResult.Disabled -> {
                    logger.warn("Disabled scoreboard part: $key")
                    continue
                }
                is CheckResult.MissingDependency -> {
                    logger.warn("Missing dependency for scoreboard part: $key, dependencies: ${checkResult.missingDependencies.joinToString()}")
                    continue
                }
            }

            runCatching { partFactory.getComponent(node) }
                .onFailure { logger.warn("Failed to create scoreboard part: $key", it) }
                .onSuccess { scoreboardComponent ->
                    scoreboardComponent.load()
                    components.add(scoreboardComponent)
                }
        }
        components
    }
}