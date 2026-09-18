package at.htlleonding.pepper.resource;

import at.htlleonding.pepper.model.Person;
import at.htlleonding.pepper.service.PersonService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("person")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class PersonResource {

    @Inject
    PersonService personService;

    @GET
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    @Operation(summary = "Get all people")
    public Response getAllPeople() {
        return Response.ok(personService.getAll()).build();
    }

    @POST
    @RolesAllowed("admin")
    public Response add(Person person) {
        return Response.status(Response.Status.CREATED)
                .entity(personService.create(person))
                .build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public Response getPersonById(@PathParam("id") Long id) {
        return Response.ok(personService.getById(id)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response updatePerson(@PathParam("id") Long id, Person updatedPerson) {
        return Response.ok(personService.update(id, updatedPerson)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response deletePerson(@PathParam("id") Long id) {
        PersonService.DeleteResult result = personService.delete(id);
        return Response.ok(String.format("Person und %d Bild(er) geloescht", result.deletedImages())).build();
    }

    /**
     * Legacy local login kept for old manual requests. API authorization is now
     * handled by Keycloak bearer tokens.
     */
    @POST
    @Path("/login")
    @PermitAll
    public Response login(Person loginPerson) {
        personService.login(loginPerson);
        return Response.ok("Erfolgreich eingeloggt").build();
    }
}
