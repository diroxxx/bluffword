package org.bluffwordbackend.repositories;

import org.bluffwordbackend.models.RoomPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface RoomPlayerRepository extends JpaRepository<RoomPlayer, Long> {
}
