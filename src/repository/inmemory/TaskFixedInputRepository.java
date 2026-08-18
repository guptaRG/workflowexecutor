package repository.inmemory;

import entity.TaskFixedInputEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<TaskFixedInputEntity> getAllByTaskRunId(String taskRunId) {
        return idxByTaskId.getOrDefault(taskRunId, new ArrayList<>())
                .stream()
                .map(id -> this.getById(id).orElseThrow())
                .toList();
    }
}
