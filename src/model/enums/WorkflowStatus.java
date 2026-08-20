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

    public boolean isValidStatusUpdate(WorkflowStatus toStatus) {
        return toStatuses.containsKey(this) && toStatuses.get(this).contains(toStatus);
    }
}
