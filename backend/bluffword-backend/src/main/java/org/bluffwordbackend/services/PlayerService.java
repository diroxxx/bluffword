package org.bluffwordbackend.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.models.Player;
import org.bluffwordbackend.repositories.PlayerRepository;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;


    public List<Player> findAllPlayers() {
        return playerRepository.findAll();
    }


    @Transactional
    public Player createPlayer(String nickname) {
        var player = new Player();
        player.setNickname(nickname);
        return playerRepository.save(player);
    }

}
