package org.bluffwordbackend.repositories;

import org.bluffwordbackend.models.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {
}
