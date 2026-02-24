package org.project.backend_kotlin.redisModels

import lombok.Builder

@Builder
data class GameRoom(
    val code: String,
    val roundTotal: Int,
    val currentRound: Int,
    val numberOfImpostors: Int,
    val maxPlayers: Int,
    val timeLimitAnswer: Int,
    val timeLimitVote: Int,
    val state: GameRoomState,
    val gameMode: GameMode,
    val staticCategory: String?,
    val categorySelectionMode: CategorySelectionMode,
    var hostId: String,
)
