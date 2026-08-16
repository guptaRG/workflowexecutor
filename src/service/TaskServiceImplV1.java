package service;

import entity.NextTaskEntity;
import entity.TaskEntity;
import entity.TaskFixedInputEntity;
import exception.InvalidRequestException;
import model.request.CreateTaskRequest;
import repository.inmemory.NextTaskRepository;
import repository.inmemory.TaskFixedInputRepository;
import repository.inmemory.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskServiceImplV1 implements TaskService {
    private final TaskRepository taskRepository;
    private final NextTaskRepository nextTaskRepository;
    private final TaskFixedInputRepository taskFixedInputRepository;

    public TaskServiceImplV1(TaskRepository taskRepository, NextTaskRepository nextTaskRepository,
                             TaskFixedInputRepository taskFixedInputRepository) {
        this.taskRepository = taskRepository;
        this.nextTaskRepository = nextTaskRepository;
        this.taskFixedInputRepository = taskFixedInputRepository;
    }

    @Override
    public TaskEntity create(CreateTaskRequest createTaskRequest) {
        if (!createTaskRequest.isValid()) {
            throw new InvalidRequestException("Invalid create task request", null);
        }
        TaskEntity t = taskRepository.save(createTaskRequest.toTaskEntity());
        try {
            taskFixedInputRepository.saveAll(createTaskRequest
                    .fixedInputs()
                    .stream()
                    .map(input -> new TaskFixedInputEntity(input, t))
                    .toList());
            return t;
        } catch (Exception e) {
            taskRepository.delete(t);
            throw e;
        }
    }

    @Override
    public List<TaskEntity> createAll(List<CreateTaskRequest> createTaskRequests) {
        if (!createTaskRequests.stream().allMatch(CreateTaskRequest::isValid)) {
            throw new InvalidRequestException("Invalid create task request", null);
        }
        List<TaskEntity> t = taskRepository.saveAll(createTaskRequests
                .stream()
                .map(CreateTaskRequest::toTaskEntity)
                .toList());
        try {
            List<TaskFixedInputEntity> fixedInputs = new ArrayList<>();
            int taskSize = createTaskRequests.size();
            for (int i = 0; i < taskSize; i++) {
                for (Object fixedInput : createTaskRequests.get(i).fixedInputs()) {
                    fixedInputs.add(new TaskFixedInputEntity(fixedInput, t.get(i)));
                }
            }
            taskFixedInputRepository.saveAll(fixedInputs);
            return t;
        } catch (Exception e) {
            t.forEach(taskRepository::delete);
            throw e;
        }
    }

    @Override
    public List<NextTaskEntity> createNextTasks(TaskEntity parent, List<TaskEntity> nextTasks) {
        if (Objects.isNull(parent) || Objects.isNull(nextTasks)) {
            throw new InvalidRequestException("Invalid create next task request", null);
        }
        return nextTasks.stream().map(t -> nextTaskRepository.save(new NextTaskEntity(parent, t))).toList();
    }
}
