package uk.ac.westminster.mlops.service;

import jakarta.ws.rs.NotFoundException;
import uk.ac.westminster.mlops.exception.LinkedWorkspaceNotFoundException;
import uk.ac.westminster.mlops.model.MLWorkspace;
import uk.ac.westminster.mlops.model.MachineLearningModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ModelService {
    private static final ModelService INSTANCE = new ModelService();

    private final ConcurrentHashMap<String, MachineLearningModel> models;
    private final WorkspaceService workspaceService;

    private ModelService() {
        this.models = InMemoryStorage.getInstance().getModels();
        this.workspaceService = WorkspaceService.getInstance();
    }

    public static ModelService getInstance() {
        return INSTANCE;
    }

    public List<MachineLearningModel> getAllModels(String status) {
        List<MachineLearningModel> result = new ArrayList<>();
        for (MachineLearningModel model : models.values()) {
            if (status == null || status.isBlank() || status.equalsIgnoreCase(model.getStatus())) {
                result.add(model);
            }
        }
        return result;
    }

    public MachineLearningModel createModel(MachineLearningModel model) {
        MachineLearningModel modelToStore = model != null ? model : new MachineLearningModel();
        if (modelToStore.getWorkspaceId() == null || modelToStore.getWorkspaceId().isBlank()) {
            throw new LinkedWorkspaceNotFoundException("workspaceId must refer to an existing workspace.");
        }

        MLWorkspace workspace;
        try {
            workspace = workspaceService.getWorkspaceById(modelToStore.getWorkspaceId());
        } catch (NotFoundException exception) {
            throw new LinkedWorkspaceNotFoundException(
                    "Workspace with id '" + modelToStore.getWorkspaceId() + "' does not exist."
            );
        }

        modelToStore.setId(generateModelId());
        models.put(modelToStore.getId(), modelToStore);
        if (!workspace.getModelIds().contains(modelToStore.getId())) {
            workspace.getModelIds().add(modelToStore.getId());
        }
        return modelToStore;
    }

    public MachineLearningModel getModelById(String modelId) {
        MachineLearningModel model = models.get(modelId);
        if (model == null) {
            throw new NotFoundException("Model with id '" + modelId + "' was not found.");
        }
        return model;
    }

    public void updateLatestAccuracy(String modelId, double latestAccuracy) {
        MachineLearningModel model = getModelById(modelId);
        model.setLatestAccuracy(latestAccuracy);
    }

    private String generateModelId() {
        return "MOD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
