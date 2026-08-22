package service;

import entity.WorkflowEntity;
import model.ExecuteWorkflowResult;
import model.request.CreateWorkflowRequest;
import model.request.ExecuteWorkflowRequest;

import java.util.concurrent.TimeoutException;

public interface WorkflowService {
    WorkflowEntity create(CreateWorkflowRequest createWorkflowRequest);
    ExecuteWorkflowResult execute(ExecuteWorkflowRequest request);
    WorkflowEntity delete(WorkflowEntity workflowEntity) throws InterruptedException, TimeoutException;
}
