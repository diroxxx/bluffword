package org.bluffwordbackend.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "round_id")
    private Round round;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id")
    private Player target;

    @ManyToOne(optional = false)
    @JoinColumn(name = "voter_id")
    private Player voter;


}
