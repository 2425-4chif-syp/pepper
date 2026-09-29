package at.htlleonding.pepper.service;

import at.htlleonding.pepper.dto.ImageContentDto;
import at.htlleonding.pepper.dto.ImageDto;
import at.htlleonding.pepper.dto.ImageLinkDto;
import at.htlleonding.pepper.model.Image;
import at.htlleonding.pepper.model.Person;
import at.htlleonding.pepper.model.Game;
import at.htlleonding.pepper.repository.GameRepository;
import at.htlleonding.pepper.repository.ImageRepository;
import at.htlleonding.pepper.repository.PersonRepository;
import at.htlleonding.pepper.repository.StepRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.function.Function;

@ApplicationScoped
public class ImageService {

    @Inject
    ImageRepository imageRepository;

    @Inject
    PersonRepository personRepository;

    @Inject
    MinioService minioService;

    @Inject
    StepRepository stepRepository;

    @Inject
    GameRepository gameRepository;

    public record Picture(byte[] bytes, String contentType, String fileName) {
    }

    public List<ImageContentDto> getAll() {
        return imageRepository.listAll().stream().map(this::toContentDto).toList();
    }

    public ImageContentDto getById(Long id) {
        Image image = imageRepository.findById(id);
        if (image == null) {
            throw new NotFoundException("Image " + id + " not found");
        }
        return toContentDto(image);
    }

    public List<ImageContentDto> getByPersonId(Long personId) {
        return imageRepository.find("person.id", personId).list().stream()
                .map(this::toContentDto)
                .toList();
    }

    public List<Image> listAllEntities() {
        return imageRepository.listAll();
    }

    public List<ImageLinkDto> getPictureLinks(Function<Long, String> hrefBuilder) {
        return imageRepository.listAll().stream()
                .map(image -> new ImageLinkDto(
                        image.getId(),
                        image.getDescription(),
                        hrefBuilder.apply(image.getId()),
                        image.getPerson()
                ))
                .toList();
    }

    public Picture getPicture(Long id) {
        Image image = imageRepository.findById(id);
        if (image == null) {
            throw new NotFoundException("Image " + id + " not found");
        }
        return toPicture(image);
    }

    public Picture toPicture(Image image) {
        if (image.getObjectKey() == null) {
            return null;
        }
        byte[] bytes = minioService.get(image.getObjectKey());
        String mime = image.getContentType() != null ? image.getContentType() : MinioService.detectMime(bytes);
        return new Picture(bytes, mime, "image-" + image.getId() + MinioService.extFromMime(mime));
    }

    @Transactional
    public ImageContentDto create(ImageContentDto imageDto) {
        if (imageDto.base64Image() == null) {
            throw new BadRequestException("base64Image is required");
        }
        Person person = imageDto.personId() != null ? personRepository.findById(imageDto.personId()) : null;
        if (imageDto.personId() != null && person == null) {
            throw new NotFoundException("Person " + imageDto.personId() + " not found");
        }
        Image image = storeImage(imageDto.base64Image(), person, imageDto.imageUrl(), imageDto.description(), "person");
        return toContentDto(image);
    }

    private static final java.util.Set<String> ALLOWED_IMAGE_TYPES =
            java.util.Set.of("image/png", "image/jpeg", "image/gif", "image/webp");

    /**
     * Decodes the (optionally data-URI-prefixed) Base64 payload, uploads it to MinIO
     * and persists the metadata row.
     */
    @Transactional
    public Image storeImage(String base64, Person person, String url, String description, String keyPrefix) {
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(at.htlleonding.pepper.util.Base64Util.extractBase64String(base64));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("base64Image is not valid Base64", e);
        }
        String contentType = MinioService.detectMime(bytes);
        // detectMime faellt auf application/octet-stream zurueck, wenn keine der
        // Bild-Signaturen passt. Ohne diese Pruefung landet jede beliebige Datei
        // im Bucket - storeImage ist der einzige Upload-Pfad (Personenbilder und
        // Mitmachgeschichten), die Pruefung gehoert also genau hierher.
        if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BadRequestException("Nur PNG, JPEG, GIF oder WebP werden akzeptiert");
        }
        String prefix = person != null ? keyPrefix + "/" + person.getId() : keyPrefix;
        String objectKey = minioService.put(prefix, bytes, contentType);
        Image image = new Image(person, objectKey, contentType, url, description);
        imageRepository.persist(image);
        return image;
    }

    @Transactional
    public void delete(Long id) {
        Image image = imageRepository.findById(id);
        if (image == null) {
            throw new NotFoundException("Image " + id + " not found");
        }
        List<String> stories = storiesUsing(image);
        if (!stories.isEmpty()) {
            throw new ClientErrorException(
                    "Das Bild wird noch in folgenden Mitmachgeschichten verwendet: " + String.join(", ", stories),
                    409);
        }
        deleteImage(image);
    }

    /** Names of the stories whose title or one of whose scenes shows this image. */
    public List<String> storiesUsing(Image image) {
        Stream<Game> asIcon = gameRepository.list("storyIcon", image).stream();
        Stream<Game> inSteps = stepRepository.list("image", image).stream().map(step -> step.getGame());
        return Stream.concat(asIcon, inSteps)
                .filter(game -> game != null)
                .map(Game::getName)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Deletes an image that was uploaded for a story (object key starts with {@code ownedKeyPrefix}) once
     * nothing references it anymore. Library images that stories only reference are kept.
     */
    @Transactional
    public void deleteIfOwnedAndUnused(Image image, String ownedKeyPrefix) {
        if (image == null || image.getObjectKey() == null || !image.getObjectKey().startsWith(ownedKeyPrefix)) {
            return;
        }
        stepRepository.flush();
        if (storiesUsing(image).isEmpty()) {
            deleteImage(image);
        }
    }

    @Transactional
    public void deleteImage(Image image) {
        String objectKey = image.getObjectKey();
        imageRepository.delete(image);
        minioService.delete(objectKey);
    }

    @Transactional
    public long deleteAllForPerson(Long personId) {
        List<Image> images = imageRepository.find("person.id", personId).list();
        images.forEach(this::deleteImage);
        return images.size();
    }

    public ImageContentDto toContentDto(Image image) {
        String base64 = null;
        if (image.getObjectKey() != null) {
            base64 = Base64.getEncoder().encodeToString(minioService.get(image.getObjectKey()));
        }
        return new ImageContentDto(
                image.getId(),
                image.getPerson() != null ? image.getPerson().getId() : null,
                image.getPerson(),
                base64,
                image.getUrl(),
                image.getDescription()
        );
    }

    public ImageDto toMetadataDto(Image image) {
        if (image == null) {
            return null;
        }
        return new ImageDto(image.getId(), image.getUrl(), image.getDescription());
    }
}
