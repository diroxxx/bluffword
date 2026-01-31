package org.bluffwordbackend.services;

import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.models.GameRoomState;
import org.bluffwordbackend.redisDtos.PlayerDto;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class GameRoomBroadcaster {
    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastPlayers(String roomCode, List<PlayerDto> players) {
        messagingTemplate.convertAndSend("/topic/room/" + roomCode +"/players", players);
    }

    public void broadcastGameRoomState(String roomCode, GameRoomState state) {
        messagingTemplate.convertAndSend("/topic/room/" + roomCode +"/state", state);
    }


}
