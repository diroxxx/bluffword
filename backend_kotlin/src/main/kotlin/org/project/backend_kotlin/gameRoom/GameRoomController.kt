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
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@RestController
@RequestMapping("/api/gameRoom")
class GameRoomController (
    private val gameRoomService: GameRoomService,
    private val gameRoomBroadcaster: GameRoomBroadcaster,

) {


    @PostMapping("/create")
    fun createGameRoom(@RequestBody @Valid createRoomDto: CreateRoomRequestDto) : ResponseEntity<Player> {

        val createdPlayer = gameRoomService.createGameRoom(createRoomDto.nickname, createRoomDto.settings)
        println(createdPlayer)
        return ResponseEntity.ok(createdPlayer)

    }

    @PostMapping("/join")
    fun joinGameRoom(@RequestBody @Valid joinRoomRequestDto: JoinRoomRequestDto) : ResponseEntity<Player> {

        return ResponseEntity.ok(gameRoomService.joinGameRoom(joinRoomRequestDto.roomCode, joinRoomRequestDto.roomCode))
    }

    @MessageMapping("/room/{roomCode}/players")
    fun getPlayersFromRoom(@DestinationVariable roomCode: String) {
        println(gameRoomService.getPlayers(roomCode))

        gameRoomBroadcaster.broadcastPlayers(roomCode, gameRoomService.getPlayers(roomCode))
    }

    @MessageMapping("/room/{roomCode}/state")
    fun getRoomState(@NotEmpty @DestinationVariable roomCode: String) =
        gameRoomBroadcaster.broadcastGameRoomState(roomCode, gameRoomService.getRoomState(roomCode))


    @DeleteMapping("/player")
    fun deletePlayerFromRoom(@RequestBody req: JoinRoomRequestDto) {
        gameRoomService.deletePlayerFromRoom(req.roomCode, req.nickname)
        gameRoomBroadcaster.broadcastPlayers(req.roomCode, gameRoomService.getPlayers(req.roomCode))
    }

    @GetMapping("/settings")
    fun getGameSettings(@RequestParam roomCode: String): ResponseEntity<GameRoom> =
         ResponseEntity.ok(gameRoomService.getGameRoomConfig(roomCode))

}