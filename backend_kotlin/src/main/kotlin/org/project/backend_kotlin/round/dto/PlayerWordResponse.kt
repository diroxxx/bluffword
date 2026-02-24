package org.project.backend_kotlin.round.dto

class PlayerWordResponse(
    val word: String,
    val isImpostor: Boolean,
    val currentRound: Int

) {
}