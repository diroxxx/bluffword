package org.bluffwordbackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;

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

    private int minPlayers;

    private int timeLimitAnswer;

    private int timeLimitVote;

    @Enumerated(EnumType.STRING)
    private GameMode mode;

    @OneToMany(mappedBy = "gameRoom", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<RoomPlayer> players = new HashSet<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "player_id")
    private  Player host;

}
