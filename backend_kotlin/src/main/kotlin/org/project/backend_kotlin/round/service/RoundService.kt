package org.project.backend_kotlin.round.service

import jakarta.annotation.PreDestroy
import org.project.backend_kotlin.gameRoom.GameRoomRedisStore
import org.project.backend_kotlin.redisModels.Player
import org.project.backend_kotlin.redisModels.WordPair
import org.project.backend_kotlin.round.RoundBroadcaster
import org.project.backend_kotlin.round.redisService.RoundRedisStore
import org.project.backend_kotlin.round.dto.PlayerWordResponse
import org.project.backend_kotlin.round.gameStrategy.GameModeStrategyFactory
import org.project.backend_kotlin.wordPair.WordPairRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Service
class RoundService(
    private val roundRedisStore: RoundRedisStore,
    private val gameRoomRedisStore: GameRoomRedisStore,
    private val roundBroadcaster: RoundBroadcaster,
    private val wordPairRepository: WordPairRepository,
    private val strategyFactory: GameModeStrategyFactory,
    private val sendScheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
) {

    fun scheduleWordBroadcast(roomCode: String, currentRound: Int, category: String, onDone: () -> Unit) {
        val config = gameRoomRedisStore.getGameRoomConfig(roomCode)
        val wordPair = getWordPair(roomCode, category) ?: return
        val players = gameRoomRedisStore.getPlayersFromRoom(roomCode)
        val impostorIds = strategyFactory.get(config.gameMode).assignImpostors(roomCode, currentRound, players)

        roundRedisStore.saveWordPair(roomCode, currentRound, wordPair)

        sendScheduler.schedule({
            try {
                broadcastWordsToPlayers(roomCode, wordPair, players, impostorIds, currentRound)
                onDone()
            } catch (e: Exception) {
                println("ERROR in scheduler: ${e.message}")
                e.printStackTrace()
            }
        }, 900, TimeUnit.MILLISECONDS)
    }

    private fun getWordPair(roomCode: String, category: String): WordPair? {
        val usedIds = roundRedisStore.listOfWordPairs(roomCode).map { it.id }
        val limitOne = PageRequest.of(0, 1)

        val wordPairDb = if (usedIds.isEmpty()) {
            wordPairRepository.findRandomWordPairs(category, limitOne).firstOrNull()
        } else {
            wordPairRepository.findRandomUnusedWordPairs(category, usedIds, limitOne).firstOrNull()
                ?: wordPairRepository.findRandomWordPairs(category,limitOne).firstOrNull()
        }
        return wordPairDb?.let { WordPair(it.id, it.impostorWord, it.realWord) }
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
                val response = if (impostorIds.contains(player.id)) impostorWord else realWord
                roundBroadcaster.broadcastRoundWord(roomCode, response, player.id)
            } catch (e: Exception) {
                println("Błąd wysyłania do ${player.id}: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    @PreDestroy
    fun shutdownScheduler() {
        sendScheduler.shutdownNow()
    }
}
