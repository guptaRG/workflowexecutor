package service;

import entity.WorkflowEntity;
import model.ExecuteWorkflowResult;
import model.request.CreateWorkflowRequest;
import model.request.ExecuteWorkflowRequest;

public interface WorkflowService {
    WorkflowEntity create(CreateWorkflowRequest createWorkflowRequest);
    ExecuteWorkflowResult execute(ExecuteWorkflowRequest request);
}
