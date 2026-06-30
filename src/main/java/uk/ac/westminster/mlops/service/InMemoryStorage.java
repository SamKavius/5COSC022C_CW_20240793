package uk.ac.westminster.mlops.service;

import uk.ac.westminster.mlops.model.EvaluationMetric;
import uk.ac.westminster.mlops.model.MLWorkspace;
import uk.ac.westminster.mlops.model.MachineLearningModel;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryStorage {
    private static final InMemoryStorage INSTANCE = new InMemoryStorage();

    private final ConcurrentHashMap<String, MLWorkspace> workspaces = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, MachineLearningModel> models = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<EvaluationMetric>> metricsByModel = new ConcurrentHashMap<>();

    private InMemoryStorage() {
    }

    public static InMemoryStorage getInstance() {
        return INSTANCE;
    }

    public ConcurrentHashMap<String, MLWorkspace> getWorkspaces() {
        return workspaces;
    }

    public ConcurrentHashMap<String, MachineLearningModel> getModels() {
        return models;
    }

    public ConcurrentHashMap<String, List<EvaluationMetric>> getMetricsByModel() {
        return metricsByModel;
    }
}
