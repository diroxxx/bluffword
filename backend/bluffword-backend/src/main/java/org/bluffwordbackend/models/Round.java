package org.bluffwordbackend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int roundNumber;
    private String category;

    @ManyToOne(optional = false)
    @JoinColumn(name = "game_room_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GameRoom gameRoom;

    @OneToMany(mappedBy = "round")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<PlayerRound> playerRounds = new HashSet<>();

    @OneToMany(mappedBy = "round")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Vote> votes = new HashSet<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "word_pair_id")
    private WordPair wordPair;

}
