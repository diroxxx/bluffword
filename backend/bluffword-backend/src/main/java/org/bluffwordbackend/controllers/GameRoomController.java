package org.bluffwordbackend.controllers;

import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.models.GameMode;
import org.bluffwordbackend.models.GameRoom;
import org.bluffwordbackend.services.GameRoomService;
import org.bluffwordbackend.services.InMemoryGameRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5174") // ← ważne

@RequestMapping("/api/gameRoom")
public class GameRoomController {

    private final GameRoomService gameRoomService;
    private final InMemoryGameRoomService inMemoryGameRoomService;

    @GetMapping("/generate")
    public ResponseEntity<Map<String, String>> generateGameRoomCode() {
        String code = inMemoryGameRoomService.generateCode();
        return ResponseEntity.ok(Map.of("code", code));
    }


}
