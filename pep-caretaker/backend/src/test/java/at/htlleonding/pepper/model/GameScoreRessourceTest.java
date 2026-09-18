    package at.htlleonding.pepper.model;

    import at.htlleonding.pepper.repository.GameRepository;
    import io.quarkus.test.junit.QuarkusTest;
    import io.quarkus.test.security.TestSecurity;
    import io.restassured.RestAssured;
    import io.restassured.parsing.Parser;
    import jakarta.inject.Inject;
    import org.junit.jupiter.api.*;

    import java.util.Base64;
    import java.util.List;
    import java.util.Map;

    import static io.restassured.RestAssured.given;
    import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
    import static org.assertj.core.api.Assertions.assertThat;

    @QuarkusTest
    @TestSecurity(user = "test-admin", roles = {"admin", "caretaker", "resident"})
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    public class GameScoreRessourceTest {

        static {
            RestAssured.defaultParser = Parser.JSON;
        }

        static Game game;
        static Person person;

        @Inject
        GameRepository gameRepository;

        @Test
        @Order(110)
        void createPersonForGameScore() {
            var newPerson = Map.of(
                    "firstName", "Milad",
                    "lastName", "Moradi",
                    "birthDate", "1995-10-10",
                    "roomNo", "C3",
                    "isWorker", false
            );
            person = given()
                    .contentType(APPLICATION_JSON)
                    .body(newPerson)
                    .when().post("/person")
                    .then().statusCode(201)
                    .extract().as(Person.class);

            assertThat(person).isNotNull();
        }

        @Test
        @Order(120)
        void createTagAlongStoryGameOnly() {
            var base64Image = Base64.getEncoder().encodeToString("Hallo".getBytes());

            var newGame = Map.of(
                    "name", "Testgeschichte",
                    "icon", base64Image,
                    "isEnabled", true,
                    "gameType", Map.of("id", "TAG_ALONG_STORY")
            );

            game = given()
                    .contentType(APPLICATION_JSON)
                    .body(newGame)
                    .when().post("/tagalongstories")
                    .then().statusCode(201)
                    .extract().as(Game.class);

            assertThat(game).isNotNull();
        }

        @Test
        @Order(130)
        void createGameScore() {
            var newGameScore = Map.of(
                    "dateTime", "2024-03-20T12:30:00",
                    "game", Map.of("id", game.getId()),
                    "person", Map.of("id", person.getId()),
                    "score", 250
            );

            var gameScoreResponse = given()
                    .contentType(APPLICATION_JSON)
                    .body(newGameScore)
                    .when().post("/gamescore")
                    .then().statusCode(201)
                    .extract().as(Map.class);

            assertThat(gameScoreResponse).containsEntry("score", 250);
        }

        @Test
        @Order(140)
        void getScoresForPlayer_shouldReturnList() {
            var response = given()
                    .contentType(APPLICATION_JSON)
                    .when().get("/gamescore/player/" + person.getId())
                    .then().statusCode(200)
                    .extract().as(List.class);

            assertThat(response).isNotEmpty();
        }

        @Test
        @Order(150)
        void getScoresByGame_shouldReturnList() {
            var response = given()
                    .contentType(APPLICATION_JSON)
                    .when().get("/gamescore/game/" + game.getId())
                    .then().statusCode(200)
                    .extract().as(List.class);

            assertThat(response).isNotEmpty();
        }

        @Test
        @Order(160)
        void updateGameScore_shouldChangeScore() {

            var updatedScore = Map.of(
                    "dateTime", "2024-03-21T10:00:00",
                    "game", Map.of("id", game.getId()),
                    "person", Map.of("id", person.getId()),
                    "score", 999
            );

            var updated = given()
                    .contentType(APPLICATION_JSON)
                    .body(updatedScore)
                    .when().put("/gamescore/" + game.getId() + "/" + person.getId())
                    .then().statusCode(200)
                    .extract().as(Map.class);

            assertThat(updated).containsEntry("score", 999);
        }

        @Test
        @Order(170)
        void deleteGameScore_shouldRemoveSuccessfully() {
            // Neue Person erstellen
            var otherPerson = given()
                    .contentType(APPLICATION_JSON)
                    .body(Map.of(
                            "firstName", "Ali",
                            "lastName", "Test",
                            "birthDate", "1990-01-01",
                            "roomNo", "C2",
                            "isWorker", false
                    ))
                    .when().post("/person")
                    .then().statusCode(201)
                    .extract().as(Person.class);

            // Score mit dieser neuen Person speichern
            var newScore = Map.of(
                    "dateTime", "2024-03-22T14:00:00",
                    "game", Map.of("id", game.getId()),
                    "person", Map.of("id", otherPerson.getId()),
                    "score", 111
            );

            // Rückgabe als GameScore statt Map!
            var created = given()
                    .contentType(APPLICATION_JSON)
                    .body(newScore)
                    .when().post("/gamescore")
                    .then().statusCode(201)
                    .extract().as(GameScore.class);

            // Löschen über zusammengesetzten Schlüssel
            given()
                    .when().delete("/gamescore/" + created.getGame().getId() + "/" + created.getPerson().getId())
                    .then().statusCode(204);

            // Sicherstellen, dass der Score nicht mehr vorhanden ist
            given()
                    .when().get("/gamescore/" + created.getGame().getId() + "/" + created.getPerson().getId())
                    .then().statusCode(404);
        }

        @Test
        @Order(180)
        void getGameScoreById_shouldReturnCorrectData() {
            // Neue Person erstellen
            var testPerson = given()
                    .contentType(APPLICATION_JSON)
                    .body(Map.of(
                            "firstName", "Testi",
                            "lastName", "McTestface",
                            "birthDate", "1999-12-31",
                            "roomNo", "T1",
                            "isWorker", false
                    ))
                    .when().post("/person")
                    .then().statusCode(201)
                    .extract().as(Person.class);

            // GameScore anlegen
            var newScore = Map.of(
                    "dateTime", "2024-03-23T09:00:00",
                    "game", Map.of("id", game.getId()),
                    "person", Map.of("id", testPerson.getId()),
                    "score", 888
            );

            // POST: GameScore erstellen
            given()
                    .contentType(APPLICATION_JSON)
                    .body(newScore)
                    .when().post("/gamescore")
                    .then().statusCode(201);

            // GET: GameScore mit composite key abrufen
            var found = given()
                    .when().get("/gamescore/" + game.getId() + "/" + testPerson.getId())
                    .then().statusCode(200)
                    .extract().as(Map.class);

            assertThat(found).containsEntry("score", 888);
        }

    }
