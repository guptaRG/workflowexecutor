package service;

import entity.*;
import model.TaskInput;
import model.request.CreateTaskRequest;

import java.util.List;

public interface TaskService {
    TaskEntity create(CreateTaskRequest createTaskRequest);
    List<TaskEntity> createAll(List<CreateTaskRequest> createTaskRequests);
    List<NextTaskEntity> createNextTasks(TaskEntity parent, List<TaskEntity> nextTasks);
    List<TaskRunEntity> createTaskRunEntities(WorkflowRunEntity workflow,
                                              List<WorkflowStartingTaskEntity> startingTasks,
                                              List<TaskInput> taskFixedInputs);
}
