package org.project.backend_kotlin.round

import org.project.backend_kotlin.gameRoom.GameRoomBroadcaster
import org.project.backend_kotlin.gameRoom.GameRoomRedisStore
import org.project.backend_kotlin.redisModels.GameRoomState
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.*
import kotlin.compareTo
import kotlin.math.max

@Component
class RoundTimerService(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1),
    private val runningTimers: MutableMap<String, ScheduledFuture<*>> = ConcurrentHashMap(),
    private val gameRoomRedisStore: GameRoomRedisStore,
    private val gameRoomBroadcaster: GameRoomBroadcaster,
    private val roundRedisStore: RoundRedisStore
) {


    fun startRoundTimer(roomCode: String, seconds: Int) {
        cancelTimer(roomCode)


        val endNanos = System.nanoTime() + TimeUnit.SECONDS.toNanos(seconds.toLong())
        println("sending $endNanos")
        simpMessagingTemplate.convertAndSend("/topic/round/$roomCode/time", seconds.toLong())

        var lastSentSeconds = seconds.toLong()

        val future = scheduler.scheduleAtFixedRate({
            try {
                val currentState = gameRoomRedisStore.getSpecificOption(roomCode, "state") as? GameRoomState
                val remainingNanos = endNanos - System.nanoTime()
                val remainingSeconds = max(0, TimeUnit.NANOSECONDS.toSeconds(remainingNanos))

                println("Timer loop: state=$currentState, remainingSeconds=$remainingSeconds")

                if (currentState == GameRoomState.RESULTS) {
                    println("Timer loop: State is RESULTS, canceling timer")
                    cancelTimer(roomCode)
                }

                if (remainingSeconds != lastSentSeconds) {
                    println("Sending time: $remainingSeconds")
                    simpMessagingTemplate.convertAndSend("/topic/round/$roomCode/time", remainingSeconds)
                    lastSentSeconds = remainingSeconds
                }

                if (remainingSeconds <= 0) {
                    println("Time is up, calling finishRound")
                    finishRound(roomCode)
                    return@scheduleAtFixedRate
                }
            } catch (e: Exception) {
                println("ERROR in timer loop: ${e.message}")
                e.printStackTrace()
            }
        }, 1, 1, TimeUnit.SECONDS)

        runningTimers[roomCode] = future

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


    private fun finishRound(roomCode: String) {
        synchronized(roomCode.intern()) {
            val currentState = gameRoomRedisStore.getSpecificOption(roomCode, "state") as GameRoomState
            println("finishRound called: currentState = $currentState for $roomCode")

            if (currentState == GameRoomState.ANSWERING) {
                println("finishRound: Canceling timer and setting state to RESULTS")
                cancelTimer(roomCode)
                gameRoomRedisStore.updateSpecificOption(roomCode, "state", GameRoomState.RESULTS)
                gameRoomBroadcaster.broadcastGameRoomState(roomCode, GameRoomState.RESULTS)
            } else {
                cancelTimer(roomCode)
                println("finishRound: Skipping - state is not ANSWERING but $currentState")
            }
        }
    }

}
