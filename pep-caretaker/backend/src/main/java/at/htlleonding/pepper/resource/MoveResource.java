package at.htlleonding.pepper.resource;

import at.htlleonding.pepper.model.Move;
import at.htlleonding.pepper.service.MoveService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("move")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class MoveResource {

    @Inject
    MoveService moveService;

    @GET
    @RolesAllowed({"admin", "caretaker", "resident"})
    @Operation(summary = "Get all moves")
    public Response getAllMoves() {
        return Response.ok(moveService.getAll()).build();
    }

    @POST
    @RolesAllowed({"admin", "caretaker"})
    @Operation(summary = "Create a move")
    public Response createMove(Move move) {
        return Response.status(Response.Status.CREATED)
                .entity(moveService.create(move))
                .build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"admin", "caretaker"})
    @Operation(summary = "Delete a move")
    public Response deleteMove(@PathParam("id") Long id) {
        moveService.delete(id);
        return Response.ok("Move with id " + id + " successfully deleted.").build();
    }
}
