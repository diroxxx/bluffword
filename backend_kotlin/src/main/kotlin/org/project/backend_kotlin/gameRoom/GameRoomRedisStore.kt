package org.project.backend_kotlin.gameRoom

import org.project.backend_kotlin.gameRoom.dto.GameSettingsRequestDto
import org.project.backend_kotlin.redisModels.CategorySelectionMode
import org.project.backend_kotlin.redisModels.GameMode
import org.project.backend_kotlin.redisModels.GameRoom
import org.project.backend_kotlin.redisModels.GameRoomState
import org.project.backend_kotlin.redisModels.Player
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import kotlin.text.get

@Service
class GameRoomRedisStore(

    private val redisTemplate: RedisTemplate<String, Any>
) {

    private fun roomConfigKey(roomCode: String): String {
        return "game_room:$roomCode:config"
    }
    /*
        options:
        roundTotal - total number of rounds in the game
        maxPlayers - maximum number of players allowed in the game
        minPlayers - minimum number of players required to start the game
        numberOfImpostors - number of impostors in the game
        timeLimitAnswer - time limit for players to submit their answers (in seconds)
        timeLimitVote - time limit for players to vote (in seconds)
        gameMode - the mode of the game (e.g., CLASSIC, CUSTOM)
        categorySelectionMode - how categories are selected (e.g., RANDOM, STATIC)
        staticCategory - if categorySelectionMode is STATIC, this field holds the category name
        hostId - the ID of the player who created the room

     */
    fun createRoom(roomCode: String, settings: GameSettingsRequestDto, hostId: String) {
        val cfg = mutableMapOf<String, Any?>(
            "roundTotal" to settings.roundTotal.toString(),
            "maxPlayers" to settings.maxPlayers.toString(),
            "currentRound" to 0,
            "numberOfImpostors" to settings.numberOfImpostors.toString(),
            "timeLimitAnswer" to settings.timeLimitAnswer.toString(),
            "timeLimitVote" to settings.timeLimitVote.toString(),
            "gameMode" to settings.gameMode.name,
            "categorySelectionMode" to settings.categorySelectionMode.name,
            "staticCategory" to settings.staticCategory,
            "hostId" to hostId,
            "state" to GameRoomState.LOBBY.name
        )
        redisTemplate.opsForHash<String, Any>().putAll(roomConfigKey(roomCode), cfg)

    }

    fun getGameRoomConfig(roomCode: String): GameRoom {
        val cfg = redisTemplate.opsForHash<String, Any>().entries(roomConfigKey(roomCode))

        return GameRoom(
            roundTotal = (cfg["roundTotal"] as? String)?.toIntOrNull() ?: 0,
            maxPlayers = (cfg["maxPlayers"] as? String)?.toIntOrNull() ?: 0,
            currentRound = (cfg["currentRound"] as? String)?.toIntOrNull() ?: 0,
            numberOfImpostors = (cfg["numberOfImpostors"] as? String)?.toIntOrNull() ?: 0,
            timeLimitAnswer = (cfg["timeLimitAnswer"] as? String)?.toIntOrNull() ?: 0,
            timeLimitVote = (cfg["timeLimitVote"] as? String)?.toIntOrNull() ?: 0,
            gameMode = runCatching { GameMode.valueOf(cfg["gameMode"]?.toString() ?: "") }.getOrNull() ?: GameMode.STATIC_IMPOSTOR,
            categorySelectionMode = runCatching { CategorySelectionMode.valueOf(cfg["categorySelectionMode"]?.toString() ?: "") }.getOrNull() ?: CategorySelectionMode.FIXED,
            staticCategory = cfg["staticCategory"] as? String,
            hostId = cfg["hostId"]?.toString() ?: "",
            state = runCatching { GameRoomState.valueOf(cfg["state"]?.toString() ?: "") }.getOrNull() ?: GameRoomState.LOBBY,
            code = roomCode
        )
    }

    fun getSpecificOption(roomCode: String, option: String): Any? =
        redisTemplate.opsForHash<String, Any>().get(roomConfigKey(roomCode), option)

    fun updateSpecificOption(roomCode: String, option: String, value: Any) {
        redisTemplate.opsForHash<String, Any>().put(roomConfigKey(roomCode), option, value)
    }

    fun getIntOption(roomCode: String, option: String): Int {
        val value = getSpecificOption(roomCode, option)
        return when (value) {
            is Int -> value
            is String -> value.toIntOrNull() ?: 0
            else -> 0
        }
    }




    // gameRoom player redis section

    private fun roomPlayersKey(roomCode: String): String {
        return "game_room:$roomCode:players"
    }

    fun addPlayerToRoom(roomCode: String, player: Player) =
        redisTemplate.opsForHash<String, Player>()
            .put(roomPlayersKey(roomCode), player.id, player)

    fun getPlayerFromRoom(roomCode: String, playerId: String): Player? =
        redisTemplate.opsForHash<String, Player>()
            .get(roomPlayersKey(roomCode), playerId)

//    fun getPlayersFromRoom(roomCode: String): List<Player> =
//        redisTemplate.opsForHash<String, Player>().entries(roomPlayersKey(roomCode)).values.toList()

    fun getPlayersFromRoom(roomCode: String): List<Player> {
        return redisTemplate.opsForHash<String, Any>()
            .entries(roomPlayersKey(roomCode))
            .values
            .map { value ->
                when (value) {
                    is Player -> value
                    is LinkedHashMap<*, *> -> Player(
                        id = value["id"]?.toString() ?: "",
                        nickname = value["nickname"]?.toString() ?: "",
                        roomCode = value["roomCode"]?.toString() ?: "",
                        isHost = value["isHost"] as? Boolean ?: false
                    )
                    else -> throw IllegalStateException("Unexpected type: ${value::class}")
                }
            }
    }




    fun playerExists(roomCode: String, playerId: String): Boolean {
        val key = roomPlayersKey(roomCode)
        val ops = redisTemplate.opsForHash<String, Player>()
        return ops.hasKey(key, playerId)
    }

    fun removePlayer(roomCode: String, playerId: String): Boolean {
        val key = roomPlayersKey(roomCode)
        val ops = redisTemplate.opsForHash<String, Player>()
        val removedCount: Long = ops.delete(key, playerId) ?: 0L
        return removedCount > 0
    }


}