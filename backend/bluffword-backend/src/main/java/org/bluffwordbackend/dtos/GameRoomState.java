package org.bluffwordbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.models.GameMode;

import java.util.ArrayList;
import java.util.List;

@Data
public class GameRoomState {
    private String code;
    private GameMode mode;
    private List<PlayerInfoDto> players = new ArrayList<>();
    private boolean started = false;
    public GameRoomState(String code, GameMode mode) {
        this.code = code;
        this.mode = mode;
        this.players = new ArrayList<>();
        this.started = false;
    }
}
