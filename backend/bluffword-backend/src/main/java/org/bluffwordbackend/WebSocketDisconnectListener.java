package org.bluffwordbackend;

import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.dtos.GameRoomState;
import org.bluffwordbackend.services.InMemoryGameRoomService;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {
    private final InMemoryGameRoomService gameRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        System.out.println("Gracz rozłączył się: " + sessionId);

//        for (GameRoomState room : gameRoomService.getAllRooms()) {
//            boolean removed = room.getPlayers().removeIf(p -> sessionId.equals(p.getSessionId()));
//
//            if (removed) {
//                System.out.println("Usunięto gracza z pokoju " + room.getCode());
//
//                messagingTemplate.convertAndSend(
//                        "/topic/room/" + room.getCode() + "/players",
//                        room.getPlayers()
//                );
//            }
//        }
    }
}
