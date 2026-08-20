package repository.inmemory;

import entity.TaskRunEntity;
import exception.InvalidRequestException;
import model.enums.TaskStatus;

import java.util.*;

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
                .map(id -> this.getById(id).orElse(null))
                .filter(taskRunEntity -> Objects.nonNull(taskRunEntity) && taskRunEntity.isActive())
                .toList();
    }

    public TaskRunEntity updateStatus(TaskRunEntity taskRun, TaskStatus newTaskStatus) {
        if (!taskRun.getStatus().isValidStatusUpdate(newTaskStatus)) {
            throw new InvalidRequestException("Invalid status change", null);
        }
        taskRun.setStatus(newTaskStatus);
        return super.update(taskRun);
    }
}
