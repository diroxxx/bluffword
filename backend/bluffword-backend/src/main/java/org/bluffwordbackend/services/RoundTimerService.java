package org.bluffwordbackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class RoundTimerService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Map<String, ScheduledFuture<?>> runningTimers = new ConcurrentHashMap<>();

    public void startRoundTimer(String roomCode, int seconds) {
        cancelTimer(roomCode);

        final long endNanos = System.nanoTime() + TimeUnit.SECONDS.toNanos(seconds);

        simpMessagingTemplate.convertAndSend("/topic/round/" + roomCode + "/time", (long) seconds);

        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(() -> {
            long remainingNanos = endNanos - System.nanoTime();
            long remainingSeconds = Math.max(0, TimeUnit.NANOSECONDS.toSeconds(remainingNanos));

            simpMessagingTemplate.convertAndSend("/topic/round/" + roomCode + "/time", remainingSeconds);

            if (remainingSeconds <= 0) {
                cancelTimer(roomCode);
            }
        }, 1, 1, TimeUnit.SECONDS);

        runningTimers.put(roomCode, task);
    }

    public void cancelTimer(String roomCode) {
        ScheduledFuture<?> existing = runningTimers.remove(roomCode);
        if (existing != null) {
            existing.cancel(false);
        }
    }

}
