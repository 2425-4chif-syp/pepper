package at.htlleonding.pepper.resource;

import at.htlleonding.pepper.dto.GameDto;
import at.htlleonding.pepper.dto.StepDto;
import at.htlleonding.pepper.service.ImageService;
import at.htlleonding.pepper.service.TagAlongStoryService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("tagalongstories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TagAlongStoryResource {

    @Inject
    TagAlongStoryService tagAlongStoryService;

    @GET
    @RolesAllowed({"admin", "caretaker", "resident"})
    @Operation(summary = "Get all tag along stories")
    public Response getAllTagAlongStory(@QueryParam("withoutDisabled") Boolean withoutDisabled) {
        return Response.ok(tagAlongStoryService.getAll(withoutDisabled)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"admin", "caretaker", "resident"})
    @Operation(summary = "Get one tag along story with id")
    public Response getTagAlongStoriesById(@PathParam("id") Long id) {
        return Response.ok(tagAlongStoryService.getById(id)).build();
    }

    @GET
    @Path("/{id}/image")
    @RolesAllowed({"admin", "caretaker", "resident"})
    @Operation(summary = "Get one image per tag along story with id")
    public Response getTagAlongStoriesPicById(@PathParam("id") Long id) {
        ImageService.Picture picture = tagAlongStoryService.getStoryPicture(id);
        return Response.ok(picture.bytes())
                .type(picture.contentType())
                .header("Content-Disposition", "inline; filename=\"" + picture.fileName() + "\"")
                .build();
    }

    @POST
    @RolesAllowed({"admin", "caretaker"})
    @Operation(summary = "Create one tag along story")
    public Response createTagAlongStories(GameDto gameDTO) {
        return Response.status(Response.Status.CREATED)
                .entity(tagAlongStoryService.create(gameDTO))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"admin", "caretaker"})
    @Operation(summary = "Update one tag along story with id")
    public Response updateTagAlongStoriesById(@PathParam("id") Long id, GameDto gameDTO) {
        return Response.ok(tagAlongStoryService.update(id, gameDTO)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"admin", "caretaker"})
    @Operation(summary = "Delete one tag along story with id")
    public Response deleteTagAlongStoriesById(@PathParam("id") Long id) {
        tagAlongStoryService.delete(id);
        return Response.ok("Deleted tag along story").build();
    }

    @GET
    @Path("/{id}/steps")
    @RolesAllowed({"admin", "caretaker", "resident"})
    @Operation(summary = "Get all steps by game id")
    public Response getStepsById(@PathParam("id") Long id) {
        return Response.ok(tagAlongStoryService.getSteps(id)).build();
    }

    @POST
    @Path("/{id}/steps")
    @RolesAllowed({"admin", "caretaker"})
    @Operation(summary = "Create step by game id")
    public Response createStepsById(StepDto stepDTO, @PathParam("id") Long id) {
        return Response.status(Response.Status.CREATED)
                .entity(tagAlongStoryService.createStep(id, stepDTO))
                .build();
    }

    @DELETE
    @Path("/{id}/steps/{stepId}")
    @RolesAllowed({"admin", "caretaker"})
    @Operation(summary = "Delete step by ID")
    public Response deleteStepById(@PathParam("id") Long gameId, @PathParam("stepId") Long stepId) {
        return Response.ok(tagAlongStoryService.deleteStep(gameId, stepId)).build();
    }
}
