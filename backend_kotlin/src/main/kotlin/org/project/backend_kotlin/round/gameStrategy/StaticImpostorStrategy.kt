package org.project.backend_kotlin.round.gameStrategy

import org.project.backend_kotlin.gameRoom.GameRoomRedisStore
import org.project.backend_kotlin.redisModels.GameMode
import org.project.backend_kotlin.redisModels.Player
import org.project.backend_kotlin.round.redisService.RoundRedisStore

import org.springframework.stereotype.Component

@Component
class StaticImpostorStrategy(
    private val roundRedisStore: RoundRedisStore,
    private val gameRoomRedisStore: GameRoomRedisStore,
) : GameModeStrategy {

    override val mode = GameMode.STATIC_IMPOSTOR

    /*
    For static impostor strategy, impostors are assigned at the beginning of the game.
    It is not possible to change impostors after the first round.
     */
    override fun assignImpostors(roomCode: String, currentRound: Int, players: List<Player>): List<String> {
        if (currentRound == 1) {
            val numberOfImpostors = gameRoomRedisStore.getIntOption(roomCode, "numberOfImpostors")
            val ids = players.shuffled().take(numberOfImpostors).map { it.id }
            roundRedisStore.saveImpostorIds(roomCode, 1, ids)
            return ids
        }
        return roundRedisStore.getImpostorIds(roomCode, 1)
    }

    /*
    checks if the game is over based on the impostor status and the round number.
     */
    override fun isGameOver(roomCode: String, roundNumber: Int, topVotedIsImpostor: Boolean): Boolean {
        val roundTotal = gameRoomRedisStore.getIntOption(roomCode, "roundTotal")
        return roundNumber >= roundTotal || topVotedIsImpostor
    }
}