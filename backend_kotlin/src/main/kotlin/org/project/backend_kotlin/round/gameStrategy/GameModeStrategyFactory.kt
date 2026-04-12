package org.project.backend_kotlin.round.gameStrategy

import org.project.backend_kotlin.redisModels.GameMode
import org.springframework.stereotype.Component

@Component
class GameModeStrategyFactory(strategies: List<GameModeStrategy>) {

    private val map: Map<GameMode, GameModeStrategy> = strategies.associateBy { it.mode }

    fun get(mode: GameMode): GameModeStrategy =
        map[mode] ?: error("No strategy found for game mode: $mode")
}