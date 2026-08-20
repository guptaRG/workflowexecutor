package model.request;

import model.TaskInput;
import util.StringUtil;

import java.util.List;

public record ExecuteWorkflowRequest(String workflowId, List<TaskInput> taskFixedInputs) {

    public boolean isValid() {
        return !StringUtil.isEmpty(workflowId);
    }
}
