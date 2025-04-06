package org.bluffwordbackend.dtos;

import lombok.Data;
import org.bluffwordbackend.models.GameMode;

@Data
public class GameSettings {
    private int rounds;
    private GameMode mode;
    private int maxPlayers;
    private int voteTime;
    private int roundTime;
}
