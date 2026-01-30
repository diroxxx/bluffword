package org.bluffwordbackend.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bluffwordbackend.dtos.GameRoomSettingsDto;
import org.bluffwordbackend.dtos.JoinGameRoomRequestDto;
import org.bluffwordbackend.redisDtos.PlayerDto;
import org.bluffwordbackend.services.GameRoomBroadcaster;
import org.bluffwordbackend.services.GameRoomService;
import org.bluffwordbackend.services.RoundService;
import org.bluffwordbackend.websocket.WebSocketDisconnectListener;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/gameRoom")
@CrossOrigin(origins = "http://localhost:5173")
public class GameRoomController {

   private final SimpMessagingTemplate messagingTemplate;
   private final WebSocketDisconnectListener webSocketDisconnectListener;
    private final GameRoomService gameRoomService;
    private final RoundService roundService;

    private final GameRoomBroadcaster gameRoomBroadcaster;

    private final ModelMapper modelMapper;


    @GetMapping("/test")
    public ResponseEntity<?> getListOfPlayers(@RequestParam String roomCode) {
        List<PlayerDto> playerDtos = gameRoomService.getListOfPlayers(roomCode);
        return ResponseEntity.ok(playerDtos);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody String nickname, @RequestBody GameRoomSettingsDto gameRoomSettingsDto) {
        if ( nickname == null || nickname.isBlank()) {
                        return ResponseEntity.badRequest().body(Map.of("message", "nickname is required"));
        }
        if ( gameRoomSettingsDto == null ) {
            return ResponseEntity.badRequest().body(Map.of("message", "game Settings are required"));
        }

        PlayerDto playerDto = gameRoomService.createRoom(nickname, gameRoomSettingsDto);
        if (playerDto == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        List<PlayerDto> playerDtos = gameRoomService.getListOfPlayers(playerDto.getRoomCode());
        gameRoomBroadcaster.broadcastPlayers(playerDto.getRoomCode(), playerDtos);

        return ResponseEntity.ok(playerDto);
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinGameRoomRequestDto request) {
        PlayerDto playerDto = gameRoomService.joinRoom(request.nickname(), request.roomCode());
        List<PlayerDto> playerDtos = gameRoomService.getListOfPlayers(playerDto.getRoomCode());
        gameRoomBroadcaster.broadcastPlayers(playerDto.getRoomCode(), playerDtos);

        return ResponseEntity.ok(playerDto);
    }


    @MessageMapping("/room/{roomCode}/players")
    public void getPlayers(@DestinationVariable String roomCode) {
        List<PlayerDto> playerDtos = gameRoomService.getListOfPlayers(roomCode);
        gameRoomBroadcaster.broadcastPlayers(roomCode, playerDtos);

    }

    @DeleteMapping("/players")
    public void deletePlayerFromRoom(@RequestBody Map<String, String> request) {
        String code = request.get("roomCode");
        Long playerId = Long.parseLong(request.get("playerId"));
        gameRoomService.deletePlayerFromRoom(code, playerId);

        List<PlayerDto> playerDtos = gameRoomService.getListOfPlayers(code);
        gameRoomBroadcaster.broadcastPlayers(code, playerDtos);
    }

    @GetMapping("/settings")
    public ResponseEntity<?> getGameSettings(@RequestParam String roomCode) {

       return ResponseEntity.ok(gameRoomService.getRoomSettings(roomCode));

    }


}
