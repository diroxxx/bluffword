package org.project.backend_kotlin.round

import org.project.backend_kotlin.config.customException.ApiCustomException
import org.project.backend_kotlin.round.dto.CategorySelectionDto
import org.project.backend_kotlin.round.dto.VoteDto
import org.springframework.http.HttpStatus
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/round")
class RoundController(
    private val gameFlowFacade: GameFlowFacade,
) {

    @MessageMapping("/room/{roomCode}/round/player/{playerId}/word")
    fun startRound(@DestinationVariable roomCode: String) {
        if (roomCode.isBlank()) throw ApiCustomException(HttpStatus.BAD_REQUEST, "Room code cannot be empty")
        gameFlowFacade.startRound(roomCode)
    }

    @MessageMapping("/room/{roomCode}/round/{roundNumber}/player/{playerId}/answers")
    fun saveAnswer(
        @DestinationVariable roomCode: String,
        @DestinationVariable roundNumber: Int,
        @DestinationVariable playerId: String,
        @RequestBody answer: String
    ) {
        gameFlowFacade.saveAnswer(roomCode, roundNumber, answer, playerId)
    }

    @MessageMapping("/room/{roomCode}/round/{roundNumber}/player/vote")
    fun saveVote(
        @DestinationVariable roomCode: String,
        @DestinationVariable roundNumber: Int,
        @RequestBody voteDto: VoteDto
    ) {
        if (voteDto.targetId.isBlank()) throw ApiCustomException(HttpStatus.BAD_REQUEST, "Target id cannot be empty")
        if (voteDto.voterId.isBlank()) throw ApiCustomException(HttpStatus.BAD_REQUEST, "Voter id cannot be empty")
        gameFlowFacade.saveVote(roomCode, roundNumber, voteDto)
    }

    @MessageMapping("/room/{roomCode}/round/{roundNumber}/player/{playerId}/category")
    fun selectCategory(
        @DestinationVariable roomCode: String,
        @DestinationVariable roundNumber: Int,
        @DestinationVariable playerId: String,
        @RequestBody dto: CategorySelectionDto
    ) {
        if (dto.category.isBlank()) throw ApiCustomException(HttpStatus.BAD_REQUEST, "Category cannot be empty")
        gameFlowFacade.selectCategory(roomCode, roundNumber, playerId, dto.category)
    }

    @MessageMapping("/room/{roomCode}/round/{roundNumber}/voting-results")
    fun getVotingResult(
        @DestinationVariable roomCode: String,
        @DestinationVariable roundNumber: Int,
    ) {
        gameFlowFacade.rebroadcastVotingResult(roomCode)
    }


}
