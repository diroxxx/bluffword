package org.bluffwordbackend.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bluffwordbackend.dtos.GameRoomState;
import org.bluffwordbackend.dtos.PlayerInfoDto;
import org.bluffwordbackend.services.InMemoryGameRoomService;
import org.bluffwordbackend.websocket.WebSocketDisconnectListener;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/gameRoom")
@CrossOrigin(origins = "http://localhost:5173")
public class GameRoomController {

   private final InMemoryGameRoomService gameRoomMemory;
   private final SimpMessagingTemplate messagingTemplate;
   private final WebSocketDisconnectListener webSocketDisconnectListener;


    @PostMapping("/{code}/start")
    public ResponseEntity<Void> startGame(@PathVariable String code) {
        log.info("Starting game room {}", code);
        GameRoomState room = gameRoomMemory.getRoom(code);

        return ResponseEntity.ok().build();
    }

//start of change

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createRoom() {
        String code = gameRoomMemory.generateCode();
        GameRoomState room = new GameRoomState(code);
        gameRoomMemory.saveRoom(code, room);
        return ResponseEntity.ok(Map.of("code", code));
    }


    @MessageMapping("/room/{code}/players")
    public void registerPlayer(@DestinationVariable String code,
                               @Payload PlayerInfoDto player,
                               StompHeaderAccessor accessor) {

        String sessionId = accessor.getSessionId();
        GameRoomState room = gameRoomMemory.getRoom(code);
        if (room == null) return;

        player.setSessionId(sessionId);

        Optional<PlayerInfoDto> existingPlayer = room.getPlayers().stream()
                .filter(p -> p.getNickname().equals(player.getNickname()))
                .findFirst();

        if (existingPlayer.isPresent()) {
            existingPlayer.get().setSessionId(sessionId);
        } else {
            room.getPlayers().add(player);
        }
        webSocketDisconnectListener.cancelDisconnectTimer(player.getNickname());

        messagingTemplate.convertAndSend(
                "/topic/room/" + code + "/players",
                room.getPlayers()
        );
    }


//end of change

    @MessageMapping("/room/{code}/sync")
    public void syncRoomState(@DestinationVariable String code) {
        log.info("sync list of players in room {}", code);
        GameRoomState room = gameRoomMemory.getRoom(code);
        if (room == null) {
            System.out.println("Room not found for code: " + code);
            return;
        }
        messagingTemplate.convertAndSend(
                "/topic/room/" + code + "/players",
                room.getPlayers()
        );
    }

    @PostMapping("/{code}/leave")
    public ResponseEntity<Void> leaveGameRoom(
            @PathVariable String code,
            @RequestBody PlayerInfoDto request
    ) {
        GameRoomState room = gameRoomMemory.getRoom(code);
        if (room == null) {
            return ResponseEntity.badRequest().build();
        }

        room.getPlayers().removeIf(p ->
                p.getNickname().equalsIgnoreCase(request.getNickname())
        );

        messagingTemplate.convertAndSend(
                "/topic/room/" + code + "/players",
                room.getPlayers()
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{code}/players/{nickname}")
    public ResponseEntity<Map<String,String>> deletePlayer(@PathVariable String code, @PathVariable String nickname) {
        GameRoomState room= gameRoomMemory.getRoom(code);
        if (room == null) {
            log.info("Room not found for code: {}", code);
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Room not found"
            ));
        }
        boolean isRemoved = gameRoomMemory.removePlayerFromRoom(code, nickname);
            if (isRemoved) {
                log.info("Removed player from room {}", code);
                messagingTemplate.convertAndSend(
                        "/topic/room/" + code + "/players",
                        room.getPlayers()
                );
                messagingTemplate.convertAndSend(
                        "/topic/room/"+ code +"/kick/"+ nickname,
                        room.getPlayers()
                );

                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Player removed"
                ));
            }
            log.info("Player not found for code: " + code);



            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "message", "Player not found"
            ));

    }


    @Data
    @AllArgsConstructor
    public static class ClueMessage {
        private String clue;
        private String nickname;
    }
}
