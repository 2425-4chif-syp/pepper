package at.htlleonding.pepper.dto;

import at.htlleonding.pepper.model.Game;
import at.htlleonding.pepper.model.Move;

public record StepDto(Game game, int index, String image, String image_desc, Move move, String text, int durationInSeconds) {
}