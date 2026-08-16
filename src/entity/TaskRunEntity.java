package entity;

import model.enums.TaskStatus;

public class TaskRunEntity extends BaseEntity {
    private final TaskEntity task;
    private final WorkflowRunEntity workflowRun;
    private Object result;
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

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
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
}
