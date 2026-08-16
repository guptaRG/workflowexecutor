package entity;

public class WorkflowStartingTaskEntity extends BaseEntity {
    private final WorkflowEntity workflow;
    private final TaskEntity startingTask;

    public WorkflowStartingTaskEntity(WorkflowEntity workflow, TaskEntity startingTask) {
        super();
        this.workflow = workflow;
        this.startingTask = startingTask;
    }

    public WorkflowEntity getWorkflow() {
        return workflow;
    }

    public TaskEntity getStartingTask() {
        return startingTask;
    }
}
