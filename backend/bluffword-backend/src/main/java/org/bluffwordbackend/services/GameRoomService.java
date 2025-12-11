package org.bluffwordbackend.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.dtos.PlayerDto;
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
    private final PlayerService playerService;

    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    @Transactional
    public PlayerDto createRoom(String nickname) {
        Player player = playerService.createPlayer(nickname);

        if (player != null) {

            GameRoom gameRoomCopy = new GameRoom();

            gameRoomCopy.setCode(generateRoomCode());
            RoomPlayer roomPlayer = new RoomPlayer();
            roomPlayer.setPlayer(player);
            roomPlayer.setGameRoom(gameRoomCopy);
            roomPlayer.setHost(true);
            gameRoomCopy.getPlayers().add(roomPlayer);

            gameRoomRepository.save(gameRoomCopy);

            PlayerDto playerDto = PlayerDto.toDto(player);
            playerDto.setIsHost(roomPlayer.isHost());
            playerDto.setRoomCode(gameRoomCopy.getCode());
            return playerDto;
        }

        return null;
    }



    @Transactional
    public PlayerDto joinRoom(String nickname, String roomCode) {
        Player player = playerService.createPlayer(nickname);


        if (player != null) {

            Optional <GameRoom> gameRoomCopy = gameRoomRepository.findByCodeFetchPlayers(roomCode);

            if (gameRoomCopy.isPresent()) {
                GameRoom room = gameRoomCopy.get();
                RoomPlayer roomPlayer = new RoomPlayer();
                roomPlayer.setPlayer(player);
                roomPlayer.setGameRoom(room);
                roomPlayer.setHost(false);
                room.getPlayers().add(roomPlayer);
                gameRoomRepository.save(room);

                PlayerDto playerDto = PlayerDto.toDto(player);
                playerDto.setIsHost(false);
                playerDto.setRoomCode(room.getCode());

                return playerDto;
            }
        }

        return null;
    }




    @Transactional()
    public List<PlayerDto> getListOfPlayers(String code) {
        Optional<GameRoom> gameRoom = gameRoomRepository.findByCodeFetchPlayers(code);
        return gameRoom.map(room -> room.getPlayers()
                .stream()
                .map(rp -> {
                    PlayerDto dto = PlayerDto.toDto(rp.getPlayer());
                    dto.setIsHost(rp.isHost());
                    dto.setRoomCode(room.getCode());
                    return dto;
                })
                .toList()).orElseGet(ArrayList::new);
    }

    @Transactional
    public void deletePlayerFromRoom(String roomCode, Long playerId) {
        Optional<GameRoom> gameRoomOpt = gameRoomRepository.findByCodeFetchPlayers(roomCode);
        if (gameRoomOpt.isPresent()) {
            GameRoom gameRoom = gameRoomOpt.get();
            RoomPlayer roomPlayerToRemove = null;

            for (RoomPlayer rp : gameRoom.getPlayers()) {
                if (rp.getPlayer().getId().equals(playerId)) {
                    roomPlayerToRemove = rp;
                    break;
                }
            }

            if (roomPlayerToRemove != null) {
                gameRoom.getPlayers().remove(roomPlayerToRemove);
                gameRoomRepository.save(gameRoom);
                playerRepository.deleteById(playerId);
            }
        }
    }




}
