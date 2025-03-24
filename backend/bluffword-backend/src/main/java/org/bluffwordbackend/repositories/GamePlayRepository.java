package org.bluffwordbackend.repositories;

import org.bluffwordbackend.models.GamePlay;
import org.bluffwordbackend.models.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GamePlayRepository extends JpaRepository<GamePlay, Long> {
    List<GamePlay> findByGameRoom(GameRoom room);

}
