package at.htlleonding.pepper.dto;

import at.htlleonding.pepper.model.Person;

/**
 * Image transfer object carrying the binary content as Base64.
 * Used for upload (POST /image) and full-content reads (GET /image/{id}).
 */
public record ImageContentDto(
        Long id,
        Long personId,
        Person person,
        String base64Image,
        String imageUrl,
        String description
) {
    public ImageContentDto(Long id, Long personId, String base64Image, String imageUrl, String description) {
        this(id, personId, null, base64Image, imageUrl, description);
    }
}
