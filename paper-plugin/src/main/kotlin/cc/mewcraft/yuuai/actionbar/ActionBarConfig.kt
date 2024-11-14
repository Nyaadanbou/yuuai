package cc.mewcraft.yuuai.actionbar

import cc.mewcraft.yuuai.CheckResult
import cc.mewcraft.yuuai.YuuaiPlugin
import cc.mewcraft.yuuai.component.ActionbarComponent
import cc.mewcraft.yuuai.component.ActionbarComponents
import cc.mewcraft.yuuai.util.reloadable
import org.slf4j.Logger
import org.spongepowered.configurate.loader.ConfigurationLoader

class ActionBarConfig(
    loader: ConfigurationLoader<*>,
    private val plugin: YuuaiPlugin,
    private val logger: Logger,
) {
    private val root by reloadable { loader.load() }

    val format: String by reloadable { root.node("format").string ?: "" }

    val actionBarComponents: List<ActionbarComponent> by reloadable {
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
                    logger.warn("Missing dependency for actionbar factory: $key, dependencies: ${checkResult.missingDependencies}")
                    continue
                }

            }

            runCatching { actionbarFactory.getComponent(node) }
                .onFailure { logger.warn("Failed to get actionbar factory: $key", it) }
                .onSuccess { bossBarComponent ->
                    bossBarComponent.refresher?.let { plugin.registerSuspendListener(it) }
                    actionbars.add(bossBarComponent)
                }
        }
        actionbars
    }
}