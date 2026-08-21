package entity;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class TaskEntity extends BaseEntity {
    private final BiFunction<Map<String, Object>, List<Object>, Object> code;
    private final String name;

    public TaskEntity(BiFunction<Map<String, Object>, List<Object>, Object> code, String name) {
        super();
        this.code = code;
        this.name = name;
    }

    public BiFunction<Map<String, Object>, List<Object>, Object> getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "TaskEntity{" +
                "name='" + name + '\'' +
                '}';
    }
}
