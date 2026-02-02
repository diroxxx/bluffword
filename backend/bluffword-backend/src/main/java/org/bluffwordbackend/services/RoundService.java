package org.bluffwordbackend.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.dtos.PlayerWordResponse;
import org.bluffwordbackend.models.*;
import org.bluffwordbackend.repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RoundService {


    private final GameRoomRepository gameRoomRepository;
    private final PlayerRepository playerRepository;
    private final RoundTimerService roundTimerService;
    private final GameRoundBroadcaster gameRoundBroadcaster;
    private final GameRoomBroadcaster gameRoomBroadcaster;
    private final RoundRepository roundRepository;
    private final WordPairService roundService;

    public Optional<WordPairDto> getWordPair(String roomCode, String wordCategory) {

        Optional<GameRoom> gameRoomByCode = gameRoomRepository.findGameRoomByCode(roomCode);

        if (gameRoomByCode.isEmpty()) {
            return Optional.empty();
        }

        List<WordPair> allWordPairsUnused = gameRoomRepository.findAllWordPairsUnused(gameRoomByCode.get().getId(), wordCategory);
        WordPair wordPair = allWordPairsUnused.get(new Random().nextInt(allWordPairsUnused.size()));
        return Optional.of(new WordPairDto(wordPair.getRealWord(), wordPair.getImpostorWord()));

    }

    private final ScheduledExecutorService debugScheduler = Executors.newSingleThreadScheduledExecutor();

    @Transactional
    public void startOrSendRoundWords(String roomCode, Long requesterPlayerId) {
        GameRoom room = gameRoomRepository.findGameRoomByCode(roomCode).orElseThrow();

        gameRoomBroadcaster.broadcastGameRoomState(roomCode, GameRoomState.ANSWERING);
        

        if (room.getCategorySelectionMode() != CategorySelectionMode.FIXED) {
            return; // albo obsłuż inne tryby
        }

        var players = playerRepository.findPlayersByRoomCode(roomCode);
        if (players.isEmpty()) return;

        var wordPairOpt = getWordPair(roomCode, room.getStaticCategory().getName());
        if (wordPairOpt.isEmpty()) return;

        int impostorIndex = ThreadLocalRandom.current().nextInt(players.size());
        Player impostorPlayer = players.get(impostorIndex);

        PlayerWordResponse impostorWord = new PlayerWordResponse(wordPairOpt.get().getImpostorWord(), true);
        PlayerWordResponse realWord = new PlayerWordResponse(wordPairOpt.get().getRealWord(), false);

//        for (Player p : players) {
//            boolean isImpostor = p.getId().equals(impostorPlayer.getId());
//            System.out.println(p + " " +"isImpostor: " + isImpostor + "--" + (isImpostor ? impostorWord : realWord));
//            gameRoundBroadcaster.broadcastRoundWord(roomCode, isImpostor ? impostorWord : realWord, p.getId());
//        }
//            roundTimerService.startRoundTimer(roomCode, 20);

        debugScheduler.schedule(() -> {
            for (Player p : players) {
                boolean isImpostor = p.getId().equals(impostorPlayer.getId());
                gameRoundBroadcaster.broadcastRoundWord(roomCode, isImpostor ? impostorWord : realWord, p.getId());
            }
            roundTimerService.startRoundTimer(roomCode, 20);
        }, 900, TimeUnit.MILLISECONDS);

    }


    public int getRandomNumber(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }





}
