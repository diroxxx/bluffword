package org.bluffwordbackend.records;

import org.bluffwordbackend.dtos.GameRoomSettingsDto;

public record CreateRoomRequestDto(String nickname, GameRoomSettingsDto settings) {
}
