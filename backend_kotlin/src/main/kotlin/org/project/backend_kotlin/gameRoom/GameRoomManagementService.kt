package org.project.backend_kotlin.gameRoom

import org.project.backend_kotlin.gameRoom.dto.GameSettingsRequestDto
import org.project.backend_kotlin.redisModels.GameRoom
import org.project.backend_kotlin.redisModels.GameRoomState
import org.project.backend_kotlin.redisModels.Player
import org.springframework.stereotype.Service
import java.util.Locale
import java.util.UUID

@Service

class GameRoomManagementService(
    private val gameRoomRedisStore: GameRoomRedisStore,
    private val gameRoomBroadcaster: GameRoomBroadcaster
) {


    private fun generateRoomCode(): String {
        return UUID.randomUUID().toString().substring(0, 6).uppercase(Locale.getDefault())
    }

    private fun generatePlayerId(): String {
        return UUID.randomUUID().toString()
    }

    fun createGameRoom(nickname: String, settings: GameSettingsRequestDto): Player {
        val roomCode = generateRoomCode()
        val host = Player(generatePlayerId(),nickname, true, roomCode)
        gameRoomRedisStore.createRoom(roomCode, settings, host.id)
        gameRoomRedisStore.addPlayerToRoom(roomCode, host)

        gameRoomBroadcaster.broadcastPlayers(roomCode, getPlayers(roomCode))

        return host
    }

    fun joinGameRoom(roomCode: String, nickname: String): Player {
        val player = Player(generatePlayerId(),nickname, false, roomCode)
        gameRoomRedisStore.addPlayerToRoom(roomCode, player)

        gameRoomBroadcaster.broadcastPlayers(roomCode, getPlayers(roomCode))

        return player
    }

    fun getPlayers(roomCode: String) : List<Player> {

        val players = gameRoomRedisStore.getPlayersFromRoom(roomCode)

        return players
    }

    fun deletePlayerFromRoom(roomCode: String, playerId: String) {
        gameRoomRedisStore.removePlayer(roomCode, playerId)
    }

    fun getGameRoomConfig(roomCode: String): GameRoom {
        return gameRoomRedisStore.getGameRoomConfig(roomCode)
    }

    fun getRoomState(roomCode: String): GameRoomState =
        gameRoomRedisStore.getSpecificOption(roomCode,"state") as GameRoomState



}