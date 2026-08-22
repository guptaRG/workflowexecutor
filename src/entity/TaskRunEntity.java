package entity;

import model.enums.TaskStatus;

import java.util.concurrent.CompletableFuture;

public class TaskRunEntity extends BaseEntity {
    private final TaskEntity task;
    private final WorkflowRunEntity workflowRun;
    private CompletableFuture<Object> futureResult;
    private TaskStatus status;
    private String failureReason;

    public TaskRunEntity(TaskEntity task, WorkflowRunEntity workflowRun) {
        super();
        this.task = task;
        this.workflowRun = workflowRun;
        this.status = TaskStatus.CREATED;
    }

    public TaskEntity getTask() {
        return task;
    }

    public WorkflowRunEntity getWorkflowRun() {
        return workflowRun;
    }

    public CompletableFuture<Object> getFutureResult() {
        return futureResult;
    }

    public void setFutureResult(CompletableFuture<Object> futureResult) {
        this.futureResult = futureResult;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    @Override
    public String toString() {
        return "TaskRunEntity{" +
                "task=" + task +
                ", workflowRun=" + workflowRun +
                ", status=" + status +
                '}';
    }
}
