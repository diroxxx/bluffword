package org.bluffwordbackend.dtos;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class RoundState {

    private String answer;
    private Map<String, List<String>> votes = new HashMap<>();
}
