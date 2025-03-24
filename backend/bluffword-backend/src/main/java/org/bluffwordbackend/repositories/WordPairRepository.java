package org.bluffwordbackend.repositories;

import org.bluffwordbackend.models.WordPair;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordPairRepository extends JpaRepository<WordPair, Long> {
}
