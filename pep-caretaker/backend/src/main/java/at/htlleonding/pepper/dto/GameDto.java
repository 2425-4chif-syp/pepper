package at.htlleonding.pepper.dto;

import at.htlleonding.pepper.model.GameType;

/** {@code iconId} references an already stored image; {@code icon} is a Base64 upload stored as a new image. */
public record GameDto(String name, String icon, Long iconId, GameType gameType, Boolean isEnabled) {
}
