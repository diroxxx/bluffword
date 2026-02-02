package org.bluffwordbackend.dtos;

import org.bluffwordbackend.models.CategorySelectionMode;
import org.bluffwordbackend.models.GameMode;
import org.bluffwordbackend.models.GameRoomState;

public record GameRoomSettingsDto(
        String roomCode,
        int roundTotal,
        int maxPlayers,
        int minPlayers,
        int timeLimitAnswer,
        int timeLimitVote,
        GameMode gameMode,
        GameRoomState gameRoomState,

        CategorySelectionMode categorySelectionMode,

        String staticCategory

        ) {
}
