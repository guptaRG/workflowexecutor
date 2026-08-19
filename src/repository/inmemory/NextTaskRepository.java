package repository.inmemory;

import entity.NextTaskEntity;

import java.util.*;

public class NextTaskRepository extends AbstractInMemoryRepository<NextTaskEntity> {
    private final Map<String, List<String>> idxByTaskId;

    public NextTaskRepository() {
        super();
        this.idxByTaskId = new HashMap<>();
    }

    @Override
    public NextTaskEntity save(NextTaskEntity entity) {
        entity = super.save(entity);
        idxByTaskId.putIfAbsent(entity.getTask().getId(), new ArrayList<>());
        idxByTaskId.get(entity.getTask().getId()).add(entity.getId());
        return entity;
    }

    public List<NextTaskEntity> getAllByTaskId(String taskId) {
        return idxByTaskId.getOrDefault(taskId, new ArrayList<>())
                .stream()
                .map(id -> this.getById(id).orElse(null))
                .filter(nextTask -> Objects.nonNull(nextTask) && nextTask.isActive())
                .toList();
    }
}
