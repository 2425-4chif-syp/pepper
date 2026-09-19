package at.htlleonding.pepper.dto;

import at.htlleonding.pepper.model.Game;
import at.htlleonding.pepper.model.Move;

/**
 * {@code imageId} references an already stored image (no copy is made); {@code image} is a
 * Base64 upload that gets stored as a new image. If both are empty the step has no image.
 */
public record StepDto(Game game, int index, Long imageId, String image, String image_desc, Move move, String text, int durationInSeconds) {
}
