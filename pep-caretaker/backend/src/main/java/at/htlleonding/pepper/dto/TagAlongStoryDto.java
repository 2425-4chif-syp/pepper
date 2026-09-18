package at.htlleonding.pepper.dto;

import at.htlleonding.pepper.model.GameType;

public record TagAlongStoryDto(
        Long id,
        String name,
        ImageDto storyIcon,
        boolean isEnabled,
        GameType gameType
) {
}
