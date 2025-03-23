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
public class Player {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nickname;
    private String email;
    private String password;

    @OneToMany
    private List<Clue> clues = new ArrayList<>();

    @OneToMany(mappedBy = "player")
    private List<GamePlay> gamePlays = new ArrayList<>();


}
