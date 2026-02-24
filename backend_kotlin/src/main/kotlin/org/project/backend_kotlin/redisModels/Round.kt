package org.project.backend_kotlin.redisModels

data class Round(
    val roundNumber: Int,
    val category: String,
    //key: playerId, value: answer
    val listOfAnswers: MutableMap<String, String>,
    val wordPair: WordPair,
    //key: voterId, value: targetId
    val votes: MutableMap<String, String>
)
