package cc.mewcraft.yuuai.scoreboard

import cc.mewcraft.yuuai.CheckResult
import org.spongepowered.configurate.ConfigurationNode

interface ScoreboardPartFactory<P : ScoreboardPart> {
    /**
     * 检查现有环境是否符合这个工厂的要求.
     *
     * 如检查工厂依赖的插件是否已经启用等.
     */
    fun check(node: ConfigurationNode): CheckResult

    /**
     * 从配置文件中创建一个计分板部分.
     */
    fun create(node: ConfigurationNode): P
}