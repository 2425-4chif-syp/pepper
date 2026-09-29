package com.pep.mealplan.resource;

import jakarta.annotation.security.RolesAllowed;
import com.pep.mealplan.entity.Person;
import com.pep.mealplan.service.PersonService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
@Path("/api/residents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
// Klassenweiter Vorgabewert: ein kuenftiger Endpunkt ohne eigene
// @RolesAllowed-Angabe ist damit zu und nicht offen.
@RolesAllowed({"admin", "caretaker", "robot"})
public class PersonResource {
    @Inject
    PersonService service;

    @GET
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public List<Person> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public Response getById(@PathParam("id") Long id) {
        Person person = service.getById(id);
        return person == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(person).build();
    }

    @GET
    @Path("/count")
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public Response count() {
        return Response.ok(service.count()).build();
    }

    @POST
    @RolesAllowed({"admin", "caretaker", "robot"})
    public Response create(Person person) {
        Person created = service.create(person);
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"admin", "caretaker", "robot"})
    public Response update(@PathParam("id") Long id, Person person) {
        Person updated = service.update(id, person);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"admin", "caretaker", "robot"})
    public Response delete(@PathParam("id") Long id) {
        return service.delete(id)
                ? Response.noContent().build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }
}
