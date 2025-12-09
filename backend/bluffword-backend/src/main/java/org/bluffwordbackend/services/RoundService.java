package org.bluffwordbackend.services;

import lombok.RequiredArgsConstructor;
import org.bluffwordbackend.models.*;
import org.bluffwordbackend.repositories.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RoundService {
    private final RoundRepository roundRepository;
    private final GameRoomRepository gameRoomRepository;
    private final WordPairRepository wordPairRepository;
    private final RoleRoundRepository roleRoundRepository;



}
