package org.bluffwordbackend.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WordPairDto {

    private String realWord;
    private String impostorWord;

}
