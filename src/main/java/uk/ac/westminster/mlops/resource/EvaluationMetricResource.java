package uk.ac.westminster.mlops.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import uk.ac.westminster.mlops.model.EvaluationMetric;
import uk.ac.westminster.mlops.service.EvaluationMetricService;

import java.net.URI;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EvaluationMetricResource {
    private final String modelId;
    private final EvaluationMetricService evaluationMetricService;

    public EvaluationMetricResource(String modelId) {
        this.modelId = modelId;
        this.evaluationMetricService = EvaluationMetricService.getInstance();
    }

    @GET
    public List<EvaluationMetric> getMetrics() {
        return evaluationMetricService.getMetricsForModel(modelId);
    }

    @POST
    public Response addMetric(EvaluationMetric metric, @Context UriInfo uriInfo) {
        EvaluationMetric createdMetric = evaluationMetricService.addMetric(modelId, metric);
        URI location = uriInfo.getAbsolutePathBuilder().path(createdMetric.getId()).build();
        return Response.created(location).entity(createdMetric).build();
    }
}
