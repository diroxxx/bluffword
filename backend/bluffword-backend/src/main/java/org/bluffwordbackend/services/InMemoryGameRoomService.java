package org.bluffwordbackend.services;

import org.bluffwordbackend.dtos.GameRoomState;
import org.bluffwordbackend.dtos.PlayerInfoDto;
import org.bluffwordbackend.models.GameMode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryGameRoomService {
    private final Map<String, GameRoomState> rooms = new ConcurrentHashMap<>();

//    public String createRoom(GameMode mode) {
//        String code = generateCode();
//        rooms.put(code, new GameRoomState(code, mode));
//        return code;
//    }
    public void saveRoom(String code, GameRoomState state) {
        rooms.put(code, state);
    }


    public GameRoomState getRoom(String code) {
        return rooms.get(code);
    }

    public String generateCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public GameRoomState joinRoom(String code, PlayerInfoDto player) {
        GameRoomState room = rooms.get(code);
        if (room == null || room.isStarted()) {
            throw new IllegalStateException("Room not found or already started");
        }
        room.getPlayers().add(player);
        return room;
    }
}
