package org.bluffwordbackend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private LocalDateTime createdAt;
    private int round_total;
    private int score;
    private boolean isAlive;

    @Enumerated(EnumType.STRING)
    private GameMode gameMode;

    @OneToMany(mappedBy = "gameRoom")
    private List<GamePlay> gamePlays = new ArrayList<>();

    @OneToMany(mappedBy = "gameRoom")
    private List<Round> rounds = new ArrayList<>();

}
