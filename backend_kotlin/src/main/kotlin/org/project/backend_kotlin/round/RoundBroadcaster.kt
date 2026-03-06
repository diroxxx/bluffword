package org.project.backend_kotlin.round

import org.project.backend_kotlin.round.dto.PlayerWordResponse
import org.project.backend_kotlin.round.dto.RoundAnswer
import org.project.backend_kotlin.round.dto.RoundAnswerDto
import org.project.backend_kotlin.round.dto.RoundVoteDto
import org.project.backend_kotlin.round.dto.VoteDto
import org.project.backend_kotlin.round.dto.VotingResultDto
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.SimpMessagingTemplate

@Configuration
class RoundBroadcaster(
    private val messagingTemplate: SimpMessagingTemplate
) {

    fun broadcastRoundWord(roomCode: String, response: PlayerWordResponse, playerId: String) {
        println("sending $response to $playerId")
        messagingTemplate.convertAndSend("/topic/room/$roomCode/player/$playerId/round/word", response)
    }

    fun broadcastRoundAnswers(roomCode: String?, roundNumber: Int, answers: List<RoundAnswer>) {
        println("sending answers to $roomCode")
        messagingTemplate.convertAndSend("/topic/room/$roomCode/round/$roundNumber/answers", answers)
    }

    fun broadcastRoundVotes(roomCode: String, roundNumber: Int, votes: List<VoteDto>) {
        println("sending vote to $roomCode list of ${votes.size}")
        messagingTemplate.convertAndSend("/topic/room/$roomCode/round/$roundNumber/votes", votes)
    }

    fun broadcastRoundCategory(roomCode: String, roundNumber: Int, category: String) {
        messagingTemplate.convertAndSend("/topic/room/$roomCode/round/$roundNumber/category", category)
    }

    fun broadcastCategoryChoices(roomCode: String, playerId: String, categories: List<String>) {
        messagingTemplate.convertAndSend("/topic/room/$roomCode/player/$playerId/category/choices", categories)
    }

    fun broadcastVotingResult(roomCode: String, roundNumber: Int, result: VotingResultDto) {
        messagingTemplate.convertAndSend("/topic/room/$roomCode/round/$roundNumber/result", result)
    }
}