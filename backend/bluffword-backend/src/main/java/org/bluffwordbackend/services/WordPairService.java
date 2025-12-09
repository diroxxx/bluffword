package org.bluffwordbackend.services;

import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.repositories.WordPairRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WordPairService {
    private final WordPairRepository wordPairRepository;

    public List<WordPair> getWordPairs() {
        return wordPairRepository.findAll();
    }



}
