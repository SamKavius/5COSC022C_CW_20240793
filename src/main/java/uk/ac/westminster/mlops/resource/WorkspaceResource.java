package uk.ac.westminster.mlops.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import uk.ac.westminster.mlops.model.MLWorkspace;
import uk.ac.westminster.mlops.service.WorkspaceService;

import java.net.URI;
import java.util.List;

@Path("/workspaces")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WorkspaceResource {
    private final WorkspaceService workspaceService = WorkspaceService.getInstance();

    @GET
    public List<MLWorkspace> getWorkspaces() {
        return workspaceService.getAllWorkspaces();
    }

    @POST
    public Response createWorkspace(MLWorkspace workspace, @Context UriInfo uriInfo) {
        MLWorkspace createdWorkspace = workspaceService.createWorkspace(workspace);
        URI location = uriInfo.getAbsolutePathBuilder().path(createdWorkspace.getId()).build();
        return Response.created(location).entity(createdWorkspace).build();
    }

    @GET
    @Path("/{workspaceId}")
    public MLWorkspace getWorkspace(@PathParam("workspaceId") String workspaceId) {
        return workspaceService.getWorkspaceById(workspaceId);
    }

    @HEAD
    @Path("/{workspaceId}")
    public Response headWorkspace(@PathParam("workspaceId") String workspaceId) {
        workspaceService.getWorkspaceById(workspaceId);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{workspaceId}")
    public Response deleteWorkspace(@PathParam("workspaceId") String workspaceId) {
        workspaceService.deleteWorkspace(workspaceId);
        return Response.noContent().build();
    }
}
