package org.bluffwordbackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RoundTimerService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void startRoundTimer(String roomCode, int roundNumber, int seconds, Long playerId) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (seconds * 1000L);

        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(() -> {
            long remainingTime = (endTime - System.currentTimeMillis()) / 1000L;

            if (remainingTime <= 0) {
                simpMessagingTemplate.convertAndSend("/topic/round/" + roomCode + "/" + roundNumber + "/player/" + playerId + "/time", remainingTime);
            } else {
                simpMessagingTemplate.convertAndSend("/topic/round/" + roomCode + "/" + roundNumber + "/player/" + playerId + "/time", remainingTime);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

}
