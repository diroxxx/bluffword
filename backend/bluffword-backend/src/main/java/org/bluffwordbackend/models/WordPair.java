package org.bluffwordbackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "word_category_id")
    @NonNull
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private WordCategory category;

    public WordPair() {

    }
}
