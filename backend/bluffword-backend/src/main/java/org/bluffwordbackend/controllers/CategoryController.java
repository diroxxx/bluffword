package org.bluffwordbackend.controllers;

import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.services.WordPairService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final WordPairService wordPairService;

    @GetMapping
    public ResponseEntity<List<String>> getAllWordCategories() {

        return ResponseEntity.ok(wordPairService.getWordCategories());
    }
}
