package org.bluffwordbackend.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bluffwordbackend.dtos.GameRoomState;
import org.bluffwordbackend.dtos.PlayerInfoDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameRoundManager {
    private final InMemoryGameRoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final TaskScheduler taskScheduler;

    private final Map<String, Integer> roundMap = new ConcurrentHashMap<>();
    private final Map<String, String> currentPhase = new ConcurrentHashMap<>();

    public void startGame(String code) {
        GameRoomState room = roomService.getRoom(code);
        if (room == null || room.getPlayers().size() < 2) return;

        log.info("🎲 Game started in room {}", code);
        roundMap.put(code, 1);
        currentPhase.put(code, "GAME");
        room.setIsStarted(true);

        broadcastState(code);

        scheduleNextPhase(code);
    }

    private void scheduleNextPhase(String code) {
        String phase = currentPhase.get(code);
        if (phase == null) return;

        long delay = switch (phase) {
            case "GAME" -> 30_000;
            case "VOTE" -> 15_000;
            default -> 0;
        };

        taskScheduler.schedule(() -> {
            if (phase.equals("GAME")) {
                currentPhase.put(code, "VOTE");
                broadcastState(code);
                scheduleNextPhase(code);
            } else if (phase.equals("VOTE")) {
                handleVoteResults(code);
                checkWinCondition(code);

                int current = roundMap.getOrDefault(code, 1);
                roundMap.put(code, current + 1);
                currentPhase.put(code, "GAME");
                broadcastState(code);
                scheduleNextPhase(code);
            }
        }, Instant.now().plusMillis(delay));
    }

    private void handleVoteResults(String code) {
        GameRoomState room = roomService.getRoom(code);
        if (room == null) return;

//        Map<String, Long> voteCounts = room.getPlayers().stream()
//                .filter(p -> p.getVote() != null)
//                .collect(Collectors.groupingBy(PlayerInfoDto::getVote, Collectors.counting()));
//
//        Optional<String> mostVoted = voteCounts.entrySet().stream()
//                .max(Map.Entry.comparingByValue())
//                .map(Map.Entry::getKey);
//
//        mostVoted.ifPresent(target -> {
//            room.getPlayers().removeIf(p -> p.getNickname().equals(target));
//            log.info("💔 Player {} was eliminated", target);
//        });
    }

    private void checkWinCondition(String code) {
        GameRoomState room = roomService.getRoom(code);
        if (room == null) return;

        boolean impostorAlive = room.getPlayers().stream().anyMatch(PlayerInfoDto::getIsImpostor);
        int remaining = room.getPlayers().size();

        if (!impostorAlive || remaining <= 2) {
            room.setIsStarted(false);
            log.info("🏆 Game over in room {}", code);
            messagingTemplate.convertAndSend("/topic/room/" + code + "/gameover", room.getPlayers());
        }
    }

    private void broadcastState(String code) {
        GameRoomState room = roomService.getRoom(code);
        if (room != null) {
            messagingTemplate.convertAndSend("/topic/room/" + code + "/state", Map.of(
                    "phase", currentPhase.get(code),
                    "round", roundMap.getOrDefault(code, 1),
                    "players", room.getPlayers()
            ));
        }
    }
}
