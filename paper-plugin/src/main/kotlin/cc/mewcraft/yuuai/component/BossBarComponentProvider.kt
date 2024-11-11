package cc.mewcraft.yuuai.component

import cc.mewcraft.yuuai.CheckResult
import org.spongepowered.configurate.ConfigurationNode

interface BossBarComponentProvider<B : BossBarComponent> : YuuaiComponentProvider<B> {
    /**
     * 检查现有环境是否符合这个工厂 Provider 的要求.
     *
     * 如检查工厂依赖的插件是否已经启用等.
     */
    fun check(node: ConfigurationNode): CheckResult

    /**
     * 从配置节点中获取一个用于创建 [net.kyori.adventure.bossbar.BossBar] 的工厂实例.
     */
    fun getBossBarFactory(node: ConfigurationNode): B
}