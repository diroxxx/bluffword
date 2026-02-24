package org.project.backend_kotlin.gameRoom.dto

import org.project.backend_kotlin.redisModels.CategorySelectionMode
import org.project.backend_kotlin.redisModels.GameMode
import org.project.backend_kotlin.redisModels.GameRoomState

class GameSettingsRequestDto(
    val code: String?,
    val roundTotal: Int,
    val numberOfImpostors: Int,
    val maxPlayers: Int,
    val timeLimitAnswer: Int,
    val timeLimitVote: Int,
    val gameMode: GameMode,
    val categorySelectionMode: CategorySelectionMode,
    val staticCategory: String?,
) {
}