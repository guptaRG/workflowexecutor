package repository.inmemory;

import entity.WorkflowStartingTaskEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowStartingTaskRepository extends AbstractInMemoryRepository<WorkflowStartingTaskEntity> {
    private final Map<String, List<String>> idxByWorkflowId;

    public WorkflowStartingTaskRepository() {
        super();
        idxByWorkflowId = new HashMap<>();
    }

    @Override
    public WorkflowStartingTaskEntity save(WorkflowStartingTaskEntity entity) {
        entity = super.save(entity);
        idxByWorkflowId.putIfAbsent(entity.getWorkflow().getId(), new ArrayList<>());
        idxByWorkflowId.get(entity.getWorkflow().getId()).add(entity.getId());
        return entity;
    }

    public List<WorkflowStartingTaskEntity> getByWorkflowId(String workflowId) {
        return idxByWorkflowId.getOrDefault(workflowId, new ArrayList<>())
                .stream()
                .map(id -> this.getById(id).orElseThrow())
                .toList();
    }
}
