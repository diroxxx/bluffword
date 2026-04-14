package org.project.backend_kotlin.round.embeddingModel

import org.springframework.ai.ollama.OllamaEmbeddingModel
import org.springframework.stereotype.Service
import kotlin.math.sqrt

@Service
class EmbeddingService(
    private val ollamaEmbed: OllamaEmbeddingModel
) {

    fun embed(text: String): List<Float> = ollamaEmbed.embed(text).toList()

    fun cosineSimilarity(a: List<Float>, b: List<Float>): Float {
        var dotProduct = 0.0
        var normA = 0.0
        var normB = 0.0
        for (i in a.indices) {
            dotProduct += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }
        val denominator = sqrt(normA) * sqrt(normB)
        if (denominator == 0.0) return 0f
        return (dotProduct / denominator).toFloat()
    }
}