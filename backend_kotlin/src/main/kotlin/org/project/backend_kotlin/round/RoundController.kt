package org.project.backend_kotlin.round

import org.project.backend_kotlin.config.customException.ApiCustomException
import org.project.backend_kotlin.round.dto.VoteDto
import org.project.backend_kotlin.round.service.AnsweringService
import org.project.backend_kotlin.round.service.RoundService
import org.project.backend_kotlin.round.service.VotingService
import org.springframework.http.HttpStatus
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/round")
class RoundController(
    private val roundService: RoundService,
    private val answeringService: AnsweringService,
    private val votingService: VotingService,
) {


    @MessageMapping("/room/{roomCode}/round/player/{playerId}/word")
    fun startRound(@DestinationVariable roomCode: String, @DestinationVariable playerId: String) {
        println("start game")
        if (roomCode.isBlank()) throw ApiCustomException(HttpStatus.BAD_REQUEST, "Room code cannot be empty")

        roundService.startRound(roomCode)
    }

    @MessageMapping("/room/{roomCode}/round/{roundNumber}/player/{playerId}/answers")
    fun saveAnswer(
        @DestinationVariable roomCode: String,
        @DestinationVariable roundNumber: Int,
        @DestinationVariable playerId: String,
        @RequestBody answer: String
    ) {
        println(answer)
        answeringService.saveAnswer(roomCode, roundNumber, answer, playerId)
    }

    @MessageMapping("/room/{roomCode}/round/{roundNumber}/player/vote")
    fun saveVote(@DestinationVariable roomCode: String, @DestinationVariable roundNumber: Int, @RequestBody voteDto: VoteDto) {

        if (voteDto.targetId.isBlank()) throw ApiCustomException(HttpStatus.BAD_REQUEST, "Target id cannot be empty")
        if (voteDto.voterId.isBlank()) throw ApiCustomException(HttpStatus.BAD_REQUEST, "Voter id cannot be empty")
        println("debug-- playerId : ${voteDto.voterId} targetId : ${voteDto.targetId}")

        votingService.saveVote(roomCode, roundNumber, voteDto)


    }
}