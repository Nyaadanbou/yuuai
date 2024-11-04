package cc.mewcraft.yuuai.scoreboard

import cc.mewcraft.yuuai.YuuaiPlugin
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ScoreboardManager : KoinComponent {
    private val plugin: YuuaiPlugin by inject()
    private val scoreboardLibrary: ScoreboardLibrary

    init {
        scoreboardLibrary = try {
            ScoreboardLibrary.loadScoreboardLibrary(plugin)
        } catch (e: NoPacketAdapterAvailableException) {
            // If no packet adapter was found, you can fallback to the no-op implementation:
            plugin.componentLogger.warn("No scoreboard packet adapter available!")
            NoopScoreboardLibrary()
        }
    }

    fun close() {
        scoreboardLibrary.close()
    }
}