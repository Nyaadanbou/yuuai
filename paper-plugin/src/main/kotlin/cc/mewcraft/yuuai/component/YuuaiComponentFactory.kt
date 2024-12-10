package cc.mewcraft.yuuai.component

import cc.mewcraft.yuuai.CheckResult
import org.spongepowered.configurate.ConfigurationNode

/**
 * 代表了一个 [YuuaiComponent] 的提供者
 */
interface YuuaiComponentFactory<C : YuuaiComponent> {
    /**
     * 检查现有环境是否符合这个工厂 Provider 的要求.
     *
     * 如检查工厂依赖的插件是否已经启用等.
     */
    fun check(node: ConfigurationNode): CheckResult

    /**
     * 创建一个新的 [YuuaiComponent] 实例.
     */
    fun getComponent(node: ConfigurationNode): C
}