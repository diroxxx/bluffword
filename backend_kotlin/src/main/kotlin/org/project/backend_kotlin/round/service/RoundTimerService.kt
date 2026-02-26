package org.project.backend_kotlin.round.service

import org.project.backend_kotlin.gameRoom.GameRoomBroadcaster
import org.project.backend_kotlin.gameRoom.GameRoomRedisStore
import org.project.backend_kotlin.gameRoom.GameStateTransitionService
import org.project.backend_kotlin.redisModels.GameRoomState
import org.project.backend_kotlin.round.dto.TimerType
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.max

@Component
class RoundTimerService(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1),
    private val runningTimers: MutableMap<String, ScheduledFuture<*>> = ConcurrentHashMap(),
    private val gameRoomRedisStore: GameRoomRedisStore,
    private val gameRoomBroadcaster: GameRoomBroadcaster,
    private val gameStateTransitionService: GameStateTransitionService
) {

    fun startRoundTimer(roomCode: String, seconds: Int, timerType: TimerType) {

        val endNanos = System.nanoTime() + TimeUnit.SECONDS.toNanos(seconds.toLong())

        val topic = "/topic/round/$roomCode/${timerType.name}/time"

        simpMessagingTemplate.convertAndSend(topic, seconds.toLong())

        var lastSentSeconds = seconds.toLong()

        val future = scheduler.scheduleAtFixedRate({
            try {
                val config = gameRoomRedisStore.getGameRoomConfig(roomCode)
                val remainingNanos = endNanos - System.nanoTime()
                val remainingSeconds = max(0, TimeUnit.NANOSECONDS.toSeconds(remainingNanos))


                if (shouldCancelTimer(config.state, timerType)) {
                    cancelTimer(roomCode)
                    return@scheduleAtFixedRate
                }

                if (remainingSeconds <= 0) {
                    println("Time is up, calling finishRound")
                    finishState(roomCode)
                    return@scheduleAtFixedRate
                }

                if (remainingSeconds != lastSentSeconds) {
                    println("Sending time: $remainingSeconds")
                    simpMessagingTemplate.convertAndSend(topic, remainingSeconds)
                    lastSentSeconds = remainingSeconds
                }

            } catch (e: Exception) {
                println("ERROR in timer loop: ${e.message}")
                e.printStackTrace()
            }
        }, 1, 1, TimeUnit.SECONDS)

        runningTimers[roomCode] = future

    }

    private fun shouldCancelTimer(currentState: GameRoomState, timerType: TimerType): Boolean {
        return when (timerType) {
            TimerType.ANSWERING -> currentState != GameRoomState.ANSWERING
            TimerType.VOTING -> currentState != GameRoomState.VOTING
        }
    }

    fun cancelTimer(roomCode: String) {
        synchronized(roomCode.intern()) {
            val future = runningTimers[roomCode]
            if (future != null && !future.isCancelled) {
                future.cancel(false)
                runningTimers.remove(roomCode)
            }
        }
    }

    private fun finishState(roomCode: String) {
        synchronized(roomCode.intern()) {
            val config = gameRoomRedisStore.getGameRoomConfig(roomCode)

            cancelTimer(roomCode)

//            when (config.state) {
//                GameRoomState.ANSWERING -> {
//                    println("finishRound: Canceling timer and setting state to RESULTS")
//                    gameStateTransitionService.setState(roomCode, GameRoomState.ANSWERING_RESULTS)
//                }
//                GameRoomState.VOTING -> {
//                    println("finishRound: Canceling timer and setting state to RESULTS")
//                    gameStateTransitionService.setState(roomCode, GameRoomState.VOTING_RESULTS)
//                }
//                else -> {
//                    cancelTimer(roomCode)
//                }
//            }
        }
    }

}