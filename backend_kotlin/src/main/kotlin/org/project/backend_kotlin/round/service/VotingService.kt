package org.project.backend_kotlin.round.service

import org.project.backend_kotlin.gameRoom.GameRoomRedisStore
import org.project.backend_kotlin.round.GameFlowFacade
import org.project.backend_kotlin.round.RoundBroadcaster
import org.project.backend_kotlin.round.RoundRedisStore
import org.project.backend_kotlin.round.dto.VoteDto
import org.springframework.stereotype.Service

@Service
class VotingService(
    private val roundRedisStore: RoundRedisStore,
    private val roundBroadcaster: RoundBroadcaster,
    private val gameRoomRedisStore: GameRoomRedisStore,
    private val gameFlowFacade: GameFlowFacade,
) {

    fun saveVote(roomCode: String, roundNumber: Int, voteDto: VoteDto) {
        if (doPlayerVoted(roomCode, voteDto.voterId, roundNumber)) return

        roundRedisStore.saveVote(roomCode, roundNumber, voteDto)
        val votes = roundRedisStore.getVotes(roomCode, roundNumber)
        roundBroadcaster.broadcastRoundVotes(roomCode, roundNumber, votes)

        val playerCount = gameRoomRedisStore.getPlayersFromRoom(roomCode).size
        if (votes.size >= playerCount) {
            gameFlowFacade.transitionToVotingResults(roomCode)
        }
    }

    fun getRoundVotes(roomCode: String, roundNumber: Int): List<VoteDto> {
        val votes = roundRedisStore.getVotes(roomCode, roundNumber)
        roundBroadcaster.broadcastRoundVotes(roomCode, roundNumber, votes)
        return votes
    }

    fun doPlayerVoted(roomCode: String, playerId: String, roundNumber: Int): Boolean =
        roundRedisStore.getVotes(roomCode, roundNumber).any { it.voterId == playerId }
}
