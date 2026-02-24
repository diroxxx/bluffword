package org.project.backend_kotlin.redisModels

data class WordPair(
    val id: Long,
    val impostorWord: String,
    val realWord: String
)
