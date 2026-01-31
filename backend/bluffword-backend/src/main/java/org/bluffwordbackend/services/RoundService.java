package org.bluffwordbackend.services;

import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.models.*;
import org.bluffwordbackend.repositories.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RoundService {


    private final GameRoomRepository gameRoomRepository;

    private boolean isWordPairUsed(String realWord,
                                   String impostorWord,
                                   Long gameRoomId) {

        return gameRoomRepository.isWordPairUsed(realWord, impostorWord, gameRoomId);

    }


    public Optional<WordPairDto> getWordPair(String roomCode, String wordCategory) {

        Optional<GameRoom> gameRoomByCode = gameRoomRepository.findGameRoomByCode(roomCode);

        if (gameRoomByCode.isEmpty()) {
            return Optional.empty();
        }

        List<WordPair> allWordPairsUnused = gameRoomRepository.findAllWordPairsUnused(gameRoomByCode.get().getId(), wordCategory);
        WordPair wordPair = allWordPairsUnused.get(new Random().nextInt(allWordPairsUnused.size()));
        return Optional.of(new WordPairDto(wordPair.getRealWord(), wordPair.getImpostorWord()));


    }








}
