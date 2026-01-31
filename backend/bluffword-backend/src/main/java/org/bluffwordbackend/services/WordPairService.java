package org.bluffwordbackend.services;

import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.models.WordCategory;
import org.bluffwordbackend.repositories.WordCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WordPairService {

    private final WordCategoryRepository wordCategoryRepository;


    public List<String> getWordCategories() {
        return wordCategoryRepository.findAll()
                .stream()
                .map(WordCategory::getName)
                .toList();
    }






}
