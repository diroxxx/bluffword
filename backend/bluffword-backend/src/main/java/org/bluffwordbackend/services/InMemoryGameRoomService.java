package org.bluffwordbackend.services;

import lombok.extern.slf4j.Slf4j;
import org.bluffwordbackend.dtos.GameRoomState;
import org.bluffwordbackend.dtos.PlayerInfoDto;
import org.bluffwordbackend.models.GameMode;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class InMemoryGameRoomService {
    private final Map<String, GameRoomState> rooms = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public void saveRoom(String code, GameRoomState state) {
        rooms.put(code, state);
    }



    public boolean removePlayerFromRoom(String code, String nickname) {
        GameRoomState room = rooms.get(code);
        if (room != null) {

            return room.getPlayers().removeIf(player -> player.getNickname().equalsIgnoreCase(nickname));
        }
        return false;
    }

    public GameRoomState getRoom(String code) {
        return rooms.get(code);
    }

    public String generateCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
    public List<GameRoomState> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }


    public int generateRandomIndex(int size) {
        return random.nextInt(size);
    }

}
