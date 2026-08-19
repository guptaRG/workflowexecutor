package model.request;

import model.TaskInput;
import util.StringUtil;

import java.util.List;
import java.util.Objects;

public record ExecuteWorkflowRequest(String workflowId, List<TaskInput> taskFixedInputs,
                                     List<TaskInput> startingTaskInputs) {

    public boolean isValid() {
        return !StringUtil.isEmpty(workflowId) && Objects.nonNull(startingTaskInputs) && !startingTaskInputs.isEmpty();
    }
}
