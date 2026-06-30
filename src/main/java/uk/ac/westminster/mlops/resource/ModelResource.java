package uk.ac.westminster.mlops.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import uk.ac.westminster.mlops.model.MachineLearningModel;
import uk.ac.westminster.mlops.service.ModelService;

import java.net.URI;
import java.util.List;

@Path("/models")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ModelResource {
    private final ModelService modelService = ModelService.getInstance();

    @GET
    public List<MachineLearningModel> getModels(@QueryParam("status") String status) {
        return modelService.getAllModels(status);
    }

    @POST
    public Response createModel(MachineLearningModel model, @Context UriInfo uriInfo) {
        MachineLearningModel createdModel = modelService.createModel(model);
        URI location = uriInfo.getAbsolutePathBuilder().path(createdModel.getId()).build();
        return Response.created(location).entity(createdModel).build();
    }

    @GET
    @Path("/{modelId}")
    public MachineLearningModel getModel(@PathParam("modelId") String modelId) {
        return modelService.getModelById(modelId);
    }

    @Path("/{modelId}/metrics")
    public EvaluationMetricResource getEvaluationMetricResource(@PathParam("modelId") String modelId) {
        return new EvaluationMetricResource(modelId);
    }
}
