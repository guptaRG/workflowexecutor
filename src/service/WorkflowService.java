package service;

import entity.WorkflowEntity;
import model.request.CreateWorkflowRequest;

public interface WorkflowService {
    WorkflowEntity create(CreateWorkflowRequest createWorkflowRequest);
}
