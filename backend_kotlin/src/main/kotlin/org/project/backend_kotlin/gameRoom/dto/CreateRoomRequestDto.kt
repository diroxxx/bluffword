package org.project.backend_kotlin.gameRoom.dto

class CreateRoomRequestDto(
//    @NotBlank(message = "nickname is required")
    val nickname: String,
//    @NotBlank(message = "gameRoom is required")
    val settings: GameSettingsRequestDto
) {
}