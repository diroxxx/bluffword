package org.bluffwordbackend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int roundNumber;
    private String category;

    @OneToMany(mappedBy = "round")
    private List<WordPair> wordPairs = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "game_room_id")
    private GameRoom gameRoom;

    @OneToMany(mappedBy = "round")
    private List<Clue> clues = new ArrayList<>();

    @OneToMany(mappedBy = "round")
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "round")
    private List<RoleRound> roleRoundlist = new ArrayList<>() ;


}
