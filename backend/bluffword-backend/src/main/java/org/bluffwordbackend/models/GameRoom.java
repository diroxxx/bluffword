package org.bluffwordbackend.models;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
public class GameRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;
    private int roundTotal;
    private int maxPlayers;

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "player_id")
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private Player player;

}
