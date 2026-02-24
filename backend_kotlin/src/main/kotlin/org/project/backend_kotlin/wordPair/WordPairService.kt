package org.project.backend_kotlin.wordPair

import org.project.backend_kotlin.model.WordPairDb
import org.springframework.stereotype.Service

@Service
class WordPairService(
    private val wordPairRepository: WordPairRepository,
    private val categoryRepository: CategoryRepository
) {

    fun getAllWordPairs(): List<WordPairDb> {
        return wordPairRepository.findAll()
    }

    fun getAllCategories(): List<String> {
       return categoryRepository.findAll()
           .map { it.name }
           .toList()
    }

}