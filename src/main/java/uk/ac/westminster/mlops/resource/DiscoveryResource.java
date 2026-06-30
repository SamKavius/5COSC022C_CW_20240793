package uk.ac.westminster.mlops.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.LinkedHashMap;
import java.util.Map;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Map<String, Object> getDiscovery() {
        Map<String, String> links = new LinkedHashMap<>();
        links.put("workspaces", "/api/v1/workspaces");
        links.put("models", "/api/v1/models");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("version", "v1");
        response.put("service", "MLOps Pipeline Management API");
        response.put("contact", "mlops-platform-admin@westminster.ac.uk");
        response.put("resources", links);
        return response;
    }
}
