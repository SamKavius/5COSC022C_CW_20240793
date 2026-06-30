package uk.ac.westminster.mlops.mapper;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import uk.ac.westminster.mlops.model.ApiError;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException exception) {
        Response response = exception.getResponse();
        int status = response.getStatus();
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            message = response.getStatusInfo().getReasonPhrase();
        }

        ApiError error = new ApiError(
                response.getStatusInfo().getReasonPhrase().replace(' ', '_').toUpperCase(),
                message,
                status
        );
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
