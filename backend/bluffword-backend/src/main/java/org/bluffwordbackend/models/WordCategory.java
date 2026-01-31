package org.bluffwordbackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Entity
@Data
public class WordCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "category")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<WordPair> wordPairs;


    @OneToMany(mappedBy = "staticCategory")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<GameRoom> gameRooms;
}
