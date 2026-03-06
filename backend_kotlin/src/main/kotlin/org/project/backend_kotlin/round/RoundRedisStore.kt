package org.project.backend_kotlin.round

import org.project.backend_kotlin.gameRoom.GameRoomRedisStore
import org.project.backend_kotlin.redisModels.WordPair
import org.project.backend_kotlin.round.dto.RoundAnswer
import org.project.backend_kotlin.round.dto.RoundVoteDto
import org.project.backend_kotlin.round.dto.VoteDto
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
        val json = objectMapper.writeValueAsString(impostorIds)
        redisTemplate.opsForHash<String, String>()
            .put(roomRoundConfigKey(roomCode, roundNumber), "impostorIds", json)
    }

    fun getImpostorIds(roomCode: String, roundNumber: Int): List<String> {
        val raw = redisTemplate.opsForHash<String, String>()
            .get(roomRoundConfigKey(roomCode, roundNumber), "impostorIds") ?: return emptyList()
        return objectMapper.readValue(raw, objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java))
    }

    private fun roomRoundWordPairsKey(roomCode: String, roundNumber: Int): String {
        return "game_room:$roomCode:round:$roundNumber:config"
    }

    //word pair
    fun saveWordPair(roomCode: String, roundNumber: Int, wordPair: WordPair) {
        val json = objectMapper.writeValueAsString(wordPair)
        redisTemplate.opsForHash<String, String>()
            .put(roomRoundWordPairsKey(roomCode, roundNumber), "wordPair", json)
    }

    fun getWordPair(roomCode: String, roundNumber: Int): WordPair? {
        val raw = redisTemplate.opsForHash<String, String>()
            .get(roomRoundWordPairsKey(roomCode, roundNumber), "wordPair") ?: return null
        return objectMapper.readValue(raw, WordPair::class.java)
    }

    fun listOfWordPairs(roomCode: String): List<WordPair> {
        val currentRound = gameRoomRedisStore.getSpecificOption(roomCode, "currentRound") as Int
        return (1..currentRound).mapNotNull { roundNumber -> getWordPair(roomCode, roundNumber) }
    }


    fun saveChooserPlayerId(roomCode: String, roundNumber: Int, playerId: String) {
        redisTemplate.opsForHash<String, String>()
            .put(roomRoundConfigKey(roomCode, roundNumber), "chooserPlayerId", playerId)
    }

    fun getChooserPlayerId(roomCode: String, roundNumber: Int): String? {
        return redisTemplate.opsForHash<String, String>()
            .get(roomRoundConfigKey(roomCode, roundNumber), "chooserPlayerId")
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

    fun getVotes(roomCode: String, roundNumber: Int): List<VoteDto> {
        val key = roomRoundVotesKey(roomCode, roundNumber)
        val rawList = redisTemplate.opsForList()
            .range(key, 0, -1) ?: emptyList()

        return rawList.mapNotNull {
            when (it) {
                is String -> objectMapper.readValue(it, VoteDto::class.java)
                else -> null
            }
        }
    }

    fun saveVote(roomCode: String, roundNumber: Int, voteDto: VoteDto) {
        val key = roomRoundVotesKey(roomCode, roundNumber)
        val json = objectMapper.writeValueAsString(VoteDto(targetId = voteDto.targetId, voterId = voteDto.voterId))


        redisTemplate.opsForList()
            .rightPush(key, json)
    }

}