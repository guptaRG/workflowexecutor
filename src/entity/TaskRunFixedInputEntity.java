package entity;

public class TaskRunFixedInputEntity extends BaseEntity {
    private final Object input;
    private final TaskRunEntity taskRun;

    public TaskRunFixedInputEntity(Object input, TaskRunEntity taskRun) {
        super();
        this.input = input;
        this.taskRun = taskRun;
    }

    public Object getInput() {
        return input;
    }

    public TaskRunEntity getTaskRun() {
        return taskRun;
    }
}
