package org.project.backend_kotlin.gameRoom.dto

import jakarta.validation.constraints.NotBlank

class JoinRoomRequestDto(

    @NotBlank(message = "nickname is required")
    val nickname: String,
    @NotBlank(message = "roomCode is required")
    val roomCode: String
)
