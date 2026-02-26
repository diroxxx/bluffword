package org.project.backend_kotlin.gameRoom

import org.project.backend_kotlin.gameRoom.dto.CreateRoomRequestDto
import org.project.backend_kotlin.gameRoom.dto.JoinRoomRequestDto
import org.project.backend_kotlin.redisModels.Player
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import org.project.backend_kotlin.redisModels.GameRoom
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@RestController
@RequestMapping("/api/gameRoom")
class GameRoomController (
    private val gameRoomManagementService: GameRoomManagementService,
    private val gameRoomBroadcaster: GameRoomBroadcaster,

    ) {


    @PostMapping("/create")
    fun createGameRoom(@RequestBody @Valid createRoomDto: CreateRoomRequestDto) : ResponseEntity<Player> {

        val createdPlayer = gameRoomManagementService.createGameRoom(createRoomDto.nickname, createRoomDto.settings)
        println(createdPlayer)
        return ResponseEntity.ok(createdPlayer)

    }

    @PostMapping("/join")
    fun joinGameRoom(@RequestBody @Valid joinRoomRequestDto: JoinRoomRequestDto) : ResponseEntity<Player> {

        return ResponseEntity.ok(gameRoomManagementService.joinGameRoom(joinRoomRequestDto.roomCode, joinRoomRequestDto.nickname))
    }

    @MessageMapping("/room/{roomCode}/players")
    fun getPlayersFromRoom(@DestinationVariable roomCode: String) {
        println(gameRoomManagementService.getPlayers(roomCode))

        gameRoomBroadcaster.broadcastPlayers(roomCode, gameRoomManagementService.getPlayers(roomCode))
    }

    @MessageMapping("/room/{roomCode}/state")
    fun getRoomState(@NotEmpty @DestinationVariable roomCode: String) =
        gameRoomBroadcaster.broadcastGameRoomState(roomCode, gameRoomManagementService.getRoomState(roomCode))

    @DeleteMapping("/player")
    fun deletePlayerFromRoom(@RequestBody req: JoinRoomRequestDto) {
        // to change from nickname to playerId
        gameRoomManagementService.deletePlayerFromRoom(req.roomCode, req.nickname)
        gameRoomBroadcaster.broadcastPlayers(req.roomCode, gameRoomManagementService.getPlayers(req.roomCode))
    }

    @GetMapping("/settings")
    fun getGameSettings(@RequestParam roomCode: String): ResponseEntity<GameRoom> =
         ResponseEntity.ok(gameRoomManagementService.getGameRoomConfig(roomCode))

}