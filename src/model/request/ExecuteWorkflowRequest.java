package model.request;

import java.util.List;

public record ExecuteWorkflowRequest(String workflowId, List<TaskRunFixedInputRequest> taskFixedInputs) {
    public static record TaskRunFixedInputRequest(String taskId, Object input) {}
}
