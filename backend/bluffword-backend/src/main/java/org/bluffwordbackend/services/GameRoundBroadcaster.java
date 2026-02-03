package org.bluffwordbackend.services;

import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.dtos.PlayerWordResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
@RequiredArgsConstructor
public class GameRoundBroadcaster {
    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastRoundWord(String roomCode, PlayerWordResponse word, Long playerId) {
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/player/" + playerId +"/round/word", word);
    }

    public void broadcastCurrentNumberOfRound(String roomCode, int roundNumber) {
        messagingTemplate.convertAndSend("/topic/room/" + roomCode +"/round/", roundNumber);
    }


}
