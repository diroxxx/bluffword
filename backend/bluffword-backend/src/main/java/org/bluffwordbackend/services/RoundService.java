package org.bluffwordbackend.services;

import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.models.*;
import org.bluffwordbackend.repositories.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RoundService {

    private final RoundRepository roundRepository;
    private final GamePlayRepository gamePlayRepository;
    private final GameRoomRepository gameRoomRepository;
    private final WordPairRepository wordPairRepository;
    private final RoleRoundRepository roleRoundRepository;

    public Round startNewRound(Long gameRoomId, int roundNumber) {
        GameRoom room = gameRoomRepository.findById(gameRoomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // 1. Wylosuj WordPair
        List<WordPair> allPairs = wordPairRepository.findAll();
        WordPair chosen = allPairs.get(new Random().nextInt(allPairs.size()));

        // 2. Utwórz nową rundę
        Round round = new Round();
        round.setGameRoom(room);
        round.setWordPairs(chosen);
        round.setRoundNumber(roundNumber);
        round = roundRepository.save(round);

        // 3. Przypisz role graczom
        List<GamePlay> players = gamePlayRepository.findByGameRoom(room);
        List<Player> playerList = players.stream().map(GamePlay::getPlayer).toList();

        Collections.shuffle(playerList);
        Player impostor = playerList.get(0); // pierwszy = impostor

        for (Player p : playerList) {
            RoleRound role = new RoleRound();
            role.setRound(round);
            role.setPlayer(p);
            role.setRoleType(p.equals(impostor) ? RoleType.IMPOSTOR : RoleType.LOYAL);
            roleRoundRepository.save(role);
        }

        return round;
    }

}
