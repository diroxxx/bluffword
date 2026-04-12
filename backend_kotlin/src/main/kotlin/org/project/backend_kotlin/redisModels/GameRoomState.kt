package org.project.backend_kotlin.redisModels

enum class GameRoomState {
    LOBBY,
    CATEGORY_SELECTION,
    ANSWERING,
    VOTING,
    VOTING_RESULTS,
    GAME_END
}