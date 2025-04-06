package org.bluffwordbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.models.GameMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class GameRoomState {
    private String code;
    private GameMode mode;
    private List<PlayerInfoDto> players = new ArrayList<>();
    private Boolean isStarted;
    private Integer numberOfRounds;
    private Integer voteTime;
    private Integer roundTime;
    private Map<Integer,RoundState> roundStateMap = new HashMap<>();
    public GameRoomState(String code) {
        this.code = code;
        this.players = new ArrayList<>();
        this.isStarted = false;
    }
}
