package org.project.backend_kotlin.redisModels

data class Player(
    val id: String,
    val nickname: String,
    val isHost: Boolean,
    val roomCode: String
)
