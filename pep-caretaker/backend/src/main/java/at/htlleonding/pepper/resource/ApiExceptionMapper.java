package at.htlleonding.pepper.resource;

import at.htlleonding.pepper.dto.ErrorResponse;
import io.quarkus.logging.Log;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        int status = status(exception);
        if (status >= 500) {
            Log.error("Unhandled API exception", exception);
        }

        Response.Status statusType = Response.Status.fromStatusCode(status);
        String error = statusType != null ? statusType.getReasonPhrase() : "Error";
        String message = exception.getMessage() != null ? exception.getMessage() : error;

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ErrorResponse(status, error, message))
                .build();
    }

    private int status(Throwable exception) {
        if (exception instanceof WebApplicationException webApplicationException) {
            return webApplicationException.getResponse().getStatus();
        }
        if (exception instanceof IllegalArgumentException) {
            return Response.Status.BAD_REQUEST.getStatusCode();
        }
        return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }
}
