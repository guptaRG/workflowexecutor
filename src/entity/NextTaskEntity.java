package entity;

public class NextTaskEntity extends BaseEntity {
    private final TaskEntity task;
    private final TaskEntity nextTask;

    public NextTaskEntity(TaskEntity task, TaskEntity nextTask) {
        super();
        this.task = task;
        this.nextTask = nextTask;
    }

    public TaskEntity getTask() {
        return task;
    }

    public TaskEntity getNextTask() {
        return nextTask;
    }
}
