package org.bluffwordbackend.repositories;

import org.bluffwordbackend.models.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {
    public Optional<GameRoom> findByCode(String code);
}
