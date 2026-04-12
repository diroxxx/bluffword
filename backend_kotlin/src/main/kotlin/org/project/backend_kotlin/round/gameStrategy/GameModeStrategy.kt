package org.project.backend_kotlin.round.gameStrategy

import org.project.backend_kotlin.redisModels.GameMode
import org.project.backend_kotlin.redisModels.Player

public interface GameModeStrategy {
    val mode: GameMode
    fun assignImpostors(roomCode: String, currentRound: Int, players: List<Player>): List<String>
    fun isGameOver(roomCode: String, roundNumber: Int, topVotedIsImpostor: Boolean): Boolean
}
