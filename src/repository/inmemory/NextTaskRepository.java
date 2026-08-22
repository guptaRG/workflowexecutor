package repository.inmemory;

import entity.NextTaskEntity;

import java.util.*;

public class NextTaskRepository extends AbstractInMemoryRepository<NextTaskEntity> {
    private final Map<String, List<String>> idxByTaskId;
    private final Map<String, List<String>> idxByNextTaskId;

    public NextTaskRepository() {
        super();
        this.idxByTaskId = new HashMap<>();
        this.idxByNextTaskId = new HashMap<>();
    }

    @Override
    public NextTaskEntity save(NextTaskEntity entity) {
        entity = super.save(entity);
        idxByTaskId.putIfAbsent(entity.getTask().getId(), new ArrayList<>());
        idxByTaskId.get(entity.getTask().getId()).add(entity.getId());
        idxByNextTaskId.putIfAbsent(entity.getNextTask().getId(), new ArrayList<>());
        idxByNextTaskId.get(entity.getNextTask().getId()).add(entity.getId());
        return entity;
    }

    public List<NextTaskEntity> getAllByTaskId(String taskId) {
        return idxByTaskId.getOrDefault(taskId, new ArrayList<>())
                .stream()
                .map(id -> this.getById(id).orElse(null))
                .filter(nextTask -> Objects.nonNull(nextTask) && nextTask.isActive())
                .toList();
    }

    public List<NextTaskEntity> getAllByNextTaskId(String nextTaskId) {
        return idxByNextTaskId.getOrDefault(nextTaskId, new ArrayList<>())
                .stream()
                .map(id -> this.getById(id).orElse(null))
                .filter(nextTask -> Objects.nonNull(nextTask) && nextTask.isActive())
                .toList();
    }
}
