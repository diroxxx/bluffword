package org.bluffwordbackend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private Long id;

    private String nickname;

    @OneToMany(mappedBy = "player")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<RoomPlayer> roomPlayers = new HashSet<>();

    @OneToMany(mappedBy = "host")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<GameRoom> gameRooms = new HashSet<>();

    @OneToMany(mappedBy = "player")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<PlayerRound> playerRounds = new HashSet<>();

    @OneToMany(mappedBy = "voter")
    @EqualsAndHashCode.Exclude
    private Set<Vote> voterVotes = new HashSet<>();

    @OneToMany(mappedBy = "target")
    @EqualsAndHashCode.Exclude
    private Set<Vote> targetVotes = new HashSet<>();
}
