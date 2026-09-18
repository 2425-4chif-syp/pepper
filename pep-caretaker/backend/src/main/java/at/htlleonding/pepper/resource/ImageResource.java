package at.htlleonding.pepper.resource;

import at.htlleonding.pepper.dto.ImageContentDto;
import at.htlleonding.pepper.dto.ImageLinkDto;
import at.htlleonding.pepper.service.ImageService;
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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.Map;

@Path("image")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ImageResource {

    @Inject
    ImageService imageService;

    @GET
    @RolesAllowed({"admin", "caretaker", "resident"})
    public Response getAllImages() {
        return Response.ok(imageService.getAll()).build();
    }

    @GET
    @Path("{id}")
    @RolesAllowed({"admin", "caretaker", "resident"})
    public Response getImageById(@PathParam("id") Long id) {
        return Response.ok(imageService.getById(id)).build();
    }

    @GET
    @Path("/picture/{id}")
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public Response getPictureById(@PathParam("id") Long id) {
        ImageService.Picture picture = imageService.getPicture(id);
        if (picture == null || picture.bytes() == null || picture.bytes().length == 0) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.ok(picture.bytes())
                .type(picture.contentType())
                .header("Content-Disposition", "inline; filename=\"" + picture.fileName() + "\"")
                .build();
    }

    @GET
    @Path("/pictures")
    @RolesAllowed({"admin", "caretaker", "resident"})
    public Response listAll(@Context UriInfo uriInfo) {
        List<ImageLinkDto> items = imageService.getPictureLinks(id -> uriInfo.getBaseUriBuilder()
                .path("image/picture/" + id)
                .build()
                .toString());
        return Response.ok(Map.of(
                "total", items.size(),
                "items", items
        )).build();
    }

    @POST
    @RolesAllowed({"admin", "caretaker"})
    public Response createImage(ImageContentDto imageDto) {
        return Response.status(Response.Status.CREATED)
                .entity(imageService.create(imageDto))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"admin", "caretaker"})
    public Response deleteImage(@PathParam("id") Long id) {
        imageService.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("/person/{personId}")
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public Response getImagesByPersonId(@PathParam("personId") Long personId) {
        return Response.ok(imageService.getByPersonId(personId)).build();
    }
}
