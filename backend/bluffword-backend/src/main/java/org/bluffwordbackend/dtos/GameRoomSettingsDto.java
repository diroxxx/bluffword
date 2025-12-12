package org.bluffwordbackend.dtos;

public record GameRoomSettingsDto(
        int maxPlayers,
        int roundTotal,
        String roomCode,
        int roundTimeSeconds
        ) {
}
