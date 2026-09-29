package com.pep.mealplan.resource;

import jakarta.annotation.security.RolesAllowed;
import com.pep.mealplan.entity.MealPlan;
import com.pep.mealplan.service.MealPlanService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/menu")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
// Klassenweiter Vorgabewert: ein kuenftiger Endpunkt ohne eigene
// @RolesAllowed-Angabe ist damit zu und nicht offen.
@RolesAllowed({"admin", "caretaker", "robot"})
public class MealPlanResource {

    @Inject
    MealPlanService service;

    // -------------------------------------------------
    // READ
    // -------------------------------------------------

    @GET
    @Path("/week/{weekNumber}")
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public List<MealPlan> getByWeek(@PathParam("weekNumber") int weekNumber) {
        return service.getByWeek(weekNumber);
    }

    @GET
    @Path("/day/{weekNumber}/{weekDay}")
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public MealPlan getByWeekAndDay(
            @PathParam("weekNumber") int weekNumber,
            @PathParam("weekDay") int weekDay
    ) {
        return service.getByWeekAndDay(weekNumber, weekDay);
    }

    // -------------------------------------------------
    // WRITE (UPSERT)
    // -------------------------------------------------

    @POST
    @RolesAllowed({"admin", "caretaker", "robot"})
    public Response upsertDay(MealPlan plan) {
        MealPlan saved = service.upsertDay(plan);
        return Response.ok(saved).build();
    }

    @POST
    @Path("/week")
    @RolesAllowed({"admin", "caretaker", "robot"})
    public Response upsertWeek(List<MealPlan> plans) {
        service.upsertWeek(plans);
        return Response.ok().build();
    }

    @DELETE
    @Path("/wipe")
    @RolesAllowed("admin")
    public Response wipeAll() {
        service.deleteAll();
        return Response.noContent().build();
    }
}
