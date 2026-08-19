package repository.inmemory;

import entity.TaskRunFixedInputEntity;

import java.util.*;

public class TaskRunFixedInputRepository extends AbstractInMemoryRepository<TaskRunFixedInputEntity> {
    private final Map<String, List<String>> idxByTaskRunId;

    public TaskRunFixedInputRepository() {
        super();
        idxByTaskRunId = new HashMap<>();
    }

    @Override
    public TaskRunFixedInputEntity save(TaskRunFixedInputEntity entity) {
        entity = super.save(entity);
        idxByTaskRunId.putIfAbsent(entity.getTaskRun().getId(), new ArrayList<>());
        idxByTaskRunId.get(entity.getTaskRun().getId()).add(entity.getId());
        return entity;
    }

    public List<TaskRunFixedInputEntity> getAllByTaskRunId(String taskRunId) {
        return idxByTaskRunId.getOrDefault(taskRunId, new ArrayList<>())
                .stream()
                .map(id -> this.getById(id).orElse(null))
                .filter(taskRunFixedInput -> Objects.nonNull(taskRunFixedInput) && taskRunFixedInput.isActive())
                .toList();
    }
}
