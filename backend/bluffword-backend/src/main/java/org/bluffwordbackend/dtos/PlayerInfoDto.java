package org.bluffwordbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerInfoDto {
    private String nickname;
    private Boolean isImpostor;
    private Boolean isHost;
    private String sessionId;
}
