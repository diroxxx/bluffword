package org.project.backend_kotlin.round.service

import org.project.backend_kotlin.gameRoom.GameRoomRedisStore
import org.project.backend_kotlin.redisModels.GameRoomState
import org.project.backend_kotlin.round.GameFlowFacade
import org.project.backend_kotlin.round.RoundRedisStore
import org.project.backend_kotlin.round.dto.RoundAnswer
import org.springframework.stereotype.Service

@Service
class AnsweringService(
    private val roundRedisStore: RoundRedisStore,
    private val gameRoomRedisStore: GameRoomRedisStore,
    private val gameFlowFacade: GameFlowFacade,
) {

    fun saveAnswer(roomCode: String, roundNumber: Int, answer: String, playerId: String) {
        val gameRoomConfig = gameRoomRedisStore.getGameRoomConfig(roomCode)
        if (gameRoomConfig.state != GameRoomState.ANSWERING) return

        val currentRoundFromRedis = gameRoomRedisStore.getIntOption(roomCode, "currentRound")
        if (roundNumber != currentRoundFromRedis) return

        roundRedisStore.saveAnswer(roomCode, roundNumber, answer, playerId)
        checkIfEveryoneAnswered(roomCode, roundNumber)
    }

    private fun checkIfEveryoneAnswered(roomCode: String, roundNumber: Int) {
        if (!roundRedisStore.checkIfEveryoneAnswered(roomCode, roundNumber)) return
        gameFlowFacade.transitionToVoting(roomCode)
    }

    fun getRoundAnswers(roomCode: String, roundNumber: Int): List<RoundAnswer> =
        roundRedisStore.getAnswers(roomCode, roundNumber)
}
