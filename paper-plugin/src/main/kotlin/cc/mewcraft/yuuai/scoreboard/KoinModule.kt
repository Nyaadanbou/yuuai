package cc.mewcraft.yuuai.scoreboard

import org.koin.core.module.Module
import org.koin.dsl.module

internal fun scoreboardModule(): Module = module {
    single { ScoreboardManager() }
}