package org.bluffwordbackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@AllArgsConstructor
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
    @NotNull
    private GameRoomState state;

    @Enumerated(EnumType.STRING)
    @NotNull
    private GameMode gameMode;

    @Enumerated(EnumType.STRING)
    @NotNull
    private CategorySelectionMode categorySelectionMode;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "word_category_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private WordCategory staticCategory;

    @OneToMany(mappedBy = "gameRoom", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<RoomPlayer> players = new HashSet<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "player_id")
    private  Player host;

    public GameRoom() {

    }
}
