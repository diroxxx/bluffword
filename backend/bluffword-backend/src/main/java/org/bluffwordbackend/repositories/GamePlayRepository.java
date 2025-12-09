package org.bluffwordbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GamePlayRepository extends JpaRepository<Gameplay, Long> {
    List<Gameplay> findByGameRoom(GameRoom room);

}
