package model.request;

import entity.TaskEntity;
import entity.TaskFixedInputEntity;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public record CreateTaskRequest(BiFunction<Map<String, Object>, List<Object>, Object> code,
                                List<CreateTaskRequest> nextTasks, List<Object> fixedInputs, String name) {

    public TaskEntity toTaskEntity() {
        return new TaskEntity(code, name);
    }

    public boolean isValid() {
        return Objects.nonNull(code);
    }
}
