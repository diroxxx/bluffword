package org.project.backend_kotlin.round.dto

data class RoundAnswerDto(
    val answer: String,
    val nickname: String,
    val similarityScore: Float,
)