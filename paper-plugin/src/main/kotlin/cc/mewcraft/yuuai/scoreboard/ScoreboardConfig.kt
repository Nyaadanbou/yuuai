package cc.mewcraft.yuuai.scoreboard

import cc.mewcraft.yuuai.scoreboard.impl.ScoreboardParts
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
    val scoreboardParts: List<ScoreboardPart> by reloadable {
        val parts = mutableListOf<ScoreboardPart>()
        for ((key, node) in root.node("formats").childrenMap()) {
            val partFactory = ScoreboardParts.getPartFactory(key.toString())
            if (partFactory == null) {
                logger.warn("Unknown scoreboard part: $key")
                continue
            }

            when (val checkResult = partFactory.check(node)) {
                is ScoreboardPartCheckResult.Success -> Unit
                is ScoreboardPartCheckResult.MissingDependency -> {
                    logger.warn("Missing dependency for scoreboard part: $key, dependencies: ${checkResult.missingDependencies}")
                    continue
                }
            }

            runCatching { partFactory.create(node) }
                .onFailure { logger.warn("Failed to create scoreboard part: $key", it) }
                .onSuccess { parts.add(it) }
        }
        parts
    }
}