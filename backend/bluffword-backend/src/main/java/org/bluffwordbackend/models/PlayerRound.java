package org.bluffwordbackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class PlayerRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String answer;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @ManyToOne(optional = false)
    @JoinColumn(name = "round_id")
    private Round round;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id")
    private Player player;

}
