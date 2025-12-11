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
//    private String id;

    private String nickname;


    @OneToMany(mappedBy = "player")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<RoomPlayer> roomPlayers = new HashSet<>();

}
