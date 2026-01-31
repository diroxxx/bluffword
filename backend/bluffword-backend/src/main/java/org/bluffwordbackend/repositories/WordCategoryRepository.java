package org.bluffwordbackend.repositories;

import org.bluffwordbackend.models.WordCategory;
import org.bluffwordbackend.models.WordPair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WordCategoryRepository extends JpaRepository <WordCategory, Long>{

    @Query(value = "SELECT * FROM word_pair WHERE category = ?1 ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<WordPair> findRandomByCategory(String category);


    long countByName(String name);

    Page<WordCategory> findByName(String name, Pageable pageable);


    Optional<WordCategory> findByName(String name);


}
