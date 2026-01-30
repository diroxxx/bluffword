package org.bluffwordbackend.redisDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bluffwordbackend.models.GameMode;

@Data
@AllArgsConstructor
public class GameSettingsDto {
    private String roomCode;
    private int roundTotal;
    private int maxPlayers;
    private int minPlayers;
    private int timeLimitAnswer;
    private int timeLimitVote;
    private GameMode mode;
}
