package org.bluffwordbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WordPairRepository extends JpaRepository<WordPair, Long> {
}
