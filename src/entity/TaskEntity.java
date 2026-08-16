package entity;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TaskEntity extends BaseEntity {
    private final BiFunction<Map<String, Object>, List<Object>, Object> code;

    public TaskEntity(BiFunction<Map<String, Object>, List<Object>, Object> code) {
        super();
        this.code = code;
    }

    public BiFunction<Map<String, Object>, List<Object>, Object> getCode() {
        return code;
    }
}
