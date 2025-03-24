package org.bluffwordbackend.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameSocketController {

    @MessageMapping("/clue")
    @SendTo("/topic/clues")
    public ClueMessage sendClue(ClueMessage clue) {
        return clue;
    }

    @Data
    @AllArgsConstructor
    public static class ClueMessage {
        private String clue;
        private String player;
    }
}
