package uk.ac.westminster.mlops.mapper;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import uk.ac.westminster.mlops.exception.ModelDeprecatedException;
import uk.ac.westminster.mlops.model.ApiError;

@Provider
public class ModelDeprecatedExceptionMapper implements ExceptionMapper<ModelDeprecatedException> {
    @Override
    public Response toResponse(ModelDeprecatedException exception) {
        ApiError error = new ApiError(
                "MODEL_DEPRECATED",
                exception.getMessage(),
                Response.Status.FORBIDDEN.getStatusCode()
        );
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
