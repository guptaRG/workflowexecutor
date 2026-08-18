package repository.inmemory;

import entity.NextTaskEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                .map(id -> this.getById(id).orElseThrow())
                .toList();
    }
}
