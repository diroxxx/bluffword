package org.project.backend_kotlin.round.service

import org.project.backend_kotlin.gameRoom.GameRoomRedisStore
import org.project.backend_kotlin.round.RoundBroadcaster
import org.project.backend_kotlin.round.RoundRedisStore
import org.project.backend_kotlin.round.dto.VoteDto
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

@Service
class VotingService(
    private val roundRedisStore: RoundRedisStore,
    private val roundBroadcaster: RoundBroadcaster,
    private val roundTimer: RoundTimerService,
    private val gameRoomRedisStore: GameRoomRedisStore,
    private val voteTimeScheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    ) {

    fun saveVote(roomCode: String, roundNumber: Int, voteDto: VoteDto) {

        if(doPlayerVoted(roomCode, voteDto.voterId, roundNumber)) return

        roundRedisStore.saveVote(roomCode, roundNumber, voteDto)
        println("Vote saved: $voteDto")

        val votes = roundRedisStore.getVotes(roomCode, roundNumber)
        println("Current votes count: ${votes.size}")

        roundBroadcaster.broadcastRoundVotes(roomCode, roundNumber, votes)

    }

    fun getRoundVotes(roomCode: String, roundNumber: Int): List<VoteDto> {
        val votes = roundRedisStore.getVotes(roomCode, roundNumber)
        roundBroadcaster.broadcastRoundVotes(roomCode, roundNumber,votes)
        return votes
    }

    fun doPlayerVoted(roomCode: String, playerId: String, roundNumber: Int): Boolean {
        val votes = roundRedisStore.getVotes(roomCode, roundNumber)
        return votes.any { it.voterId == playerId }
    }



}