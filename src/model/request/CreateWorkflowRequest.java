package model.request;

import java.util.List;
import java.util.Objects;

public record CreateWorkflowRequest(String name, String desc, List<CreateTaskRequest> startingTasks) {

    public boolean isValid() {
        return Objects.nonNull(startingTasks) && !startingTasks.isEmpty();
    }
}
