package com.pep.mealplan.resource;

import jakarta.annotation.security.RolesAllowed;
import com.pep.mealplan.entity.Food;
import com.pep.mealplan.resource.dto.FoodCreateDTO;
import com.pep.mealplan.service.FoodService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/foods")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
// Klassenweiter Vorgabewert: ein kuenftiger Endpunkt ohne eigene
// @RolesAllowed-Angabe ist damit zu und nicht offen.
@RolesAllowed({"admin", "caretaker", "robot"})
public class FoodResource {

    @Inject
    FoodService foodService;

    // -------------------------------------------------
    // READ
    // -------------------------------------------------
    @GET
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public List<Food> getAll() {
        return foodService.getAll();
    }
    @GET
    @Path("/{id}")
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public Response getById(@PathParam("id") Long id) {
        Food food = foodService.getById(id);
        if (food == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(food).build();
    }

    @GET
    @Path("/type/{type}")
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public List<Food> getByType(@PathParam("type") String type) {
        return foodService.getByType(type);
    }

    @GET
    @Path("/name/{name}")
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public List<Food> getByName(@PathParam("name") String name) {
        return foodService.searchByName(name);
    }

    // -------------------------------------------------
    // WRITE
    // -------------------------------------------------

    @POST
    @RolesAllowed({"admin", "caretaker", "robot"})
    public Response create(FoodCreateDTO dto) {
        Food created = foodService.create(dto.name(), dto.type(), dto.pictureId());
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"admin", "caretaker", "robot"})
    public Response update(@PathParam("id") Long id, Food food) {
        Food updated = foodService.update(id, food);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"admin", "caretaker", "robot"})
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = foodService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
