package uk.ac.westminster.mlops.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.jackson.JacksonFeature;
import uk.ac.westminster.mlops.filter.ApiLoggingFilter;
import uk.ac.westminster.mlops.mapper.LinkedWorkspaceNotFoundExceptionMapper;
import uk.ac.westminster.mlops.mapper.ModelDeprecatedExceptionMapper;
import uk.ac.westminster.mlops.mapper.NotFoundExceptionMapper;
import uk.ac.westminster.mlops.mapper.ThrowableExceptionMapper;
import uk.ac.westminster.mlops.mapper.WebApplicationExceptionMapper;
import uk.ac.westminster.mlops.mapper.WorkspaceNotEmptyExceptionMapper;
import uk.ac.westminster.mlops.resource.DiscoveryResource;
import uk.ac.westminster.mlops.resource.ModelResource;
import uk.ac.westminster.mlops.resource.WorkspaceResource;

import java.util.Set;

@ApplicationPath("/api/v1")
public class MlopsApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
                JacksonFeature.class,
                DiscoveryResource.class,
                WorkspaceResource.class,
                ModelResource.class,
                WorkspaceNotEmptyExceptionMapper.class,
                LinkedWorkspaceNotFoundExceptionMapper.class,
                ModelDeprecatedExceptionMapper.class,
                NotFoundExceptionMapper.class,
                WebApplicationExceptionMapper.class,
                ThrowableExceptionMapper.class,
                ApiLoggingFilter.class
        );
    }
}
