package model.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum WorkflowStatus {
    CREATED, IN_PROGRESS, FAILED, SUCCESS;

    private static final Map<WorkflowStatus, Set<WorkflowStatus>> toStatuses = new HashMap<>();
    static {
        // FSM
        toStatuses.put(CREATED, Set.of(WorkflowStatus.IN_PROGRESS, WorkflowStatus.FAILED));
        toStatuses.put(IN_PROGRESS, Set.of(WorkflowStatus.SUCCESS, WorkflowStatus.FAILED));
    }

    public static boolean isValidStatusUpdate(WorkflowStatus fromStatus, WorkflowStatus toStatus) {
        return toStatuses.containsKey(fromStatus) && toStatuses.get(fromStatus).contains(toStatus);
    }
}
