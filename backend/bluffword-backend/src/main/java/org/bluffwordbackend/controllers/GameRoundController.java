package org.bluffwordbackend.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bluffwordbackend.dtos.GameRoomState;
import org.bluffwordbackend.dtos.GameSettings;
import org.bluffwordbackend.dtos.PlayerInfoDto;
import org.bluffwordbackend.dtos.RoundState;
import org.bluffwordbackend.models.WordPair;
import org.bluffwordbackend.services.InMemoryGameRoomService;
import org.bluffwordbackend.services.WordPairService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/round")
@CrossOrigin(origins = "http://localhost:5173")
public class GameRoundController {
    private final InMemoryGameRoomService gameRoomMemory;
    private final SimpMessagingTemplate messagingTemplate;
    private final WordPairService wordPairService;

    @PostMapping("/{code}/start")
    public ResponseEntity<Void> startGame(@PathVariable String code, @RequestBody  GameSettings gameSettings) {
        log.info("Starting game room {}", code);
        GameRoomState room = gameRoomMemory.getRoom(code);
        room.setIsStarted(true);
        room.setMode(gameSettings.getMode());
        room.setVoteTime(gameSettings.getVoteTime());
        room.setRoundTime(gameSettings.getRoundTime());
        room.setNumberOfRounds(gameSettings.getRounds());
        List<WordPair> wordPairs = wordPairService.getWordPairs();

        //random impostor
        int randomIndex = gameRoomMemory.generateRandomIndex(room.getPlayers().size());
        PlayerInfoDto impostor = room.getPlayers().get(randomIndex);
        //create rounds, and add impostror to every rounds for example
        for (int i = 0; i < room.getNumberOfRounds(); i++) {
            room.getRoundStateMap().put(i, new RoundState(wordPairs.get(i).getRealWord(),
                    wordPairs.get(i).getImpostorWord(), impostor.getNickname()));
        }

        messagingTemplate.convertAndSend(
                "/topic/room/" + code + "/start",
                "GAME_STARTED"
        );
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/{code}/{roundNumber}/answers")
    public void retrieveAnswersFromRound(@PathVariable String code, @PathVariable Integer roundNumber,  @Payload RoundMessage roundMessage) {
        GameRoomState room = gameRoomMemory.getRoom(code);

        room.getRoundStateMap().get(roundNumber).getPlayerAnswers().put(roundMessage.getNickname(), roundMessage.getAnswer());

        if (room.getRoundStateMap().get(roundNumber).getPlayerAnswers().size() == room.getPlayers().size()) {
            messagingTemplate.convertAndSend(
                    "/topic/room/" + code + "/vote",
                    "VOTE_STARTED"
            );
        }

    }

    @GetMapping("/{code}/{nickname}/round/{roundNumber}")
    public ResponseEntity<String> sendWord(@PathVariable String code,
                                         @PathVariable String nickname,
                                         @PathVariable Integer roundNumber) {
        GameRoomState room = gameRoomMemory.getRoom(code);

        String wordForPlayer = null;

        if (room.getRoundStateMap().containsKey(roundNumber)) {
            if (Objects.equals(room.getRoundStateMap().get(roundNumber).getImpostorNickname(), nickname)) {
                wordForPlayer = room.getRoundStateMap().get(roundNumber).getImpostorWord();
            } else {
                wordForPlayer = room.getRoundStateMap().get(roundNumber).getRealWord();
            }
        }
        return ResponseEntity.ok(wordForPlayer);
    }


    @Data
    @AllArgsConstructor
    public static class RoundMessage {
        private String answer;
        private String nickname;
    }
}
