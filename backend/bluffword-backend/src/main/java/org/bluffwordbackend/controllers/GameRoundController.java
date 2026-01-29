package org.bluffwordbackend.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bluffwordbackend.services.GameRoomService;
import org.bluffwordbackend.services.RoundTimerService;
import org.bluffwordbackend.services.WordPairService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/round")
public class GameRoundController {
    private final SimpMessagingTemplate messagingTemplate;
    private final WordPairService wordPairService;
    private final GameRoomService gameRoomService;
    private final RoundTimerService roundTimerService;


    @MessageMapping("/room/{roomCode}/round/player/{playerId}/start")
    public void startRound(@DestinationVariable String roomCode, @DestinationVariable Long playerId) {




    }




}
