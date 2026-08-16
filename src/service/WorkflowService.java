package service;

import entity.Workflow;
import model.ExecuteWorkflowOutput;
import model.request.CreateWorkflowRequest;
import model.request.ExecuteWorkflowRequest;

public interface WorkflowService {
    Workflow create(CreateWorkflowRequest createWorkflowRequest);
    ExecuteWorkflowOutput execute(ExecuteWorkflowRequest executeWorkflowRequest);
}
