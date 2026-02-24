package org.project.backend_kotlin.gameRoom

import org.project.backend_kotlin.redisModels.GameRoomState
import org.project.backend_kotlin.redisModels.Player
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class GameRoomBroadcaster(
    private val messagingTemplate: SimpMessagingTemplate
) {

    fun broadcastPlayers(roomCode: String, players: List<Player>) =
        messagingTemplate.convertAndSend("/topic/room/$roomCode/players", players)

    fun broadcastGameRoomState(roomCode: String, gameRoomState: GameRoomState) =
        messagingTemplate.convertAndSend("/topic/room/$roomCode/state", gameRoomState)



}