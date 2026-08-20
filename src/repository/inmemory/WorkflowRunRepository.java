package repository.inmemory;

import entity.WorkflowRunEntity;
import exception.InvalidRequestException;
import model.enums.WorkflowStatus;

public class WorkflowRunRepository extends AbstractInMemoryRepository<WorkflowRunEntity> {

    public WorkflowRunRepository() {
        super();
    }

    public WorkflowRunEntity updateStatus(WorkflowRunEntity workflowRunEntity, WorkflowStatus newWorkflowStatus) {
        if (!workflowRunEntity.getStatus().isValidStatusUpdate(newWorkflowStatus)) {
            throw new InvalidRequestException("Invalid status change", null);
        }
        workflowRunEntity.setStatus(newWorkflowStatus);
        return super.update(workflowRunEntity);
    }
}
