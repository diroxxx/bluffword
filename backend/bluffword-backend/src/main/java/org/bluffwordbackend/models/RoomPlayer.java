package org.bluffwordbackend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Entity
@Data
public class RoomPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Player player;

    @ManyToOne(optional = false)
    @JoinColumn(name = "game_room_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private GameRoom gameRoom;


}
