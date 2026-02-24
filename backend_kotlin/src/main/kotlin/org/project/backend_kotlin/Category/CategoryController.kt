package org.project.backend_kotlin.Category

import org.project.backend_kotlin.wordPair.CategoryRepository
import org.project.backend_kotlin.wordPair.WordPairService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/categories")
@RestController
class CategoryController(
    private val wordPairService: WordPairService
) {


    @GetMapping()
    fun getAllCategories(): ResponseEntity<List<String>> {
        return  ResponseEntity.ok(wordPairService.getAllCategories())
    }
}