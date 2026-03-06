package org.project.backend_kotlin.wordPair

import org.project.backend_kotlin.model.WordPairDb
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface WordPairRepository : JpaRepository<WordPairDb, Long> {


    @Query("""
    SELECT w FROM WordPairDb w
    WHERE w.category.name = :category
    ORDER BY FUNCTION('RANDOM')
    """)
    fun findRandomWordPairs(
        @Param("category") category: String,
        pageable: Pageable
    ): List<WordPairDb>

    @Query("""
    SELECT w FROM WordPairDb w
    WHERE w.category.name = :category AND w.id NOT IN (:wordPairsIds)
    ORDER BY FUNCTION('RANDOM')
    """)
    fun findRandomUnusedWordPairs(
        @Param("category") category: String,
        @Param("wordPairsIds") wordPairsIds: List<Long>,
        pageable: Pageable
    ): List<WordPairDb>
}