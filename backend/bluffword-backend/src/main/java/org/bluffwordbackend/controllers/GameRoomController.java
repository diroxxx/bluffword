package org.bluffwordbackend.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bluffwordbackend.dtos.JoinGameRoomRequestDto;
import org.bluffwordbackend.dtos.PlayerDto;
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

//    @GetMapping("/GameModes")
//    public ResponseEntity<List<GameMode>> getGameModes() {
//        List<GameMode> gameModes = new ArrayList<>();
//        gameModes.add(GameMode.STATIC_IMPOSTOR);
//        gameModes.add(GameMode.ROUND_IMPOSTOR);
//        return ResponseEntity.ok(gameModes);
//    }


    @GetMapping("/test")
    public ResponseEntity<?> getListOfPlayers(@RequestParam String roomCode) {
        List<PlayerDto> playerDtos = gameRoomService.getListOfPlayers(roomCode);
        return ResponseEntity.ok(playerDtos);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody Map<String,String> request) {
        String nickname = request.get("nickname");
        PlayerDto playerDto = gameRoomService.createRoom(nickname);
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

}
