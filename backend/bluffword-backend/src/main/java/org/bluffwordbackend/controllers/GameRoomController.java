package org.bluffwordbackend.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bluffwordbackend.dtos.GameRoomState;
import org.bluffwordbackend.dtos.PlayerInfoDto;
import org.bluffwordbackend.models.GameMode;
import org.bluffwordbackend.models.Player;
import org.bluffwordbackend.services.GameRoomBroadcaster;
import org.bluffwordbackend.services.GameRoomService;
import org.bluffwordbackend.services.RoundService;
import org.bluffwordbackend.websocket.WebSocketDisconnectListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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


//    @GetMapping("/GameModes")
//    public ResponseEntity<List<GameMode>> getGameModes() {
//        List<GameMode> gameModes = new ArrayList<>();
//        gameModes.add(GameMode.STATIC_IMPOSTOR);
//        gameModes.add(GameMode.ROUND_IMPOSTOR);
//        return ResponseEntity.ok(gameModes);
//    }


    @PostMapping("/{playerId}")
    public ResponseEntity<?> createRoom(@PathVariable("playerId") Long playerId) {
        String roomCode  = gameRoomService.createRoom(playerId);
        List<PlayerInfoDto> playerInfoDtos = gameRoomService.getListOfPlayers(roomCode);
        gameRoomBroadcaster.broadcastPlayers(roomCode, playerInfoDtos);
        return ResponseEntity.ok(Map.of("roomCode", roomCode));

    }

    @MessageMapping("/room/{roomCode}/players")
    public void getPlayers(@DestinationVariable String roomCode) {
        List<PlayerInfoDto> playerInfoDtos = gameRoomService.getListOfPlayers(roomCode);
        gameRoomBroadcaster.broadcastPlayers(roomCode, playerInfoDtos);
    }



}
