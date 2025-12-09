package org.bluffwordbackend.models;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Entity
public class RoomPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private boolean isHost = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Player player;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_room_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private GameRoom gameRoom;


}
