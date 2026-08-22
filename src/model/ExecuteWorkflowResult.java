package model;

import constants.StringConstants;
import entity.WorkflowRunEntity;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public record ExecuteWorkflowResult(WorkflowRunEntity workflowRun,
                                    Map<String, CompletableFuture<Object>> endTaskFutures, String failureReason) {

    public ExecuteWorkflowResult(WorkflowRunEntity workflowRun, String failureReason) {
        this(workflowRun, Map.of(), failureReason);
    }

    public ExecuteWorkflowResult(WorkflowRunEntity workflowRun,
                                 Map<String, CompletableFuture<Object>> endTaskResultList) {
        this(workflowRun, endTaskResultList, StringConstants.EMPTY_STR);
    }

    @Override
    public String toString() {
        return "ExecuteWorkflowResult{" +
                "workflowRun=" + workflowRun +
                ", failureReason='" + failureReason + '\'' +
                '}';
    }
}
