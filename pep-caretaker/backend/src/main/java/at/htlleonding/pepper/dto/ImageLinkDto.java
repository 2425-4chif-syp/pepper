package at.htlleonding.pepper.dto;

import at.htlleonding.pepper.model.Person;

public record ImageLinkDto(
        Long id,
        String description,
        String href,
        Person person
) {
}
