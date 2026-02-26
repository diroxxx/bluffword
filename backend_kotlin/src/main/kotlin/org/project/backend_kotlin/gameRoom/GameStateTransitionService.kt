package org.project.backend_kotlin.gameRoom

import org.project.backend_kotlin.redisModels.GameRoomState
import org.project.backend_kotlin.round.service.RoundTimerService
import org.springframework.stereotype.Service

@Service
class GameStateTransitionService(
    private val gameRoomRedisStore: GameRoomRedisStore,
    private val gameRoomBroadcaster: GameRoomBroadcaster,
) {

    fun setState(roomCode: String, newState: GameRoomState) {

        //add maybe some validation here

        gameRoomRedisStore.updateSpecificOption(roomCode, "state", newState)
        gameRoomBroadcaster.broadcastGameRoomState(roomCode, newState)
    }
}