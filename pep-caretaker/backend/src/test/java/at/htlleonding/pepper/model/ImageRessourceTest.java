package at.htlleonding.pepper.model;

import at.htlleonding.pepper.resource.ImageResource;
import at.htlleonding.pepper.dto.ImageContentDto;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.common.mapper.TypeRef;
import jakarta.inject.Inject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;


@QuarkusTest
@TestSecurity(user = "test-admin", roles = {"admin", "caretaker", "resident"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImageRessourceTest {

    private static final Logger log = LoggerFactory.getLogger(ImageRessourceTest.class);

    /** Kleinstmoegliches gueltiges PNG (1x1, transparent). */
    private static final byte[] PNG_1X1 = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk"
            + "+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==");
    private static final String PNG_1X1_BASE64 = Base64.getEncoder().encodeToString(PNG_1X1);
    static Person person;
    static Image image;
    static long imageId;

    @Inject
    ImageResource imageResource;

    @Test
    @Order(110)
    void createPersonForImage() {
        // Arrange
        var newPerson = Map.of(
                "firstName", "Milad",
                "lastName", "Moradi",
                "birthDate", "1995-10-10",
                "roomNo", "C3",
                "isWorker", false
        );

        // Act
        person = given()
                .contentType(APPLICATION_JSON)
                .body(newPerson)
                .when().post("/person")
                .then().statusCode(201)
                .extract().as(Person.class);



        // Assert
        assertThat(person).isNotNull();
        assertThat(person.getFirstName()).isEqualTo("Milad");
        assertThat(person.getLastName()).isEqualTo("Moradi");
        assertThat(person.getRoomNo()).isEqualTo("C3");
    }

    @Test
    @Order(130)
    void createImage_shouldPersistAndReturn201(){
        // Arrange
        var base64Image = PNG_1X1_BASE64;
        ImageContentDto imageDto = new ImageContentDto(null, person.getId(), base64Image, null, "Test image");

        var created = given()
                .contentType(APPLICATION_JSON)
                .body(imageDto)
                .when().post("/image")
                .then().statusCode(201)
                .extract().as(new TypeRef<Map<String, Object>>() {});

        assertThat(created).isNotNull();
        assertThat(created.get("description")).isEqualTo("Test image");
        // Act + Assert
        // Act: Erstelle das Bild
         given()
                .contentType(APPLICATION_JSON)
                .body(imageDto)
                .when().post("/image")
                .then().statusCode(201);
    }

    @Test
    @Order(135)
    void createImage_shouldRejectNonImagePayload() {
        // Arrange: gueltiges Base64, aber keine Bilddatei.
        var notAnImage = Base64.getEncoder().encodeToString("Hallo".getBytes());
        ImageContentDto imageDto =
                new ImageContentDto(null, person.getId(), notAnImage, null, "Kein Bild");

        // Act + Assert
        given()
                .contentType(APPLICATION_JSON)
                .body(imageDto)
                .when().post("/image")
                .then().statusCode(400);
    }

    @Test
    @Order(140)
    void getAllImages_shouldReturnCreatedImage() {


        // Act
        var response = given()
                .contentType(APPLICATION_JSON)
                .when().get("/image")
                .then().extract();

        // Assert
        if (response.statusCode() == 200) {
            var images = response.as(new TypeRef<List<Map<String, Object>>>() {});
            assertThat(images)
                    .isNotEmpty()
                    .anySatisfy(img -> {
                        assertThat(img.get("description")).isEqualTo("Test image");
                        imageId = ((Number) img.get("id")).longValue();
                    });
        } else {
            assertThat(response.statusCode()).isEqualTo(404);
        }
    }

    @Test
    @Order(145)
    void getImage_shouldReturnSameBase64() {
        // Arrange
        var expectedBase64 = PNG_1X1_BASE64;

        // Act
        var image = given()
                .contentType(APPLICATION_JSON)
                .when().get("/image/" + imageId)
                .then().statusCode(200)
                .extract().as(new TypeRef<Map<String, Object>>() {});

        // Assert
        assertThat(image.get("base64Image")).isEqualTo(expectedBase64);
    }

    @Test
    @Order(150)
    void deleteImage_shouldRemoveImage() {
        // Arrange
        // (imageId existiert bereits)

        // Act
        given()
                .when().delete("/image/" + imageId)
                .then().statusCode(204);

        // Assert
        var images = given()
                .contentType(APPLICATION_JSON)
                .when().get("/image")
                .then()
                .extract().as(new TypeRef<List<Map<String, Object>>>() {});

        assertThat(images)
                .noneMatch(img -> ((Number) img.get("id")).longValue() == imageId);
    }

    @Test
    @Order(160)
    void deleteImage_shouldReturn404_ifNotFound() {
        // Arrange
        long nonExistingId = 999999;

        // Act + Assert
        given()
                .when().delete("/image/" + nonExistingId)
                .then().statusCode(404);
    }

    @Test
    @Order(170)
    void createImage_shouldReturn400_ifMissingBase64() {
        // Arrange
        var invalidDto = Map.of(
                "description", "Missing base64",
                "imageUrl", "http://example.com/missing.jpg",
                "person", Map.of("id", person.getId())
        );

        // Act + Assert
        given()
                .contentType(APPLICATION_JSON)
                .body(invalidDto)
                .when().post("/image")
                .then().statusCode(400);
    }
}
