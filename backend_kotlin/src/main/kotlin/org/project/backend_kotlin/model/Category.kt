package org.project.backend_kotlin.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    val name: String,

    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL], orphanRemoval = true)
    val wordPairs: MutableList<WordPairDb> = mutableListOf()
) {
}