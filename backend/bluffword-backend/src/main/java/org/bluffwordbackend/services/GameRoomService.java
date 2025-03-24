package org.bluffwordbackend.services;

import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.models.GameMode;
import org.bluffwordbackend.models.GameRoom;
import org.bluffwordbackend.repositories.GameRoomRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameRoomService {

    private final GameRoomRepository gameRoomRepository;

    public GameRoom createRoom(GameMode gameMode, int roundsTotal) {
        GameRoom room = new GameRoom();
        room.setGameMode(gameMode);
        room.setRound_total(roundsTotal);
        room.setCode(generateRoomCode());
        return gameRoomRepository.save(room);
    }

    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

}
