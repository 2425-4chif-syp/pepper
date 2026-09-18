package at.htlleonding.pepper.dto;

import at.htlleonding.pepper.model.GameType;

public record GameDto(String name, String icon, GameType gameType, Boolean isEnabled) {
}
