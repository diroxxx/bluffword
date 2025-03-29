package org.bluffwordbackend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bluffwordbackend.dtos.GameRoomState;
import org.bluffwordbackend.services.InMemoryGameRoomService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {


    private final InMemoryGameRoomService gameRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        System.out.println("WebSocket disconnected. Session ID: " + sessionId);

        GameRoomState room = gameRoomService.removePlayerBySession(sessionId);
        if (room != null) {
            messagingTemplate.convertAndSend(
                    "/topic/room/" + room.getCode() + "/players",
                    room.getPlayers()
            );
        }
    }

}
