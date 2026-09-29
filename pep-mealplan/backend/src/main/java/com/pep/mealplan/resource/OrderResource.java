package com.pep.mealplan.resource;

import jakarta.annotation.security.RolesAllowed;
import com.pep.mealplan.entity.Order;
import com.pep.mealplan.resource.dto.OrderCreateDTO;
import com.pep.mealplan.service.OrderService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;

import com.pep.mealplan.resource.dto.KitchenSummary;


@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
// Klassenweiter Vorgabewert: ein kuenftiger Endpunkt ohne eigene
// @RolesAllowed-Angabe ist damit zu und nicht offen.
@RolesAllowed({"admin", "caretaker", "robot"})
public class OrderResource {

    @Inject
    OrderService service;

    // -------------------------------------------------
    // READ
    // -------------------------------------------------

    @GET
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public List<Order> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public Response getById(@PathParam("id") Long id) {
        Order order = service.getById(id);
        return order == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(order).build();
    }

    @GET
    @Path("/date/{date}")
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public List<Order> getByDate(@PathParam("date") LocalDate date) {
        return service.getByDate(date);
    }

    @GET
    @Path("/person/{personId}/week/{date}")
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public List<Order> getByPersonAndWeek(
            @PathParam("personId") Long personId,
            @PathParam("date") LocalDate date) {
        return service.getByPersonAndWeek(personId, date);
    }

    // -------------------------------------------------
    // WRITE
    // -------------------------------------------------

    /**
     * Upsert:
     * eine Bestellung pro Person und Tag
     */
    @PUT
    @Path("/by-user-date")
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public Order upsert(Order order) {
        return service.upsert(order);
    }

    @POST
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public Response create(OrderCreateDTO dto) {
        Order order = service.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(java.util.Map.of("id", order.id))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"admin", "caretaker", "robot"})
    public Response delete(@PathParam("id") Long id) {
        return service.delete(id)
                ? Response.noContent().build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    // -------------------------------------------------
    // EXPORT (Küche)
    // -------------------------------------------------

    @GET
    @Path("/export/{date}")
    @RolesAllowed({"admin", "caretaker", "robot"})
    public List<Order> export(@PathParam("date") LocalDate date) {
        return service.exportForWeek(date);
    }

    @GET
    @Path("/kitchen/{date}")
    @RolesAllowed({"admin", "caretaker", "robot"})
    public KitchenSummary kitchenSummary(@PathParam("date") LocalDate date) {
        return service.kitchenSummaryForWeek(date);
    }

}
