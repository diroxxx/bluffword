package org.bluffwordbackend.redisDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bluffwordbackend.models.GameMode;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@RedisHash("gameRoomState")
public class GameRoomState {

    private GameSettingsDto settings;


    private Map<Long, PlayerDto> players = new HashMap<>();

    private Map<Integer, RoundState> roundStateMap;

}
