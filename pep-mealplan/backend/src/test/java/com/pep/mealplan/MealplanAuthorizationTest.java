package com.pep.mealplan;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

/**
 * Bis zu diesem Commit war das Mealplan-Backend vollstaendig offen: keine
 * quarkus-oidc-Abhaengigkeit, kein @RolesAllowed. Jeder konnte
 * "DELETE /api/menu/wipe" aufrufen. Diese Tests halten die Absicherung fest.
 */
@QuarkusTest
class MealplanAuthorizationTest {

    @Test
    void anonymousIsRejected() {
        given().when().get("/api/menu/week/1").then().statusCode(401);
    }

    @Test
    void anonymousCannotWipeTheMenu() {
        given().when().delete("/api/menu/wipe").then().statusCode(401);
    }

    @Test
    @TestSecurity(user = "bewohner", roles = {"resident"})
    void residentMayReadTheMenu() {
        given().when().get("/api/menu/week/1").then().statusCode(200);
    }

    @Test
    @TestSecurity(user = "bewohner", roles = {"resident"})
    void residentMayNotWipeTheMenu() {
        given().when().delete("/api/menu/wipe").then().statusCode(403);
    }

    @Test
    @TestSecurity(user = "pflege", roles = {"caretaker"})
    void caretakerMayNotWipeTheMenu() {
        given().when().delete("/api/menu/wipe").then().statusCode(403);
    }

    @Test
    @TestSecurity(user = "pepper", roles = {"robot"})
    void robotMayMaintainResidents() {
        given().when().get("/api/residents").then().statusCode(200);
    }
}
