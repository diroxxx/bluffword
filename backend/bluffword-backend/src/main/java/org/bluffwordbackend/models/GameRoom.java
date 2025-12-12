package org.bluffwordbackend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class GameRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;
    private int roundTotal;
    private int currentRound;
    private int maxPlayers;
    private int roundTimeSeconds;

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "player_id")
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private Player player;

    @OneToMany(mappedBy = "gameRoom", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<RoomPlayer> players = new HashSet<>();

}
