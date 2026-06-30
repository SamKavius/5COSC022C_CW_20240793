package uk.ac.westminster.mlops.mapper;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import uk.ac.westminster.mlops.exception.WorkspaceNotEmptyException;
import uk.ac.westminster.mlops.model.ApiError;

@Provider
public class WorkspaceNotEmptyExceptionMapper implements ExceptionMapper<WorkspaceNotEmptyException> {
    @Override
    public Response toResponse(WorkspaceNotEmptyException exception) {
        ApiError error = new ApiError(
                "WORKSPACE_NOT_EMPTY",
                exception.getMessage(),
                Response.Status.CONFLICT.getStatusCode()
        );
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
