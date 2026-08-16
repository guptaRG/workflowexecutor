package service;

import entity.NextTaskEntity;
import entity.TaskEntity;
import model.request.CreateTaskRequest;

import java.util.List;

public interface TaskService {
    TaskEntity create(CreateTaskRequest createTaskRequest);
    List<TaskEntity> createAll(List<CreateTaskRequest> createTaskRequests);
    List<NextTaskEntity> createNextTasks(TaskEntity parent, List<TaskEntity> nextTasks);
}
