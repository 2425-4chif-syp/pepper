package at.htlleonding.pepper.dto;

import java.time.LocalDateTime;

public record GameScoreDto(
        String comment,
        LocalDateTime dateTime,
        int elapsedTime,
        int score,
        IdDto game,
        IdDto person
) {
}
