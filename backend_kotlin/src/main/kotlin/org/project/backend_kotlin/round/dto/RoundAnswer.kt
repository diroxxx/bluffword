package org.project.backend_kotlin.round.dto

data class RoundAnswer(
    val answer: String,
    val playerId: String,
    val similarityScore: Float = 0f,
)