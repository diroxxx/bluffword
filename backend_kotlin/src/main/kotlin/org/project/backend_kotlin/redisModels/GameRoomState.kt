package org.project.backend_kotlin.redisModels

enum class GameRoomState {
    LOBBY,
    CATEGORY_SELECTION,
    GAME_START,
    ROUND_START,
    ANSWERING,
    VOTING,
    RESULTS,
    GAME_END
}