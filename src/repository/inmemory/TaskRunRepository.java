package repository.inmemory;

import entity.TaskRunEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskRunRepository extends AbstractInMemoryRepository<TaskRunEntity> {
    private final Map<String, List<String>> idxByWorkflowRunId;

    public TaskRunRepository() {
        super();
        this.idxByWorkflowRunId = new HashMap<>();
    }

    @Override
    public TaskRunEntity save(TaskRunEntity entity) {
        entity = super.save(entity);
        idxByWorkflowRunId.putIfAbsent(entity.getWorkflowRun().getId(), new ArrayList<>());
        idxByWorkflowRunId.get(entity.getWorkflowRun().getId()).add(entity.getId());
        return entity;
    }

    public List<TaskRunEntity> getByWorkflowRunId(String workflowRunId) {
        return idxByWorkflowRunId.getOrDefault(workflowRunId, new ArrayList<>())
                .stream()
                .map(id -> this.getById(id).orElseThrow())
                .toList();
    }
}
