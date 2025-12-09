package org.bluffwordbackend.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.models.GameRoom;
import org.bluffwordbackend.repositories.GameRoomRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameRoomService {
    private final GameRoomRepository gameRoomRepository;

    public String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    @Transactional
    public void createRoom(GameRoom gameRoom) {
        if (gameRoom != null){
            gameRoomRepository.save(gameRoom);
        }
    }

}
