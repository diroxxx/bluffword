package org.bluffwordbackend.repositories;

import org.bluffwordbackend.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
