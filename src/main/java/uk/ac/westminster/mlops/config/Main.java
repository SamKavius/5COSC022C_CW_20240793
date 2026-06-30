package uk.ac.westminster.mlops.config;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class Main {
    private static final String DEFAULT_BASE_URI = "http://0.0.0.0:8080/";

    public static void main(String[] args) throws IOException {
        String baseUri = System.getenv().getOrDefault("MLOPS_API_BASE_URI", DEFAULT_BASE_URI);
        ResourceConfig resourceConfig = ResourceConfig.forApplication(new MlopsApplication());
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUri), resourceConfig);

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
        System.out.println("MLOps Pipeline Management API is running at " + baseUri + "api/v1");
        System.out.println("Press Enter to stop the server.");
        System.in.read();
        server.shutdownNow();
    }
}
