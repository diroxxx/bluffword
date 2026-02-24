package org.project.backend_kotlin.round

import org.project.backend_kotlin.gameRoom.GameRoomBroadcaster
import org.project.backend_kotlin.gameRoom.GameRoomRedisStore
import org.project.backend_kotlin.redisModels.GameRoomState
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.*
import kotlin.math.max

@Component
class RoundTimerService(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1),
    private val runningTimers: MutableMap<String, ScheduledFuture<*>> = ConcurrentHashMap(),
    private val gameRoomRedisStore: GameRoomRedisStore,
    private val gameRoomBroadcaster: GameRoomBroadcaster
) {


    fun startRoundTimer(roomCode: String, seconds: Int) {
        cancelTimer(roomCode)


        val endNanos = System.nanoTime() + TimeUnit.SECONDS.toNanos(seconds.toLong())
        println("sending $endNanos")
        simpMessagingTemplate.convertAndSend("/topic/round/$roomCode/time", seconds.toLong())

        var lastSentSeconds = seconds.toLong()

        val future = scheduler.scheduleAtFixedRate({
            val remainingNanos = endNanos - System.nanoTime()
            val remainingSeconds = max(0, TimeUnit.NANOSECONDS.toSeconds(remainingNanos))

            if (remainingSeconds != lastSentSeconds) {
                simpMessagingTemplate.convertAndSend("/topic/round/$roomCode/time", remainingSeconds)
                lastSentSeconds = remainingSeconds
            }

            if (remainingSeconds <= 0) {
                finishRound(roomCode)
            }
        }, 1, 1, TimeUnit.SECONDS)

        runningTimers[roomCode] = future

    }

    fun cancelTimer(roomCode: String) {
        val existing: ScheduledFuture<*>? = runningTimers.remove(roomCode)
        existing?.cancel(false)
    }


    private fun finishRound(roomCode: String) {
//        synchronized(roomCode.intern()) {
            val currentState = gameRoomRedisStore.getSpecificOption(roomCode, "state") as GameRoomState

            if (currentState == GameRoomState.ANSWERING)
                cancelTimer(roomCode)
                gameRoomRedisStore.updateSpecificOption(roomCode, "state", GameRoomState.RESULTS)
                gameRoomBroadcaster.broadcastGameRoomState(roomCode, GameRoomState.RESULTS)
//        }
    }

}
