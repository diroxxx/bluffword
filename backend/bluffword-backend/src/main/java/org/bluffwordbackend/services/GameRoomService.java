package org.bluffwordbackend.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.dtos.GameRoomSettingsDto;
import org.bluffwordbackend.models.*;
import org.bluffwordbackend.redisDtos.PlayerDto;
import org.bluffwordbackend.repositories.GameRoomRepository;
import org.bluffwordbackend.repositories.PlayerRepository;
import org.bluffwordbackend.repositories.WordCategoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GameRoomService {
    private final GameRoomRepository gameRoomRepository;
    private final PlayerRepository playerRepository;
    private final PlayerService playerService;
    private final WordCategoryRepository wordCategoryRepository;
    private final Random random = new Random();

    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    @Transactional
    public PlayerDto createRoom(String nickname, GameRoomSettingsDto settingsDto) {
        Player player = playerService.createPlayer(nickname);

        if (player != null) {

            WordCategory wordCategory = null;
            if (settingsDto.staticCategory() != null) {
                wordCategory = wordCategoryRepository.findByName(settingsDto.staticCategory()).orElse(null);
            }

            GameRoom gameRoomCopy = GameRoom.builder()
                    .code(generateRoomCode())
                    .roundTotal(settingsDto.roundTotal())
                    .currentRound(0)
                    .maxPlayers(settingsDto.maxPlayers())
                    .minPlayers(settingsDto.minPlayers())
                    .timeLimitAnswer(settingsDto.timeLimitAnswer())
                    .timeLimitVote(settingsDto.timeLimitVote())
                    .state(GameRoomState.LOBBY)
                    .gameMode(settingsDto.gameMode())
                    .categorySelectionMode(settingsDto.categorySelectionMode())
                    .staticCategory(wordCategory)
                    .players(new HashSet<>())
                    .host(player)
                    .build();

            RoomPlayer roomPlayer = new RoomPlayer();
            roomPlayer.setPlayer(player);
            roomPlayer.setGameRoom(gameRoomCopy);
            gameRoomCopy.getPlayers().add(roomPlayer);

            gameRoomRepository.save(gameRoomCopy);

            PlayerDto playerDto = PlayerDto.toDto(player);
            playerDto.setIsHost(true);
            playerDto.setRoomCode(gameRoomCopy.getCode());
            return playerDto;
        }

        return null;
    }


//    private WordCategory generateStaticCategory(){
//        wordCategoryRepository.findRandomByCategory()
//    }



    public Optional<WordCategory> pickRandomByCategory(String name) {
        long categoryCount = wordCategoryRepository.countByName(name);
        if (categoryCount == 0) return Optional.empty();

        int index = random.nextInt((int) categoryCount);
        return wordCategoryRepository.findByName(name, PageRequest.of(index, 1))
                .stream()
                .findFirst();
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
                    Player host = room.getHost();

                    PlayerDto dto = PlayerDto.toDto(rp.getPlayer());
                    dto.setIsHost(host.getId().equals(rp.getPlayer().getId()));
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

    public GameRoomSettingsDto getRoomSettings(String roomCode) {
        Optional<GameRoom> gameRoomOpt = gameRoomRepository.findByCodeFetchPlayers(roomCode);
        if (gameRoomOpt.isPresent()) {
            GameRoom gameRoom = gameRoomOpt.get();

            return new GameRoomSettingsDto(
                    gameRoom.getCode(),
                    gameRoom.getRoundTotal(),
                    gameRoom.getMaxPlayers(),
                    gameRoom.getMinPlayers(),
                    gameRoom.getTimeLimitAnswer(),
                    gameRoom.getTimeLimitVote(),
                    gameRoom.getGameMode(),
                    gameRoom.getState(),
                    gameRoom.getCategorySelectionMode(),
                    gameRoom.getStaticCategory().getName()
            );
        }
        return null;
    }


    public GameRoom getGameRoomByCode(String roomCode) {
        return gameRoomRepository.findGameRoomByCode(roomCode).orElse(null);
    }


    public void retrive(String roomCode) {
        Optional<GameRoom> gameRoomByCode = gameRoomRepository.findGameRoomByCode(roomCode);

        if (gameRoomByCode.isPresent()) {
            gameRoomByCode.get().setCurrentRound(1);
            gameRoomByCode.get().setState(GameRoomState.GAME_START);
        }

    }

    public Optional<Long> chooseRandomImpostor(String roomCode) {

        Optional<GameRoom> gameRoomByCode = gameRoomRepository.findGameRoomByCode(roomCode);
        if (gameRoomByCode.isPresent()) {
            List<Player> allPlayersInRoom = gameRoomRepository.findAllPlayersInRoom(gameRoomByCode.get().getId());
            int randomPlayerId = generateRandomNumber(0, allPlayersInRoom.size());
            return Optional.of(allPlayersInRoom.get(randomPlayerId).getId());

        } else {
            throw new RuntimeException("Room not found");
        }

    }


    private int generateRandomNumber(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }


    public GameRoomState getRoomState(String roomCode) {
        Optional<GameRoom> gameRoomByCode = gameRoomRepository.findGameRoomByCode(roomCode);
        return gameRoomByCode.map(GameRoom::getState).orElse(null);
    }



    public void updateGameRoomState(String roomCode, GameRoomState state) {
        Optional<GameRoom> gameRoomByCode = gameRoomRepository.findGameRoomByCode(roomCode);
        if (gameRoomByCode.isPresent()) {
            gameRoomByCode.get().setState(state);
            gameRoomRepository.save(gameRoomByCode.get());
        }
    }


}
