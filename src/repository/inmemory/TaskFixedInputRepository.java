package repository.inmemory;

import entity.TaskFixedInputEntity;

import java.util.*;

public class TaskFixedInputRepository extends AbstractInMemoryRepository<TaskFixedInputEntity> {
    private final Map<String, List<String>> idxByTaskId;

    public TaskFixedInputRepository() {
        super();
        idxByTaskId = new HashMap<>();
    }

    @Override
    public TaskFixedInputEntity save(TaskFixedInputEntity entity) {
        entity = super.save(entity);
        idxByTaskId.putIfAbsent(entity.getTask().getId(), new ArrayList<>());
        idxByTaskId.get(entity.getTask().getId()).add(entity.getId());
        return entity;
    }

    public List<TaskFixedInputEntity> getAllByTaskId(String taskId) {
        return idxByTaskId.getOrDefault(taskId, new ArrayList<>())
                .stream()
                .map(id -> this.getById(id).orElse(null))
                .filter(taskFixedInput -> Objects.nonNull(taskFixedInput) && taskFixedInput.isActive())
                .toList();
    }
}
