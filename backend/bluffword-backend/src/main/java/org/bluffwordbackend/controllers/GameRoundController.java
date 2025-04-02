package org.bluffwordbackend.controllers;

import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.services.GameRoundManager;
import org.bluffwordbackend.services.InMemoryGameRoomService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class GameRoundController {
    private final InMemoryGameRoomService gameRoomService;
    private final GameRoundManager gameRoundManager;


    @MessageMapping("/room/{code}/start")
    public void startGame(@DestinationVariable String code) {
        gameRoundManager.startGame(code);
    }

}
