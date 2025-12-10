package org.bluffwordbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bluffwordbackend.models.Player;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerInfoDto {
    private String nickname;
//    private Boolean isImpostor;
    private Boolean isHost;
    private String sessionId;


    public static   PlayerInfoDto toDto(Player player) {
        PlayerInfoDto dto = new PlayerInfoDto();
        dto.setNickname(player.getNickname());
        return dto;
    }
}
