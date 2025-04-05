package org.bluffwordbackend.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bluffwordbackend.dtos.GameRoomState;
import org.bluffwordbackend.dtos.PlayerInfoDto;
import org.bluffwordbackend.services.InMemoryGameRoomService;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {
    private final InMemoryGameRoomService gameRoomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, ScheduledFuture<?>> disconnectTimers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);



    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        log.info("Player disconnected: {}", sessionId);

        for (GameRoomState room : gameRoomService.getAllRooms()) {
            Optional<PlayerInfoDto> playerOpt = room.getPlayers().stream()
                    .filter(p -> sessionId.equals(p.getSessionId()))
                    .findFirst();

            playerOpt.ifPresent(player -> {
                ScheduledFuture<?> future = scheduler.schedule(() -> {
                    room.getPlayers().removeIf(p -> p.getNickname().equals(player.getNickname()));
                    messagingTemplate.convertAndSend(
                            "/topic/room/" + room.getCode() + "/players",
                            room.getPlayers()
                    );
                }, 3, TimeUnit.SECONDS);

                disconnectTimers.put(player.getNickname(), future);
            });
        }
    }
public void cancelDisconnectTimer(String nickname) {
    ScheduledFuture<?> future = disconnectTimers.remove(nickname);
    if (future != null) {
        future.cancel(true);
    }
}
}