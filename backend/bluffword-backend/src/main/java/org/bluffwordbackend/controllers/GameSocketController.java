package org.bluffwordbackend.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.dtos.GameRoomState;
import org.bluffwordbackend.dtos.PlayerInfoDto;
import org.bluffwordbackend.models.GameMode;
import org.bluffwordbackend.services.InMemoryGameRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/gameRoom")
@CrossOrigin(origins = "http://localhost:5173")
public class GameSocketController {

   private final InMemoryGameRoomService gameRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    @SendTo("/topic/mes")
    public String chat(@Payload PlayerInfoDto playerInfoDto) {
        return playerInfoDto + " asdfsdfsdfasdf";
    }

    @MessageMapping("/room/{code}/sync")
    public void syncRoomState(@DestinationVariable String code) {
        GameRoomState room = gameRoomService.getRoom(code);
        if (room == null) {
            System.out.println("❌ Room not found for code: " + code);
            return;
        }
        System.out.println(room.getPlayers());
        messagingTemplate.convertAndSend(
                "/topic/room/" + code + "/players",
                room.getPlayers()
        );
    }


    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createRoom(@RequestBody PlayerInfoDto request) {
        String code = gameRoomService.generateCode();
        GameRoomState room = new GameRoomState(code, GameMode.STATIC_IMPOSTOR); // lub z parametrem

        room.getPlayers().add(request);
        gameRoomService.saveRoom(code, room); // Dodaj tę metodę!

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

        System.out.println(code);
        System.out.println(request.getNickname());

        GameRoomState room = gameRoomService.getRoom(code);
        if (room == null || room.isStarted()) {
            return ResponseEntity.badRequest().build();
        }

        boolean alreadyJoined = room.getPlayers().stream()
                .anyMatch(p -> p.getNickname().equalsIgnoreCase(request.getNickname()));

        if (alreadyJoined) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 Conflict
        }
        room.getPlayers().add(request);
        messagingTemplate.convertAndSend(
                "/topic/room/" + code + "/players",
                room.getPlayers()
        );
        System.out.println("Wysyłam WS: " + room.getPlayers());

        return ResponseEntity.ok().build();
    }


    @PostMapping("/{code}/leave")
    public ResponseEntity<Void> leaveGameRoom(
            @PathVariable String code,
            @RequestBody PlayerInfoDto request
    ) {
        GameRoomState room = gameRoomService.getRoom(code);
        if (room == null) {
            return ResponseEntity.badRequest().build();
        }

        room.getPlayers().removeIf(p ->
                p.getNickname().equalsIgnoreCase(request.getNickname())
        );

        System.out.println("❌ Gracz opuścił pokój: " + request.getNickname());

        messagingTemplate.convertAndSend(
                "/topic/room/" + code + "/players",
                room.getPlayers()
        );

        return ResponseEntity.ok().build();
    }




    @Data
    @AllArgsConstructor
    public static class ClueMessage {
        private String clue;
        private String nickname;
    }
}
