package org.project.backend_kotlin.round.gameStrategy

import org.project.backend_kotlin.gameRoom.GameRoomRedisStore
import org.project.backend_kotlin.redisModels.GameMode
import org.project.backend_kotlin.redisModels.Player
import org.project.backend_kotlin.round.redisService.RoundRedisStore
import org.springframework.stereotype.Component

@Component
class RoundImpostorStrategy(
    private val roundRedisStore: RoundRedisStore,
    private val gameRoomRedisStore: GameRoomRedisStore,
) : GameModeStrategy {

    override val mode = GameMode.ROUND_IMPOSTOR

    // Assigning impostor role to players
    override fun assignImpostors(roomCode: String, currentRound: Int, players: List<Player>): List<String> {
        val numberOfImpostors = gameRoomRedisStore.getIntOption(roomCode, "numberOfImpostors")
        val ids = players.shuffled().take(numberOfImpostors).map { it.id }
        roundRedisStore.saveImpostorIds(roomCode, currentRound, ids)
        return ids
    }

    // Game is over when the round total is reached
    override fun isGameOver(roomCode: String, roundNumber: Int, topVotedIsImpostor: Boolean): Boolean {
        val roundTotal = gameRoomRedisStore.getIntOption(roomCode, "roundTotal")
        return roundNumber >= roundTotal
    }
}