package org.bluffwordbackend.redisDtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class RoundState {


    private String realWord;
    private String impostorWord;
    private List<String> impostorNickname;
    private Map<String, String> playerAnswers;
    private Map<String, List<String>> votes;

}
