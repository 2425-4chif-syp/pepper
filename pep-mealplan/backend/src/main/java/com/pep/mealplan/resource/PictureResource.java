package com.pep.mealplan.resource;

import jakarta.annotation.security.RolesAllowed;
import com.pep.mealplan.entity.Picture;
import com.pep.mealplan.resource.dto.ImageDto;
import com.pep.mealplan.resource.dto.ImageJson;
import com.pep.mealplan.service.PictureService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.Base64;
import java.util.List;

@Path("/api/images")
@RolesAllowed({"admin", "caretaker", "robot"})
public class PictureResource {

    @Inject
    PictureService pictureService;

    @Context
    UriInfo uriInfo;

    // GET all images as DTOs with Base64
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public List<ImageDto> getAll() {
        return pictureService.getAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    // GET all images with href URLs
    @GET
    @Path("/pictures")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public List<ImageJson> getAllWithHref() {
        String baseUri = uriInfo.getBaseUri().toString();
        return pictureService.getAll().stream()
                .map(p -> new ImageJson(
                        p.id,
                        baseUri + "api/images/picture/" + p.id,
                        p.description
                ))
                .toList();
    }

    // GET single image by ID as DTO with Base64
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public Response getById(@PathParam("id") Long id) {
        Picture picture = pictureService.getById(id);
        if (picture == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(convertToDto(picture)).build();
    }

    // GET raw binary image with MIME type detection
    @GET
    @Path("/picture/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @RolesAllowed({"admin", "caretaker", "resident", "robot"})
    public Response getPictureById(@PathParam("id") Long id) {
        byte[] imageData = pictureService.getImageData(id);
        if (imageData == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String mimeType = detectMimeType(imageData);
        return Response.ok(imageData)
                .type(mimeType)
                .header("Content-Disposition", "inline; filename=\"image-" + id + "\"")
                .build();
    }

    // POST - Create image with Base64
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin", "caretaker", "robot"})
    public Response createImage(ImageDto imageDto) {
        if (imageDto.base64Image() == null || imageDto.base64Image().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("base64Image is required")
                    .build();
        }

        try {
            byte[] imageData = Base64.getDecoder().decode(imageDto.base64Image());
            Picture picture = pictureService.create(
                    imageDto.description(),
                    imageDto.imageUrl(),
                    imageData
            );

            return Response.status(Response.Status.CREATED)
                    .entity(convertToDto(picture))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid Base64 encoding")
                    .build();
        }
    }

    // DELETE image
    @DELETE
    @Path("/{id}")
    @RolesAllowed({"admin", "caretaker", "robot"})
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = pictureService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    // Convert entity to DTO (without base64 data - use /picture/{id} endpoint for image data)
    private ImageDto convertToDto(Picture picture) {
        return new ImageDto(
                picture.id,
                null, // base64 not included - use /picture/{id} endpoint
                picture.url,
                picture.description
        );
    }

    // Detect MIME type from binary header
    private String detectMimeType(byte[] data) {
        if (data == null || data.length < 12) {
            return "application/octet-stream";
        }

        // PNG: 89 50 4E 47
        if (data.length >= 8 &&
                (data[0] & 0xFF) == 0x89 &&
                data[1] == 0x50 &&
                data[2] == 0x4E &&
                data[3] == 0x47) {
            return "image/png";
        }

        // JPEG: FF D8 FF
        if (data.length >= 3 &&
                (data[0] & 0xFF) == 0xFF &&
                (data[1] & 0xFF) == 0xD8 &&
                (data[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }

        // GIF: 47 49 46 38
        if (data.length >= 6 &&
                data[0] == 0x47 &&
                data[1] == 0x49 &&
                data[2] == 0x46 &&
                data[3] == 0x38) {
            return "image/gif";
        }

        // WebP: 52 49 46 46 ... 57 45 42 50
        if (data.length >= 12 &&
                data[0] == 0x52 &&
                data[1] == 0x49 &&
                data[2] == 0x46 &&
                data[3] == 0x46 &&
                data[8] == 0x57 &&
                data[9] == 0x45 &&
                data[10] == 0x42 &&
                data[11] == 0x50) {
            return "image/webp";
        }

        return "application/octet-stream";
    }
}
