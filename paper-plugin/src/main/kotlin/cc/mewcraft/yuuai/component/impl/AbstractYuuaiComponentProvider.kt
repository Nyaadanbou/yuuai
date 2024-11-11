package cc.mewcraft.yuuai.component.impl

import cc.mewcraft.yuuai.CheckResult
import cc.mewcraft.yuuai.Injector
import cc.mewcraft.yuuai.YuuaiPlugin
import cc.mewcraft.yuuai.component.YuuaiComponent
import cc.mewcraft.yuuai.component.YuuaiComponentProvider
import org.koin.core.component.get
import org.spongepowered.configurate.ConfigurationNode

abstract class AbstractYuuaiComponentProvider<C : YuuaiComponent> : YuuaiComponentProvider<C> {
    fun isEnabled(node: ConfigurationNode): Boolean {
        return node.node("enabled").getBoolean(false)
    }

    fun checkDependencies(vararg pluginRequired: String): CheckResult {
        val pluginsNotFound = mutableListOf<String>()
        for (pString in pluginRequired) {
            Injector.get<YuuaiPlugin>().server.pluginManager.getPlugin(pString)
                ?: pluginsNotFound.add(pString)
        }
        return if (pluginsNotFound.isNotEmpty()) {
            CheckResult.MissingDependency(*pluginsNotFound.toTypedArray())
        } else {
            CheckResult.Success
        }
    }
}