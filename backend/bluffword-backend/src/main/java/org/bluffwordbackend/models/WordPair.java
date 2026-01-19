package org.bluffwordbackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class WordPair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String realWord;
    @NotNull
    private String impostorWord;

    @OneToMany(mappedBy = "wordPair")
    private Set<Round> rounds = new HashSet<>();

}
