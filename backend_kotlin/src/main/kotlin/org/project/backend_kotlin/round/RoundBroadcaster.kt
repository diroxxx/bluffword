package org.project.backend_kotlin.round

import org.project.backend_kotlin.round.dto.PlayerWordResponse
import org.project.backend_kotlin.round.dto.RoundAnswer
import org.project.backend_kotlin.round.dto.RoundAnswerDto
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
}