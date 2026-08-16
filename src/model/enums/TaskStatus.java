package model.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum TaskStatus {
    CREATED, IN_PROGRESS, FAILED, SUCCESS;

    private static final Map<TaskStatus, Set<TaskStatus>> toStatuses = new HashMap<>();
    static {
        // FSM
        toStatuses.put(CREATED, Set.of(TaskStatus.IN_PROGRESS, TaskStatus.FAILED));
        toStatuses.put(IN_PROGRESS, Set.of(TaskStatus.SUCCESS, TaskStatus.FAILED));
    }

    public static boolean isValidStatusUpdate(TaskStatus fromStatus, TaskStatus toStatus) {
        return toStatuses.containsKey(fromStatus) && toStatuses.get(fromStatus).contains(toStatus);
    }
}
