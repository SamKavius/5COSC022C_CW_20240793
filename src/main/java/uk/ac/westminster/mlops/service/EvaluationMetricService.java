package uk.ac.westminster.mlops.service;

import uk.ac.westminster.mlops.exception.ModelDeprecatedException;
import uk.ac.westminster.mlops.model.EvaluationMetric;
import uk.ac.westminster.mlops.model.MachineLearningModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EvaluationMetricService {
    private static final EvaluationMetricService INSTANCE = new EvaluationMetricService();

    private final Map<String, List<EvaluationMetric>> metricsByModel;
    private final ModelService modelService;

    private EvaluationMetricService() {
        this.metricsByModel = InMemoryStorage.getInstance().getMetricsByModel();
        this.modelService = ModelService.getInstance();
    }

    public static EvaluationMetricService getInstance() {
        return INSTANCE;
    }

    public List<EvaluationMetric> getMetricsForModel(String modelId) {
        modelService.getModelById(modelId);
        List<EvaluationMetric> metrics = metricsByModel.get(modelId);
        if (metrics == null) {
            return Collections.emptyList();
        }
        synchronized (metrics) {
            return new ArrayList<>(metrics);
        }
    }

    public EvaluationMetric addMetric(String modelId, EvaluationMetric metric) {
        MachineLearningModel model = modelService.getModelById(modelId);
        if ("DEPRECATED".equalsIgnoreCase(model.getStatus())) {
            throw new ModelDeprecatedException("Deprecated models cannot accept new evaluation metrics.");
        }

        EvaluationMetric metricToStore = metric != null ? metric : new EvaluationMetric();
        if (metricToStore.getId() == null || metricToStore.getId().isBlank()) {
            metricToStore.setId(UUID.randomUUID().toString());
        }
        if (metricToStore.getTimestamp() <= 0) {
            metricToStore.setTimestamp(System.currentTimeMillis());
        }

        List<EvaluationMetric> metrics = metricsByModel.computeIfAbsent(
                modelId,
                key -> Collections.synchronizedList(new ArrayList<>())
        );
        metrics.add(metricToStore);
        modelService.updateLatestAccuracy(modelId, metricToStore.getAccuracyScore());
        return metricToStore;
    }
}
