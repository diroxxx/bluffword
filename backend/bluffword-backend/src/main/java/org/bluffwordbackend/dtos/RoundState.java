package org.bluffwordbackend.dtos;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class RoundState {

    private Boolean isStarted;
    private Integer currentRound;
    private String phase;
    private Integer time;
    private Map<String, String> votes = new HashMap<>();


}
