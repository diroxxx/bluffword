package org.project.backend_kotlin.round.dto

data class VotingResultDto(
    val nickname: String?,
    val voteCount: Int,
    val isImpostor: Boolean,
    val isGameOver: Boolean,
)
