package model;

import java.util.List;

public record ExecuteWorkflowResult(String workflowId, List<EndTaskResult> endTaskResultList) {
    public static record EndTaskResult(String taskId, Object result) {}
}
