package entity;

import model.enums.WorkflowStatus;

import java.util.List;

public class Workflow extends BaseEntity {
    private WorkflowStatus status;
    private final List<Task> initTasks;
    private final String failureReason;

    public Workflow(List<Task> initTasks, String failureReason) {
        this.initTasks = initTasks;
        this.failureReason = failureReason;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }
}
