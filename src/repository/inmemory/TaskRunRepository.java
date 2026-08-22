package repository.inmemory;

import entity.TaskRunEntity;
import exception.InvalidRequestException;
import model.enums.TaskStatus;

import java.util.*;

public class TaskRunRepository extends AbstractInMemoryRepository<TaskRunEntity> {
    private final Map<String, Map<String, String>> idxByWorkflowRunTaskId;

    public TaskRunRepository() {
        super();
        this.idxByWorkflowRunTaskId = new HashMap<>();
    }

    @Override
    public TaskRunEntity save(TaskRunEntity entity) {
        entity = super.save(entity);
        idxByWorkflowRunTaskId.putIfAbsent(entity.getWorkflowRun().getId(), new HashMap<>());
        idxByWorkflowRunTaskId.get(entity.getWorkflowRun().getId()).put(entity.getTask().getId(), entity.getId());
        return entity;
    }

    public List<TaskRunEntity> getByWorkflowRunId(String workflowRunId) {
        return idxByWorkflowRunTaskId.getOrDefault(workflowRunId, new HashMap<>())
                .values()
                .stream()
                .map(id -> this.getById(id).orElse(null))
                .filter(taskRunEntity -> Objects.nonNull(taskRunEntity) && taskRunEntity.isActive())
                .toList();
    }

    public Optional<TaskRunEntity> getByWorkflowRunTaskId(String workflowRunId, String taskId) {
        if (!idxByWorkflowRunTaskId.containsKey(workflowRunId) ||
                !idxByWorkflowRunTaskId.get(workflowRunId).containsKey(taskId)) {
            return Optional.empty();
        }
        return getById(idxByWorkflowRunTaskId.get(workflowRunId).get(taskId));
    }

    public TaskRunEntity updateStatus(TaskRunEntity taskRun, TaskStatus newTaskStatus) {
        if (!taskRun.getStatus().isValidStatusUpdate(newTaskStatus)) {
            throw new InvalidRequestException("Invalid status change", null);
        }
        taskRun.setStatus(newTaskStatus);
        return super.update(taskRun);
    }
}
