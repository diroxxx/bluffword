package org.project.backend_kotlin.round

import org.project.backend_kotlin.gameRoom.GameRoomRedisStore
import org.project.backend_kotlin.redisModels.CategorySelectionMode
import org.project.backend_kotlin.redisModels.WordPair
import org.project.backend_kotlin.round.dto.RoundAnswer
import org.project.backend_kotlin.round.dto.RoundVoteDto
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
import kotlin.text.get

@Service
class RoundRedisStore(

    private val redisTemplate: RedisTemplate<String, Any>,
    private val gameRoomRedisStore: GameRoomRedisStore,
    private val objectMapper: ObjectMapper
) {

    private fun roomRoundConfigKey(roomCode: String, roundNumber: Int): String {
        return "game_room:$roomCode:round:$roundNumber:config"
    }

    fun addRoundCategory(roomCode: String, roundNumber: Int, category: String) {
        redisTemplate.opsForHash<String, String>()
            .put(roomRoundConfigKey(roomCode, roundNumber), "category", category)
    }

    fun getRoundCategory(roomCode: String, roundNumber: Int): String {

        return redisTemplate.opsForHash<String, String>()
            .get(roomRoundConfigKey(roomCode, roundNumber), "category")
            ?: throw IllegalStateException("Category not found for room: $roomCode, round: $roundNumber")
    }



    fun saveImpostorIds(roomCode: String, roundNumber: Int, impostorIds: List<String>) {
        redisTemplate.opsForHash<String, List<String>>()
            .put(roomRoundConfigKey(roomCode, roundNumber), "impostorIds", impostorIds)
    }

    fun getImpostorIds(roomCode: String, roundNumber: Int): List<String> {
        return redisTemplate.opsForHash<String, List<String>>()
            .get(roomRoundConfigKey(roomCode, roundNumber), "impostorIds") ?: emptyList()
    }

    private fun roomRoundWordPairsKey(roomCode: String, roundNumber: Int): String {
        return "game_room:$roomCode:round:$roundNumber:config"
    }

    //word pair
    fun saveWordPair(roomCode: String, roundNumber: Int, wordPair: WordPair) {
        redisTemplate.opsForHash<String, WordPair>()
            .put(roomRoundWordPairsKey(roomCode, roundNumber), "wordPair", wordPair)
    }

    fun getWordPair(roomCode: String, roundNumber: Int): WordPair? {
        return redisTemplate.opsForHash<String, WordPair>()
            .get(roomRoundWordPairsKey(roomCode, roundNumber), "wordPair")
    }

    fun listOfWordPairs(roomCode: String): List<WordPair> {

        val currentRound = gameRoomRedisStore.getSpecificOption(roomCode,"currentRound") as Int
        val wordPairs = mutableListOf<WordPair>()
        for (roundNumber in 1..currentRound) {
            wordPairs.addAll(
                redisTemplate.opsForHash<String, WordPair>()
                    .entries(roomRoundWordPairsKey(roomCode, roundNumber)).values.toList()
            )
        }

        return wordPairs.ifEmpty { emptyList() }
    }


    //answers
    private fun roomRoundAnswersKey(roomCode: String, roundNumber: Int): String {
        return "game_room:$roomCode:round:$roundNumber:answers"
    }

    fun getAnswers(roomCode: String, roundNumber: Int): List<RoundAnswer> {
        val key = roomRoundAnswersKey(roomCode, roundNumber)

        val rawList = redisTemplate.opsForList()
            .range(key, 0, -1) ?: emptyList()

        return rawList.mapNotNull {
            if (it is String) {
                try {
                    objectMapper.readValue(it, RoundAnswer::class.java)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }
    }

    fun saveAnswer(roomCode: String, roundNumber: Int, answer: String, playerId: String) {

        val key = roomRoundAnswersKey(roomCode, roundNumber)

        val roundAnswerDto = RoundAnswer(answer, playerId)
        val json = objectMapper.writeValueAsString(roundAnswerDto)

        redisTemplate.opsForList()
            .rightPush(key, json)
    }

    fun checkIfEveryoneAnswered(roomCode: String, roundNumber: Int): Boolean {
        val answersLength = getAnswers(roomCode, roundNumber).size
        val playersLength = gameRoomRedisStore.getPlayersFromRoom(roomCode).size
        println("checkIfEveryoneAnswered: $answersLength == $playersLength")
        return answersLength == playersLength
    }



    //votes
    private fun roomRoundVotesKey(roomCode: String, roundNumber: Int): String {
        return "game_room:$roomCode:round:$roundNumber:votes"
    }

    fun getVotes(roomCode: String, roundNumber: Int): List<RoundVoteDto> {
        val key = roomRoundVotesKey(roomCode, roundNumber)
        val rawList = redisTemplate.opsForList()
            .range(key, 0, -1) ?: emptyList()

        return rawList.mapNotNull {
            when (it) {
                is String -> objectMapper.readValue(it, RoundVoteDto::class.java)
                else -> null
            }
        }
    }

    fun saveVote(roomCode: String, roundNumber: Int) {
        val key = roomRoundVotesKey(roomCode, roundNumber)

        redisTemplate.opsForList()
            .rightPush(key, RoundVoteDto("test", "test"))
    }

}