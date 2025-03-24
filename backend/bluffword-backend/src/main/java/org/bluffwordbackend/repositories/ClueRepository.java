package org.bluffwordbackend.repositories;

import org.bluffwordbackend.models.Clue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClueRepository extends JpaRepository<Clue, Long> {
}
