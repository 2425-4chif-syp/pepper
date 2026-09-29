package com.pep.mealplan.resource;

import jakarta.annotation.security.RolesAllowed;
import com.pep.mealplan.entity.FoodAllergen;
import com.pep.mealplan.service.FoodAllergenService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/food-allergens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
// Klassenweiter Vorgabewert: ein kuenftiger Endpunkt ohne eigene
// @RolesAllowed-Angabe ist damit zu und nicht offen.
@RolesAllowed({"admin", "caretaker", "robot"})
public class FoodAllergenResource {

    @Inject
    FoodAllergenService service;

    // -------------------------------------------------
    // READ
    // -------------------------------------------------

    @GET
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public List<FoodAllergen> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/food/{foodId}")
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public List<FoodAllergen> getByFoodId(@PathParam("foodId") Long foodId) {
        return service.getByFoodId(foodId);
    }

    @GET
    @Path("/allergen/{shortname}")
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public List<FoodAllergen> getByAllergenShortname(@PathParam("shortname") String shortname) {
        return service.getByAllergenShortname(shortname);
    }

    @GET
    @Path("/{foodId}/{allergenShortname}")
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public Response getById(
            @PathParam("foodId") Long foodId,
            @PathParam("allergenShortname") String allergenShortname) {
        FoodAllergen foodAllergen = service.getById(foodId, allergenShortname);
        if (foodAllergen == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(foodAllergen).build();
    }

    // -------------------------------------------------
    // WRITE
    // -------------------------------------------------

    @POST
    @Path("/{foodId}/{allergenShortname}")
    @RolesAllowed({"admin", "caretaker", "robot"})
    public Response create(
            @PathParam("foodId") Long foodId,
            @PathParam("allergenShortname") String allergenShortname) {
        FoodAllergen created = service.create(foodId, allergenShortname);
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

    @DELETE
    @Path("/{foodId}/{allergenShortname}")
    @RolesAllowed({"admin", "caretaker", "robot"})
    public Response delete(
            @PathParam("foodId") Long foodId,
            @PathParam("allergenShortname") String allergenShortname) {
        boolean deleted = service.delete(foodId, allergenShortname);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
