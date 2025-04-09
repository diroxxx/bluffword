package org.bluffwordbackend.dtos;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class RoundState {

    private String realWord;
    private String impostorWord;
    private String impostorNickname;
    private Map<String, String> playerAnswers = new HashMap<>();
    private Map<String, List<String>> votes = new HashMap<>();

    public RoundState(String realWord, String impostorWord) {
        this.realWord = realWord;
        this.impostorWord = impostorWord;
    }

    public RoundState(String realWord, String impostorWord, String impostorNickname) {
        this.realWord = realWord;
        this.impostorWord = impostorWord;
        this.impostorNickname = impostorNickname;
    }
}
