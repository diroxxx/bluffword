package org.bluffwordbackend.dtos;

import org.bluffwordbackend.models.GameMode;

public record GameRoomSettingsDto(
        String roomCode,
        int roundTotal,
        int maxPlayers,
        int minPlayers,
        int timeLimitAnswer,
        int timeLimitVote,
        GameMode mode

        ) {
}
