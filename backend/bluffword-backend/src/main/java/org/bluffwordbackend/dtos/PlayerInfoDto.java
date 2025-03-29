package org.bluffwordbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerInfoDto {
    private String nickname;
    private Boolean isImpostor;
    private Boolean isHost;
}
