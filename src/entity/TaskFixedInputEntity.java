package entity;

public class TaskFixedInputEntity extends BaseEntity {
    private final Object input;
    private final TaskEntity task;

    public TaskFixedInputEntity(Object input, TaskEntity task) {
        super();
        this.input = input;
        this.task = task;
    }

    public Object getInput() {
        return input;
    }

    public TaskEntity getTask() {
        return task;
    }
}
