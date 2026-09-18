package at.htlleonding.pepper.dto;

import at.htlleonding.pepper.model.Move;

public record StepResponseDto(
        Long id,
        int index,
        ImageDto image,
        Move move,
        String text,
        int durationInSeconds
) {
}
