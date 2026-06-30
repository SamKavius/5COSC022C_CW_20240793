package uk.ac.westminster.mlops.service;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import uk.ac.westminster.mlops.exception.WorkspaceNotEmptyException;
import uk.ac.westminster.mlops.model.MLWorkspace;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class WorkspaceService {
    private static final WorkspaceService INSTANCE = new WorkspaceService();

    private final ConcurrentHashMap<String, MLWorkspace> workspaces;

    private WorkspaceService() {
        this.workspaces = InMemoryStorage.getInstance().getWorkspaces();
    }

    public static WorkspaceService getInstance() {
        return INSTANCE;
    }

    public List<MLWorkspace> getAllWorkspaces() {
        return new ArrayList<>(workspaces.values());
    }

    public MLWorkspace createWorkspace(MLWorkspace workspace) {
        MLWorkspace workspaceToStore = workspace != null ? workspace : new MLWorkspace();
        if (workspaceToStore.getId() == null || workspaceToStore.getId().isBlank()) {
            workspaceToStore.setId(generateWorkspaceId());
        }
        workspaceToStore.setModelIds(new ArrayList<>());

        MLWorkspace existing = workspaces.putIfAbsent(workspaceToStore.getId(), workspaceToStore);
        if (existing != null) {
            throw new WebApplicationException(
                    "Workspace with id '" + workspaceToStore.getId() + "' already exists.",
                    Response.Status.CONFLICT
            );
        }
        return workspaceToStore;
    }

    public MLWorkspace getWorkspaceById(String workspaceId) {
        MLWorkspace workspace = workspaces.get(workspaceId);
        if (workspace == null) {
            throw new NotFoundException("Workspace with id '" + workspaceId + "' was not found.");
        }
        return workspace;
    }

    public void deleteWorkspace(String workspaceId) {
        MLWorkspace workspace = getWorkspaceById(workspaceId);
        if (workspace.getModelIds() != null && !workspace.getModelIds().isEmpty()) {
            throw new WorkspaceNotEmptyException("Workspace cannot be deleted because models are still assigned to it.");
        }
        workspaces.remove(workspaceId);
    }

    private String generateWorkspaceId() {
        return "WS-" + ThreadLocalRandom.current().nextInt(1000, 10000);
    }
}
