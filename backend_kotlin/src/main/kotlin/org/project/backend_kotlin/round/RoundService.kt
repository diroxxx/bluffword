package org.project.backend_kotlin.round

import org.project.backend_kotlin.gameRoom.GameRoomBroadcaster
import org.project.backend_kotlin.gameRoom.GameRoomRedisStore
import org.project.backend_kotlin.redisModels.CategorySelectionMode
import org.project.backend_kotlin.redisModels.GameRoom
import org.project.backend_kotlin.redisModels.GameRoomState
import org.project.backend_kotlin.redisModels.Player
import org.project.backend_kotlin.redisModels.WordPair
import org.project.backend_kotlin.round.dto.PlayerWordResponse
import org.project.backend_kotlin.round.dto.RoundAnswer
import org.project.backend_kotlin.round.dto.RoundVoteDto
import org.project.backend_kotlin.round.dto.VoteDto
import org.project.backend_kotlin.wordPair.WordPairRepository
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Service
class RoundService(
    private val roundRedisStore: RoundRedisStore,
    private val roundTimer: RoundTimerService,
    private val gameRoomRedisStore: GameRoomRedisStore,
    private val gameRoomBroadcaster: GameRoomBroadcaster,
    private val roundBroadcaster: RoundBroadcaster,
    private val sendScheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    private val wordPairRepository: WordPairRepository

) {

    private fun getCategorySelectionMode(roomCode: String): CategorySelectionMode {
        val raw = gameRoomRedisStore.getSpecificOption(roomCode, "categorySelectionMode")
            ?: throw IllegalStateException("Missing option 'categorySelectionMode' for roomCode=$roomCode")

        return when (raw) {
            is CategorySelectionMode -> raw
            is String -> {
                val normalized = raw.trim()
                try {
                    enumValueOf<CategorySelectionMode>(normalized.uppercase())
                } catch (e: IllegalArgumentException) {
                    throw IllegalStateException(
                        "Invalid option 'categorySelectionMode'='$raw' for roomCode=$roomCode",
                        e
                    )
                }
            }
            else -> throw IllegalStateException(
                "Option 'categorySelectionMode' has unexpected type ${raw::class.qualifiedName} for roomCode=$roomCode"
            )
        }
    }


    fun getUnUsedWordPairByCategory(roomCode: String, wordCategory: String?): WordPair? {

        val categoryMode = getCategorySelectionMode(roomCode)

        val randomWordPair = when (categoryMode) {
            CategorySelectionMode.FIXED -> {
                val staticCategory = gameRoomRedisStore.getSpecificOption(roomCode, "staticCategory") as String
                wordPairRepository.getRandomWordPair(staticCategory)
            }
            else -> {
                val usedIds = roundRedisStore.listOfWordPairs(roomCode)
                    .map { it.id }
                wordPairRepository.getRandomUnusedWordPair(wordCategory!!, usedIds)
            }
        }

        return randomWordPair?.let {
            WordPair(
                id = randomWordPair.id,
                impostorWord = randomWordPair.impostorWord,
                realWord = randomWordPair.realWord
            )
        }
    }

    private fun getCategory(roomCode: String, roundNumber: Int?): String {
        if (roundNumber != null) {
            return roundRedisStore.getRoundCategory(roomCode, roundNumber)
        }
        return gameRoomRedisStore.getSpecificOption(roomCode, "staticCategory") as String
    }

    private fun getListOfRandomIndexForImpostors(numberOfImpostors: Int, players: List<Player>): List<String> {

        roundRedisStore.saveImpostorIds(players[0].roomCode, 1, players.map { it.id })

        return players.indices.shuffled().take(numberOfImpostors).map { players[it].id }.toList()
    }

    fun startRound(roomCode: String) {
        val gameRoomConfig = gameRoomRedisStore.getGameRoomConfig(roomCode)

        if (gameRoomConfig.categorySelectionMode != CategorySelectionMode.FIXED) {
            return
        }

        gameRoomRedisStore.updateSpecificOption(roomCode, "state", GameRoomState.ANSWERING)

        val currentRound = gameRoomRedisStore.getIntOption(roomCode, "currentRound") + 1
        gameRoomRedisStore.updateSpecificOption(roomCode, "currentRound", currentRound)

        val wordCategory = when (gameRoomConfig.categorySelectionMode) {
            CategorySelectionMode.FIXED -> getCategory(roomCode, null)
            else -> null

        }
        println("wordCategory: $wordCategory")

        val wordPair = getUnUsedWordPairByCategory(roomCode, wordCategory) ?: return

        println("wordPair: $wordPair")

        val players = gameRoomRedisStore.getPlayersFromRoom(roomCode)
        println("players: $players")

        val numberOfImpostors = gameRoomConfig.numberOfImpostors
        val impostorIds = getListOfRandomIndexForImpostors(numberOfImpostors, players)
        println("impostorIds: $impostorIds")

        roundRedisStore.saveWordPair(roomCode, currentRound, wordPair)

        gameRoomRedisStore.updateSpecificOption(roomCode, "state", GameRoomState.ANSWERING)
        gameRoomBroadcaster.broadcastGameRoomState(roomCode, GameRoomState.ANSWERING)

        scheduleRoundWordBroadcast(roomCode, wordPair, players, impostorIds, currentRound)
    }

    private fun scheduleRoundWordBroadcast(
        roomCode: String,
        wordPair: WordPair,
        players: List<Player>,
        impostorIds: List<String>,
        currentRound: Int
    ) {
        println("scheduleRoundWordBroadcast: $roomCode, $wordPair, $players, $impostorIds, $currentRound")

        sendScheduler.schedule({
            try {
                println("SCHEDULER EXECUTED - przed broadcastem")
                broadcastWordsToPlayers(roomCode, wordPair, players, impostorIds, currentRound)
                startRoundTimerIfActive(roomCode)
                println("SCHEDULER EXECUTED - po broadcastie")
            } catch (e: Exception) {
                println("ERROR in scheduler: ${e.message}")
                e.printStackTrace()
            }
        }, 900, TimeUnit.MILLISECONDS)
    }

    private fun broadcastWordsToPlayers(
        roomCode: String,
        wordPair: WordPair,
        players: List<Player>,
        impostorIds: List<String>,
        currentRound: Int
    ) {
        val impostorWord = PlayerWordResponse(wordPair.impostorWord, true, currentRound)
        val realWord = PlayerWordResponse(wordPair.realWord, false, currentRound)

        players.forEach { player ->
            try {
                println("Wysyłam do gracza: ${player.id}")
                val isImpostor = impostorIds.contains(player.id)
                val response = if (isImpostor) impostorWord else realWord
                roundBroadcaster.broadcastRoundWord(roomCode, response, player.id)
                println("Wysłano do gracza: ${player.id}")
            } catch (e: Exception) {
                println("Błąd wysyłania do ${player.id}: ${e.message}")
                e.printStackTrace()
            }
        }
        println("BROADCAST END")
    }

    private fun startRoundTimerIfActive(roomCode: String) {
        val gameRoom = gameRoomRedisStore.getGameRoomConfig(roomCode)
        if (gameRoom.state == GameRoomState.ANSWERING) {
            println("start timer: $roomCode")
            roundTimer.startRoundTimer(roomCode, gameRoom.timeLimitAnswer)
        }
    }

    fun saveAnswer(roomCode: String,roundNumber: Int, answer: String, playerId: String) {

        val gameRoomConfig = gameRoomRedisStore.getGameRoomConfig(roomCode)
        println("state check ${gameRoomConfig.state}")
        if (gameRoomConfig.state != GameRoomState.ANSWERING) return

        val currentRoundFromRedis = gameRoomRedisStore.getIntOption(roomCode, "currentRound")
        println("answers check $roundNumber == $currentRoundFromRedis")
        if (roundNumber != currentRoundFromRedis){
            println("wrong round number")
            return
        }

        roundRedisStore.saveAnswer(roomCode, roundNumber,answer, playerId)

        checkIfEveryoneAnswered(roomCode, roundNumber)

    }

    fun checkIfEveryoneAnswered(roomCode: String, roundNumber: Int) {

        val checkIfEveryoneAnswered = roundRedisStore.checkIfEveryoneAnswered(roomCode, roundNumber)
        println("is everyone answered : $checkIfEveryoneAnswered")

        if (checkIfEveryoneAnswered) {
            println("change state to RESULTS")
            roundTimer.cancelTimer(roomCode)

            gameRoomRedisStore.updateSpecificOption(roomCode, "state", GameRoomState.RESULTS)

            val stateAfterUpdate = gameRoomRedisStore.getSpecificOption(roomCode, "state")
            println("State verification after update: $stateAfterUpdate")

            gameRoomBroadcaster.broadcastGameRoomState(roomCode, GameRoomState.RESULTS)
            roundBroadcaster.broadcastRoundAnswers(roomCode, roundNumber,getRoundAnswers(roomCode,roundNumber))
        }

    }

    fun getRoundAnswers(roomCode: String, roundNumber: Int): List<RoundAnswer> {
        return roundRedisStore.getAnswers(roomCode, roundNumber)
    }

    //votes

    fun saveVote(roomCode: String, roundNumber: Int, voteDto: VoteDto) {
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




}