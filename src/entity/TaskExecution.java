package entity;

import java.util.Map;
import java.util.function.Function;

public class TaskExecution<R> {
    private R result;
    private final Function<Map<String, Object>, R> function;
}
