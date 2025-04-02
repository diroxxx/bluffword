package org.bluffwordbackend.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.dtos.GameRoomState;
import org.bluffwordbackend.dtos.PlayerInfoDto;
import org.bluffwordbackend.models.GameMode;
import org.bluffwordbackend.services.GameLoopManager;
import org.bluffwordbackend.services.InMemoryGameRoomService;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/gameRoom")
@CrossOrigin(origins = "http://localhost:5173")
public class GameSocketController {

   private final InMemoryGameRoomService gameRoomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final GameLoopManager gameLoopManager;


    @PostMapping("/{code}/start")
    public ResponseEntity<Void> startGame(@PathVariable String code) {
        gameLoopManager.startGame(code);
        return ResponseEntity.ok().build();
    }


    @MessageMapping("/room/{code}/sync")
    public void syncRoomState(@DestinationVariable String code) {
        GameRoomState room = gameRoomService.getRoom(code);
        if (room == null) {
            System.out.println("Room not found for code: " + code);
            return;
        }
        System.out.println(room.getPlayers());
        messagingTemplate.convertAndSend(
                "/topic/room/" + code + "/players",
                room.getPlayers()
        );
    }


@PostMapping("/create")
public ResponseEntity<Map<String, String>> createRoom( @RequestBody PlayerInfoDto request
                                                     ) {
//                                                       @RequestHeader("simpSessionId") String sessionId ) {
    String code = gameRoomService.generateCode();
    GameRoomState room = new GameRoomState(code);

//    request.setSessionId(sessionId);
    room.getPlayers().add(request);
    gameRoomService.saveRoom(code, room);

    messagingTemplate.convertAndSend(
            "/topic/room/" + code + "/players",
            room.getPlayers()
    );

    return ResponseEntity.ok(Map.of("code", code));
}


    @PostMapping("/{code}/join")
    public ResponseEntity<Void> joinGameRoom(
            @PathVariable String code,
            @RequestBody PlayerInfoDto request
//            @RequestHeader("simpSessionId") String sessionId
            ) {

        System.out.println(code);
        System.out.println(request.getNickname());

        GameRoomState room = gameRoomService.getRoom(code);
        if (room == null || room.getIsStarted()) {
            System.out.println("Room not found for code: " + code);
            return ResponseEntity.badRequest().build();
        }

        boolean alreadyJoined = room.getPlayers().stream()
                .anyMatch(p -> p.getNickname().equalsIgnoreCase(request.getNickname()));

        if (alreadyJoined) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
//        request.setSessionId(sessionId);
        room.getPlayers().add(request);
        messagingTemplate.convertAndSend(
                "/topic/room/" + code + "/players",
                room.getPlayers()
        );

        return ResponseEntity.ok().build();
    }

//    @MessageMapping("/room/create")
//    public void createRoomViaSocket(PlayerInfoDto request, Message<?> message) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        String sessionId = accessor.getSessionId();
//
//        Principal user = accessor.getUser();
//        if (user == null) {
//            System.err.println("🚨 Brak użytkownika (Principal) – nie można stworzyć pokoju");
//            return;
//        }
//
//        String username = user.getName();
//
//
//        String code = gameRoomService.generateCode();
//        GameRoomState room = new GameRoomState(code);
//
//        request.setSessionId(sessionId);
//        request.setIsHost(true); // bo tworzący = host
//
//        room.getPlayers().add(request);
//        gameRoomService.saveRoom(code, room);
//
//        messagingTemplate.convertAndSendToUser(
//                username,
//                "/queue/room/created",
//                Map.of("code", code)
//        );
//
//
//        messagingTemplate.convertAndSend(
//                "/topic/room/" + code + "/players",
//                room.getPlayers()
//        );
//    }
//
//    @MessageMapping("/room/{code}/join")
//    public void joinRoomViaSocket(@DestinationVariable String code,
//                                  PlayerInfoDto request,
//                                  Message<?> message) {
//
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        String sessionId = accessor.getSessionId();
//        String username = accessor.getUser().getName(); // 💡 Dodane
//
//        GameRoomState room = gameRoomService.getRoom(code);
//        if (room == null || room.getIsStarted()) return;
//
//        request.setSessionId(sessionId);
//        request.setIsHost(false);
//
//        boolean alreadyJoined = room.getPlayers().stream()
//                .anyMatch(p -> p.getNickname().equalsIgnoreCase(request.getNickname()));
//
//        if (!alreadyJoined) {
//            room.getPlayers().add(request);
//            messagingTemplate.convertAndSend(
//                    "/topic/room/" + code + "/players",
//                    room.getPlayers()
//            );
//        }
//        messagingTemplate.convertAndSendToUser(
//                username,
//                "/queue/room/joined",
//                Map.of(
//                        "code", code,
//                        "players", room.getPlayers()
//                )
//        );
//
//    }






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

        System.out.println("Gracz opuścił pokój: " + request.getNickname());

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
