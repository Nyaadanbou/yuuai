package cc.mewcraft.yuuai.scoreboard

import org.spongepowered.configurate.ConfigurationNode

interface ScoreboardPartFactory<P : ScoreboardPart> {
    /**
     * 从配置文件中创建一个计分板部分
     */
    fun create(node: ConfigurationNode): P
}