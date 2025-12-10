package org.bluffwordbackend.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.dtos.PlayerInfoDto;
import org.bluffwordbackend.models.GameRoom;
import org.bluffwordbackend.models.Player;
import org.bluffwordbackend.models.RoomPlayer;
import org.bluffwordbackend.repositories.GameRoomRepository;
import org.bluffwordbackend.repositories.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GameRoomService {
    private final GameRoomRepository gameRoomRepository;
    private final PlayerRepository playerRepository;


    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    @Transactional
    public String createRoom(Long playerId) {
        Optional<Player> player = playerRepository.findById(playerId);
        GameRoom gameRoomCopy = new GameRoom();
        if (player.isPresent()) {
            gameRoomCopy.setCode(generateRoomCode());

            RoomPlayer roomPlayer = new RoomPlayer();
            roomPlayer.setPlayer(player.get());
            roomPlayer.setGameRoom(gameRoomCopy);
            roomPlayer.setHost(true);
            gameRoomRepository.save(gameRoomCopy);
        }
        return gameRoomCopy.getCode();
    }

    public List<PlayerInfoDto> getListOfPlayers(String code) {
        Optional<GameRoom> gameRoom = gameRoomRepository.findByCode(code);
        if (gameRoom.isPresent()) {
            return gameRoom.get().getPlayers()
                    .stream()
                    .map(RoomPlayer::getPlayer)
                    .filter(Objects::nonNull)
                    .map( PlayerInfoDto::toDto)
                    .toList();
        }
        return new ArrayList<>();
    }

}
