package org.project.backend_kotlin.round

import org.project.backend_kotlin.gameRoom.GameRoomRedisStore
import org.project.backend_kotlin.gameRoom.GameStateTransitionService
import org.project.backend_kotlin.gameRoom.TimerExpiryHandler
import org.project.backend_kotlin.redisModels.CategorySelectionMode
import org.project.backend_kotlin.redisModels.GameRoomState
import org.project.backend_kotlin.round.dto.TimerType
import org.project.backend_kotlin.round.dto.VoteDto
import org.project.backend_kotlin.round.dto.VotingResultDto
import org.project.backend_kotlin.round.service.RoundService
import org.project.backend_kotlin.round.service.RoundTimerService
import org.project.backend_kotlin.wordPair.CategoryRepository
import org.springframework.stereotype.Component

private const val NEXT_ROUND_DELAY_SECONDS = 10

@Component
class GameFlowFacade(
    private val stateTransition: GameStateTransitionService,
    private val roundTimer: RoundTimerService,
    private val roundRedisStore: RoundRedisStore,
    private val roundBroadcaster: RoundBroadcaster,
    private val gameRoomRedisStore: GameRoomRedisStore,
    private val roundService: RoundService,
    private val categoryRepository: CategoryRepository,
) : TimerExpiryHandler {

    // ── Round start ──────────────────────────────────────────────────────────

    fun startRound(roomCode: String) {
        val config = gameRoomRedisStore.getGameRoomConfig(roomCode)
        val currentRound = gameRoomRedisStore.getIntOption(roomCode, "currentRound") + 1
        gameRoomRedisStore.updateSpecificOption(roomCode, "currentRound", currentRound)

        when (config.categorySelectionMode) {
            CategorySelectionMode.FIXED -> {
                val category = config.staticCategory ?: return
                beginAnsweringWithCategory(roomCode, currentRound, category)
            }
            CategorySelectionMode.RANDOM_PER_ROUND -> {
                val category = categoryRepository.findAll().randomOrNull()?.name ?: return
                roundRedisStore.addRoundCategory(roomCode, currentRound, category)
                roundBroadcaster.broadcastRoundCategory(roomCode, currentRound, category)
                beginAnsweringWithCategory(roomCode, currentRound, category)
            }
            CategorySelectionMode.PLAYER_CHOSEN_PER_ROUND -> {
                val players = gameRoomRedisStore.getPlayersFromRoom(roomCode)
                val chooser = players.randomOrNull() ?: return
                roundRedisStore.saveChooserPlayerId(roomCode, currentRound, chooser.id)
                val categories = categoryRepository.findAll().shuffled().take(3).map { it.name }
                stateTransition.setState(roomCode, GameRoomState.CATEGORY_SELECTION)
                roundBroadcaster.broadcastCategoryChoices(roomCode, chooser.id, categories)

            }

        }

    }

    fun selectCategory(roomCode: String, roundNumber: Int, playerId: String, category: String) {
        val config = gameRoomRedisStore.getGameRoomConfig(roomCode)
        if (config.state != GameRoomState.CATEGORY_SELECTION) return

        val chooserId = roundRedisStore.getChooserPlayerId(roomCode, roundNumber)
        if (chooserId != playerId) return

        roundRedisStore.addRoundCategory(roomCode, roundNumber, category)
        roundBroadcaster.broadcastRoundCategory(roomCode, roundNumber, category)
        beginAnsweringWithCategory(roomCode, roundNumber, category)
    }

    private fun beginAnsweringWithCategory(roomCode: String, currentRound: Int, category: String) {
        stateTransition.setState(roomCode, GameRoomState.ANSWERING)
        roundService.scheduleWordBroadcast(roomCode, currentRound, category) {
            startAnsweringTimer(roomCode)
        }
    }

    // ── Answering ────────────────────────────────────────────────────────────

    fun saveAnswer(roomCode: String, roundNumber: Int, answer: String, playerId: String) {
        val config = gameRoomRedisStore.getGameRoomConfig(roomCode)
        if (config.state != GameRoomState.ANSWERING) return
        if (roundNumber != gameRoomRedisStore.getIntOption(roomCode, "currentRound")) return

        roundRedisStore.saveAnswer(roomCode, roundNumber, answer, playerId)

        if (roundRedisStore.checkIfEveryoneAnswered(roomCode, roundNumber)) {
            transitionToVoting(roomCode)
        }
    }

    private fun startAnsweringTimer(roomCode: String) {
        val config = gameRoomRedisStore.getGameRoomConfig(roomCode)
        roundTimer.startRoundTimer(roomCode, config.timeLimitAnswer, TimerType.ANSWERING)
    }

    // ── Voting ───────────────────────────────────────────────────────────────

    fun saveVote(roomCode: String, roundNumber: Int, voteDto: VoteDto) {
        val votes = roundRedisStore.getVotes(roomCode, roundNumber)
        if (votes.any { it.voterId == voteDto.voterId }) return

        roundRedisStore.saveVote(roomCode, roundNumber, voteDto)
        val updatedVotes = roundRedisStore.getVotes(roomCode, roundNumber)
        roundBroadcaster.broadcastRoundVotes(roomCode, roundNumber, updatedVotes)

        val playerCount = gameRoomRedisStore.getPlayersFromRoom(roomCode).size
        if (updatedVotes.size >= playerCount) {
            transitionToVotingResults(roomCode)
        }
    }

    // ── State transitions ────────────────────────────────────────────────────

    fun transitionToVoting(roomCode: String) {
        val currentRound = gameRoomRedisStore.getIntOption(roomCode, "currentRound")
        roundTimer.cancelTimer(roomCode)
        stateTransition.setState(roomCode, GameRoomState.VOTING)
        val answers = roundRedisStore.getAnswers(roomCode, currentRound)
        roundBroadcaster.broadcastRoundAnswers(roomCode, currentRound, answers)
        val config = gameRoomRedisStore.getGameRoomConfig(roomCode)
        roundTimer.startRoundTimer(roomCode, config.timeLimitVote, TimerType.VOTING)
    }

    fun transitionToVotingResults(roomCode: String) {
        val config = gameRoomRedisStore.getGameRoomConfig(roomCode)
        if (config.state != GameRoomState.VOTING) return

        val currentRound = gameRoomRedisStore.getIntOption(roomCode, "currentRound")
        roundTimer.cancelTimer(roomCode)
        stateTransition.setState(roomCode, GameRoomState.VOTING_RESULTS)

        val result = buildVotingResult(roomCode, currentRound)
        roundBroadcaster.broadcastVotingResult(roomCode, currentRound, result)

        if (result.isGameOver) {
            roundTimer.startRoundTimer(roomCode, NEXT_ROUND_DELAY_SECONDS, TimerType.GAME_END)
        } else {
            roundTimer.startRoundTimer(roomCode, NEXT_ROUND_DELAY_SECONDS, TimerType.NEXT_ROUND)
        }
    }

    private fun buildVotingResult(roomCode: String, roundNumber: Int): VotingResultDto {
        val votes = roundRedisStore.getVotes(roomCode, roundNumber)
        val config = gameRoomRedisStore.getGameRoomConfig(roomCode)
        val isLastRound = roundNumber >= config.roundTotal

        if (votes.isEmpty()) {
            return VotingResultDto(nickname = null, voteCount = 0, isImpostor = false, isGameOver = isLastRound)
        }

        val players = gameRoomRedisStore.getPlayersFromRoom(roomCode)
        val impostorIds = roundRedisStore.getImpostorIds(roomCode, roundNumber)

        val topVotedId = votes
            .groupingBy { it.targetId }
            .eachCount()
            .maxByOrNull { it.value }?.key

        val player = players.find { it.id == topVotedId }
        val voteCount = if (topVotedId != null) votes.count { it.targetId == topVotedId } else 0
        val isImpostor = impostorIds.contains(topVotedId)

        val allImpostorsEliminated = isImpostor && impostorIds.size == 1
        val isGameOver = isLastRound || allImpostorsEliminated

        return VotingResultDto(
            nickname = player?.nickname,
            voteCount = voteCount,
            isImpostor = isImpostor,
            isGameOver = isGameOver,
        )
    }

    fun rebroadcastVotingResult(roomCode: String) {
        val currentRound = gameRoomRedisStore.getIntOption(roomCode, "currentRound")
        val result = buildVotingResult(roomCode, currentRound)
        roundBroadcaster.broadcastVotingResult(roomCode, currentRound, result)
    }

    // ── Timer expiry ─────────────────────────────────────────────────────────

    override fun onTimerExpired(roomCode: String, timerType: TimerType) {
        when (timerType) {
            TimerType.ANSWERING  -> transitionToVoting(roomCode)
            TimerType.VOTING     -> transitionToVotingResults(roomCode)
            TimerType.NEXT_ROUND -> startRound(roomCode)
            TimerType.GAME_END   -> stateTransition.setState(roomCode, GameRoomState.GAME_END)
        }
    }
}
