package entity;

import model.enums.TaskStatus;
import model.request.TaskInputRequest;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class Task<R> extends BaseEntity {
    private final List<Task> nextTasks;
    private final List<Task> prevTasks;
    private R result;
    private final Function<Map<String, Object>, R> function;
    private TaskStatus status;

    public Task(List<Task> nextTasks, List<Task> prevTasks, Function function) {
        this.nextTasks = nextTasks;
        this.prevTasks = prevTasks;
        this.function = function;
    }

    public List<Task> getNextTasks() {
        return nextTasks;
    }

    public List<Task> getPrevTasks() {
        return prevTasks;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
