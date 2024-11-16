package cc.mewcraft.yuuai.actionbar

import cc.mewcraft.yuuai.CheckResult
import cc.mewcraft.yuuai.component.ActionbarComponent
import cc.mewcraft.yuuai.component.ActionbarComponents
import cc.mewcraft.yuuai.util.reloadable
import org.slf4j.Logger
import org.spongepowered.configurate.loader.ConfigurationLoader

class ActionBarConfig(
    loader: ConfigurationLoader<*>,
    private val logger: Logger,
) {
    private val root by reloadable { loader.load() }

    val format: String by reloadable { root.node("format").string ?: "" }

    val actionBarComponents: List<ActionbarComponent> by reloadable(
        onLoad = { it.forEach { actionbarComponent -> actionbarComponent.load() } },
        onUnload = { it?.forEach { actionbarComponent -> actionbarComponent.unload() } }
    ) {
        val actionbars = mutableListOf<ActionbarComponent>()
        for ((key, node) in root.node("formats").childrenMap()) {
            val actionbarFactory = ActionbarComponents.getActionBarFactory(key.toString())
            if (actionbarFactory == null) {
                logger.warn("Unknown actionbar factory key: $key")
                continue
            }

            when (val checkResult = actionbarFactory.check(node)) {
                is CheckResult.Success -> Unit
                is CheckResult.Disabled -> {
                    logger.info("Actionbar factory is disabled: $key")
                    continue
                }
                is CheckResult.MissingDependency -> {
                    logger.warn("Missing dependency for actionbar factory: $key, dependencies: ${checkResult.missingDependencies.joinToString()}")
                    continue
                }

            }

            runCatching { actionbarFactory.getComponent(node) }
                .onFailure { logger.warn("Failed to get actionbar factory: $key", it) }
                .onSuccess { actionbarComponent -> actionbars.add(actionbarComponent) }
        }
        actionbars
    }
}