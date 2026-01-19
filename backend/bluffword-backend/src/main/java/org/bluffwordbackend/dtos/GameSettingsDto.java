package org.bluffwordbackend.dtos;

import lombok.Data;
import org.bluffwordbackend.models.GameMode;

@Data
public class GameSettingsDto {
    private String roomCode;
    private int roundTotal;
    private int maxPlayers;
    private int minPlayers;
    private int timeLimitAnswer;
    private int timeLimitVote;
    private GameMode mode;
}
