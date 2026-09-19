package at.htlleonding.pepper.model;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestSecurity(user = "test-admin", roles = {"admin", "caretaker"})
class TagAlongStoryImageTest {

    // 1x1 PNG
    private static final String PNG =
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==";

    @Test
    void sceneReferencingAnImageById_reusesItInsteadOfCopying() {
        long storyId = createStory();
        long libraryImageId = uploadLibraryImage();
        int imagesBefore = imageCount();

        long first = createStep(storyId, 1, Map.of("imageId", libraryImageId)).getLong("image.id");
        long second = createStep(storyId, 2, Map.of("imageId", libraryImageId)).getLong("image.id");

        assertThat(first).isEqualTo(libraryImageId);
        assertThat(second).isEqualTo(libraryImageId);
        assertThat(imageCount()).isEqualTo(imagesBefore);
    }

    @Test
    void sceneWithoutImage_storesNoImage() {
        long storyId = createStory();
        int imagesBefore = imageCount();

        JsonPath step = createStep(storyId, 1, Map.of());

        assertThat(step.getString("image")).isNull();
        assertThat(imageCount()).isEqualTo(imagesBefore);
    }

    @Test
    void deletingAnImageUsedByAStory_isRejectedWithConflict() {
        long storyId = createStory();
        long libraryImageId = uploadLibraryImage();
        createStep(storyId, 1, Map.of("imageId", libraryImageId));

        given().when().delete("/image/" + libraryImageId)
                .then().statusCode(409);
    }

    @Test
    void deletingAScene_keepsLibraryImagesAndSceneUploadsStillInUse() {
        long storyId = createStory();
        long libraryImageId = uploadLibraryImage();
        JsonPath uploaded = createStep(storyId, 1, Map.of("image", PNG));
        long uploadedImageId = uploaded.getLong("image.id");
        long secondStepId = createStep(storyId, 2, Map.of("imageId", uploadedImageId)).getLong("id");
        long libraryStepId = createStep(storyId, 3, Map.of("imageId", libraryImageId)).getLong("id");

        // Szene 1 löschen: ihr Upload wird noch von Szene 2 verwendet
        given().when().delete("/tagalongstories/" + storyId + "/steps/" + uploaded.getLong("id"))
                .then().statusCode(200);
        given().when().get("/image/picture/" + uploadedImageId).then().statusCode(200);

        // letzte Verwendung weg → der Szenen-Upload wird mitgelöscht
        given().when().delete("/tagalongstories/" + storyId + "/steps/" + secondStepId)
                .then().statusCode(200);
        given().when().get("/image/picture/" + uploadedImageId).then().statusCode(404);

        // Bibliotheksbilder bleiben immer erhalten
        given().when().delete("/tagalongstories/" + storyId + "/steps/" + libraryStepId)
                .then().statusCode(200);
        given().when().get("/image/picture/" + libraryImageId).then().statusCode(200);
    }

    @Test
    void updatingAStoryWithItsCurrentIconId_keepsTheIcon() {
        long storyId = createStory();
        long iconId = given().when().get("/tagalongstories/" + storyId)
                .then().statusCode(200).extract().jsonPath().getLong("storyIcon.id");

        given().contentType(APPLICATION_JSON)
                .body(Map.of("name", "Umbenannt", "iconId", iconId))
                .when().put("/tagalongstories/" + storyId)
                .then().statusCode(200);

        JsonPath story = given().when().get("/tagalongstories/" + storyId)
                .then().statusCode(200).extract().jsonPath();
        assertThat(story.getString("name")).isEqualTo("Umbenannt");
        assertThat(story.getLong("storyIcon.id")).isEqualTo(iconId);
        given().when().get("/image/picture/" + iconId).then().statusCode(200);
    }

    private long createStory() {
        return given().contentType(APPLICATION_JSON)
                .body(Map.of(
                        "name", "Teststory",
                        "icon", PNG,
                        "gameType", Map.of("id", "TAG_ALONG_STORY"),
                        "isEnabled", true))
                .when().post("/tagalongstories")
                .then().statusCode(201)
                .extract().jsonPath().getLong("id");
    }

    private long uploadLibraryImage() {
        return given().contentType(APPLICATION_JSON)
                .body(Map.of("description", "Bibliotheksbild", "base64Image", PNG.split(",")[1]))
                .when().post("/image")
                .then().statusCode(201)
                .extract().jsonPath().getLong("id");
    }

    private JsonPath createStep(long storyId, int index, Map<String, Object> image) {
        Map<String, Object> body = new HashMap<>(image);
        body.put("index", index);
        body.put("text", "Szene " + index);
        body.put("durationInSeconds", 5);
        body.put("move", Map.of("id", 1));
        return given().contentType(APPLICATION_JSON)
                .body(body)
                .when().post("/tagalongstories/" + storyId + "/steps")
                .then().statusCode(201)
                .extract().jsonPath();
    }

    private int imageCount() {
        List<?> items = given().when().get("/image/pictures")
                .then().statusCode(200)
                .extract().jsonPath().getList("items");
        return items.size();
    }
}
