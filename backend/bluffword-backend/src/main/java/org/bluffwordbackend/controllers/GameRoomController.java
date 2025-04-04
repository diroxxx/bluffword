package org.bluffwordbackend.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bluffwordbackend.dtos.GameRoomState;
import org.bluffwordbackend.dtos.PlayerInfoDto;
import org.bluffwordbackend.services.InMemoryGameRoomService;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/gameRoom")
@CrossOrigin(origins = "http://localhost:5173")
public class GameRoomController {

   private final InMemoryGameRoomService gameRoomMemory;
    private final SimpMessagingTemplate messagingTemplate;


    @PostMapping("/{code}/start")
    public ResponseEntity<Void> startGame(@PathVariable String code) {
        log.info("Starting game room {}", code);
        GameRoomState room = gameRoomMemory.getRoom(code);

        return ResponseEntity.ok().build();
    }

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


@PostMapping("/create")
public ResponseEntity<Map<String, String>> createRoom( @RequestBody PlayerInfoDto request) {

        String code = gameRoomMemory.generateCode();
    log.info("Creating new room with code: {}", code);
    GameRoomState room = new GameRoomState(code);

    room.getPlayers().add(request);
    gameRoomMemory.saveRoom(code, room);

    messagingTemplate.convertAndSend(
            "/topic/room/" + code + "/players",
            room.getPlayers()
    );

    return ResponseEntity.ok(Map.of("code", code));
}


    @PostMapping("/{code}/join")
    public ResponseEntity<Void> joinGameRoom(
            @PathVariable String code,
            @RequestBody PlayerInfoDto request) {
        log.info("Received join request for code: {}", code);

        GameRoomState room = gameRoomMemory.getRoom(code);
        if (room == null || room.getIsStarted()) {
            System.out.println("Room not found for code: " + code);
            return ResponseEntity.badRequest().build();
        }

        boolean alreadyJoined = room.getPlayers().stream()
                .anyMatch(p -> p.getNickname().equalsIgnoreCase(request.getNickname()));

        if (alreadyJoined) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        room.getPlayers().add(request);
        messagingTemplate.convertAndSend(
                "/topic/room/" + code + "/players",
                room.getPlayers()
        );

        return ResponseEntity.ok().build();
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

        System.out.println("Gracz opuścił pokój: " + request.getNickname());

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
            log.info("Room not found for code: " + code);
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
