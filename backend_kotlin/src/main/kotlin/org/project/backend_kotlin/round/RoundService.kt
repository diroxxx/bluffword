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
import org.project.backend_kotlin.wordPair.WordPairRepository
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadLocalRandom
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
        val impostorIndex = ThreadLocalRandom.current().nextInt(players.size)



        roundRedisStore.saveWordPair(roomCode, currentRound, wordPair)

        gameRoomRedisStore.updateSpecificOption(roomCode, "state", GameRoomState.ANSWERING)
        gameRoomBroadcaster.broadcastGameRoomState(roomCode, GameRoomState.ANSWERING)


        scheduleRoundWordBroadcast(roomCode, wordPair, players, impostorIndex, currentRound)
    }

    private fun scheduleRoundWordBroadcast(
        roomCode: String,
        wordPair: WordPair,
        players: List<Player>,
        impostorIndex: Int,
        currentRound: Int
    ) {
        println("scheduleRoundWordBroadcast: $roomCode, $wordPair, $players, $impostorIndex, $currentRound")

        sendScheduler.schedule({
            try {
                println("SCHEDULER EXECUTED - przed broadcastem")
                broadcastWordsToPlayers(roomCode, wordPair, players, impostorIndex, currentRound)
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
        impostorIndex: Int,
        currentRound: Int
    ) {
        val impostorWord = PlayerWordResponse(wordPair.impostorWord, true, currentRound)
        val realWord = PlayerWordResponse(wordPair.realWord, false, currentRound)

        players.forEachIndexed { index, player ->
            try {
                println("Wysyłam do gracza: ${player.id}, index: $index")
                val isImpostor = index == impostorIndex
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
            println("startRoundTimerIfActive: $roomCode")
            roundTimer.startRoundTimer(roomCode, gameRoom.timeLimitAnswer)
        }
    }


    fun saveAnswer(roomCode: String,roundNumber: Int, answer: String, playerId: String) {

        val gameRoomConfig = gameRoomRedisStore.getGameRoomConfig(roomCode)
        println("check1 ${GameRoomState.ANSWERING}")
        if (gameRoomConfig.state != GameRoomState.ANSWERING) return

        val currentRoundFromRedis = gameRoomRedisStore.getIntOption(roomCode, "currentRound")
        println("check2 $roundNumber == $currentRoundFromRedis")
        if (roundNumber != currentRoundFromRedis) return

        roundRedisStore.saveAnswer(roomCode, roundNumber,answer, playerId)

        checkIfEveryoneAnswered(roomCode, roundNumber)

    }


    fun checkIfEveryoneAnswered(roomCode: String, roundNumber: Int) {


        val checkIfEveryoneAnswered = roundRedisStore.checkIfEveryoneAnswered(roomCode, roundNumber)
        println("checkIfEveryoneAnswered: $checkIfEveryoneAnswered")
        if (checkIfEveryoneAnswered) {
            //stop the timer

            gameRoomRedisStore.updateSpecificOption(roomCode, "state", GameRoomState.RESULTS)
            gameRoomBroadcaster.broadcastGameRoomState(roomCode, GameRoomState.RESULTS)
            roundBroadcaster.broadcastRoundAnswers(roomCode, roundNumber,getRoundAnswers(roomCode,roundNumber))
        }

    }

    fun getRoundAnswers(roomCode: String, roundNumber: Int): List<RoundAnswer> {
        return roundRedisStore.getAnswers(roomCode, roundNumber)
    }


}