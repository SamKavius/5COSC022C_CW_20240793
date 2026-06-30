package uk.ac.westminster.mlops.mapper;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import uk.ac.westminster.mlops.exception.LinkedWorkspaceNotFoundException;
import uk.ac.westminster.mlops.model.ApiError;

@Provider
public class LinkedWorkspaceNotFoundExceptionMapper implements ExceptionMapper<LinkedWorkspaceNotFoundException> {
    @Override
    public Response toResponse(LinkedWorkspaceNotFoundException exception) {
        ApiError error = new ApiError(
                "LINKED_WORKSPACE_NOT_FOUND",
                exception.getMessage(),
                422
        );
        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
