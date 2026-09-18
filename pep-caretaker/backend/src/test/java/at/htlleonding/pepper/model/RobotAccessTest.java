package at.htlleonding.pepper.model;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * The Pepper apps authenticate as the Keycloak service account "pepper-robot" with the realm role "robot".
 * That role may only read what the apps display and record game scores.
 */
@QuarkusTest
@TestSecurity(user = "service-account-pepper-robot", roles = {"robot"})
public class RobotAccessTest {

    @Test
    void robotCanReadWhatThePepperAppsUse() {
        given().when().get("/tagalongstories").then().statusCode(200);
        given().when().get("/person").then().statusCode(200);
        given().when().get("/gamescore/player/1").then().statusCode(200);
    }

    @Test
    void robotCannotChangeOrManageData() {
        given().contentType(APPLICATION_JSON).body(Map.of("firstName", "X", "lastName", "Y"))
                .when().post("/person").then().statusCode(403);
        given().contentType(APPLICATION_JSON).body(Map.of("description", "x", "base64Image", "AA=="))
                .when().post("/image").then().statusCode(403);
        given().when().delete("/tagalongstories/1").then().statusCode(403);
        given().when().delete("/image/1").then().statusCode(403);
        given().when().get("/gamescore").then().statusCode(403);
        given().when().get("/image/pictures").then().statusCode(403);
    }
}
