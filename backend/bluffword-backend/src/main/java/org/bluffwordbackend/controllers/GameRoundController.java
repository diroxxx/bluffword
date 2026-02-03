package org.bluffwordbackend.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bluffwordbackend.dtos.PlayerWordResponse;
import org.bluffwordbackend.models.CategorySelectionMode;
import org.bluffwordbackend.models.GameRoom;
import org.bluffwordbackend.models.Player;
import org.bluffwordbackend.models.WordPairDto;
import org.bluffwordbackend.services.*;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/round")
public class GameRoundController {
    private final SimpMessagingTemplate messagingTemplate;
    private final WordPairService wordPairService;
    private final GameRoomService gameRoomService;
    private final RoundTimerService roundTimerService;
    private final RoundService roundService;
    private final PlayerService playerService;
    private final GameRoundBroadcaster gameRoundBroadcaster;

    @MessageMapping("/room/{roomCode}/round/player/{playerId}/word")
    public void startRound(@DestinationVariable String roomCode, @DestinationVariable Long playerId) {
        roundService.startOrSendRoundWords(roomCode, playerId);
    }


    @MessageMapping("/room/{roomCode}/round/{roundNumber}/categorySelection")
    public void categorySelection() {

    }

    @MessageMapping("/room/{roomCode}/round/{roundNumber}/answer")
    public void answering(@DestinationVariable String roomCode, @DestinationVariable Integer roundNumber, @RequestBody PlayerWordResponse playerWordResponse) {

    }



}
