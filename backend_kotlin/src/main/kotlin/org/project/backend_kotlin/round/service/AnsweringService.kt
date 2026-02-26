package org.project.backend_kotlin.round.service

import org.project.backend_kotlin.gameRoom.GameRoomBroadcaster
import org.project.backend_kotlin.gameRoom.GameRoomRedisStore
import org.project.backend_kotlin.gameRoom.GameStateTransitionService
import org.project.backend_kotlin.redisModels.GameRoomState
import org.project.backend_kotlin.round.RoundBroadcaster
import org.project.backend_kotlin.round.RoundRedisStore
import org.project.backend_kotlin.round.service.RoundTimerService
import org.project.backend_kotlin.round.dto.RoundAnswer
import org.project.backend_kotlin.round.dto.TimerType
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Service
class AnsweringService(
    private val roundRedisStore: RoundRedisStore,
    private val roundTimer: RoundTimerService,
    private val gameRoomRedisStore: GameRoomRedisStore,
    private val gameRoomBroadcaster: GameRoomBroadcaster,
    private val roundBroadcaster: RoundBroadcaster,
    private val stateTransition: GameStateTransitionService,
    private val voteTimeScheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),

    ) {

    fun saveAnswer(roomCode: String,roundNumber: Int, answer: String, playerId: String) {
        val gameRoomConfig = gameRoomRedisStore.getGameRoomConfig(roomCode)
        if (gameRoomConfig.state != GameRoomState.ANSWERING) return

        val currentRoundFromRedis = gameRoomRedisStore.getIntOption(roomCode, "currentRound")
        if (roundNumber != currentRoundFromRedis) return

        roundRedisStore.saveAnswer(roomCode, roundNumber,answer, playerId)

        checkIfEveryoneAnswered(roomCode, roundNumber)
    }

    fun checkIfEveryoneAnswered(roomCode: String, roundNumber: Int) {

        val checkIfEveryoneAnswered = roundRedisStore.checkIfEveryoneAnswered(roomCode, roundNumber)
        if (!checkIfEveryoneAnswered) return

        stateTransition.setState(roomCode, GameRoomState.VOTING)
        roundBroadcaster.broadcastRoundAnswers(roomCode, roundNumber,getRoundAnswers(roomCode,roundNumber))
        scheduleVoteTimeBroadcast(roomCode, roundNumber)
    }

    fun getRoundAnswers(roomCode: String, roundNumber: Int): List<RoundAnswer> {
        return roundRedisStore.getAnswers(roomCode, roundNumber)
    }

    private fun scheduleVoteTimeBroadcast(
        roomCode: String,
        currentRound: Int
    ) {
        voteTimeScheduler.schedule({
            try {
                println("SCHEDULER EXECUTED - before switching to VOTING")
                stateTransition.setState(roomCode, GameRoomState.VOTING)
                startVoteTimerIfActive(roomCode)
                println("SCHEDULER EXECUTED - after starting VOTING timer")
            } catch (e: Exception) {
                println("ERROR in scheduler: ${e.message}")
                e.printStackTrace()
            }
        }, 900, TimeUnit.MILLISECONDS)
    }

    private fun startVoteTimerIfActive(roomCode: String) {
        val gameRoom = gameRoomRedisStore.getGameRoomConfig(roomCode)
        println("startVoteTimerIfActive: ${gameRoom.state}")

        roundTimer.startRoundTimer(roomCode, gameRoom.timeLimitVote, TimerType.VOTING)
    }
}