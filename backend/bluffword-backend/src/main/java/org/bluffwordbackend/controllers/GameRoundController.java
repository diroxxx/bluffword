package org.bluffwordbackend.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bluffwordbackend.dtos.GameRoomState;
import org.bluffwordbackend.dtos.GameSettings;
import org.bluffwordbackend.models.GameMode;
import org.bluffwordbackend.services.InMemoryGameRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/round")
@CrossOrigin(origins = "http://localhost:5173")
public class GameRoundController {
    private final InMemoryGameRoomService gameRoomMemory;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/{code}/start")
    public ResponseEntity<Void> startGame(@PathVariable String code, @Payload GameSettings gameSettings) {
        log.info("Starting game room {}", code);
        GameRoomState room = gameRoomMemory.getRoom(code);
        room.setIsStarted(true);
        room.setMode(gameSettings.getMode());
        room.setVoteTime(gameSettings.getVoteTime());
        room.setRoundTime(gameSettings.getRoundTime());

        int randomIndex = gameRoomMemory.generateRandomIndex(room.getPlayers().size());
        room.getPlayers().get(randomIndex).setIsImpostor(true);

        messagingTemplate.convertAndSend(
                "/topic/room/" + code + "/start",
                "GAME_STARTED"
        );
        return ResponseEntity.ok().build();
    }


}
