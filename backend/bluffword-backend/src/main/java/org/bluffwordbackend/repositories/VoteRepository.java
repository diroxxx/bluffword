package org.bluffwordbackend.repositories;

import org.bluffwordbackend.models.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository  extends JpaRepository<Vote, Long> {
}
