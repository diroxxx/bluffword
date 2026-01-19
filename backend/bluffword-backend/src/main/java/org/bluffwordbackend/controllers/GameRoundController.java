package org.bluffwordbackend.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bluffwordbackend.services.WordPairService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/round")
@CrossOrigin(origins = "http://localhost:5173")
public class GameRoundController {
    private final SimpMessagingTemplate messagingTemplate;
    private final WordPairService wordPairService;

}
