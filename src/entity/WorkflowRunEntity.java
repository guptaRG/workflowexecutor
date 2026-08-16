package entity;

import model.enums.WorkflowStatus;

public class WorkflowRunEntity extends BaseEntity {
    private final WorkflowEntity workflow;
    private WorkflowStatus status;
    private String failureReason;

    public WorkflowRunEntity(WorkflowEntity workflow) {
        super();
        this.workflow = workflow;
        this.status = WorkflowStatus.CREATED;
    }

    public WorkflowEntity getWorkflow() {
        return workflow;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
