package org.project.backend_kotlin.round

import org.project.backend_kotlin.config.customException.ApiCustomException
import org.project.backend_kotlin.round.dto.RoundAnswer
import org.springframework.http.HttpStatus
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.coroutines.RestrictsSuspension

@RestController
@RequestMapping("api/round")
class RoundController(
    private val roundService: RoundService
) {


    @MessageMapping("/room/{roomCode}/round/player/{playerId}/word")
    fun startRound(@DestinationVariable roomCode: String, @DestinationVariable playerId: String) {
        println("start game")
        if (roomCode.isBlank()) throw ApiCustomException(HttpStatus.BAD_REQUEST, "Room code cannot be empty")

        roundService.startRound(roomCode)
    }


    @MessageMapping("/room/{roomCode}/round/{roundNumber}/player/{playerId}/answers")
    fun answering(
        @DestinationVariable roomCode: String,
        @DestinationVariable roundNumber: Int,
        @DestinationVariable playerId: String,
        @RequestBody answer: String
    ) {
        println(answer)
        roundService.saveAnswer(roomCode, roundNumber, answer, playerId)
    }





}