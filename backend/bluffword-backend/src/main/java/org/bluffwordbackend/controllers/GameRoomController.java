package org.bluffwordbackend.controllers;

import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.models.GameMode;
import org.bluffwordbackend.models.GameRoom;
import org.bluffwordbackend.services.GameRoomService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class GameRoomController {

    private final GameRoomService gameRoomService;


    @PostMapping
    public GameRoom createRoom(@RequestParam GameMode mode,
                               @RequestParam(defaultValue = "5") int rounds) {
        return gameRoomService.createRoom(mode, rounds);
    }
}
