package org.bluffwordbackend.services;

import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.dtos.PlayerInfoDto;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class GameRoomBroadcaster {
    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastPlayers(String roomCode, List<PlayerInfoDto> players) {
        messagingTemplate.convertAndSend("/topic/room/players");
    }

}
