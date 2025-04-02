package org.bluffwordbackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class GameLoopManager {
    private final TaskScheduler taskScheduler;
    private final SimpMessagingTemplate messagingTemplate;
    private ScheduledFuture<?> currentRoundTask;



    public void startGame(String code) {
        System.out.println("🎮 Game started in room: " + code);
        startRound(code, 1);
    }

    private void startRound(String code, int roundNumber) {

        messagingTemplate.convertAndSend("/topic/room/" + code + "/game", "GAME_STARTED");



        currentRoundTask = taskScheduler.schedule(() -> {
            endRound(code, roundNumber);
        }, new java.util.Date(System.currentTimeMillis() + 30_000)); // 30 sec round
    }

    private void endRound(String code, int roundNumber) {
        messagingTemplate.convertAndSend(
                "/topic/room/" + code + "/game",
                "Round " + roundNumber + " has ended! Time to vote."
        );

        // Możesz teraz przejść do głosowania albo rozpocząć kolejną rundę:
        // startRound(code, roundNumber + 1);
    }

    public void stopGame() {
        if (currentRoundTask != null) {
            currentRoundTask.cancel(true);
        }
    }
}
