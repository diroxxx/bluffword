package org.bluffwordbackend.dtos;

public record PlayerWordResponse(
        String word,
        boolean isImposter
) {
}
