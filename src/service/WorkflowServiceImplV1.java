package service;

import entity.TaskEntity;
import entity.WorkflowEntity;
import entity.WorkflowStartingTaskEntity;
import exception.InvalidRequestException;
import model.request.CreateTaskRequest;
import model.request.CreateWorkflowRequest;
import repository.inmemory.WorkflowRepository;
import repository.inmemory.WorkflowRunRepository;
import repository.inmemory.WorkflowStartingTaskRepository;

import java.util.*;

public class WorkflowServiceImplV1 implements WorkflowService {
    private final WorkflowRepository workflowRepository;
    private final WorkflowRunRepository workflowRunRepository;
    private final WorkflowStartingTaskRepository workflowStartingTaskRepository;
    private final TaskService taskService;

    public WorkflowServiceImplV1(WorkflowRepository workflowRepository, WorkflowRunRepository workflowRunRepository,
                                 WorkflowStartingTaskRepository workflowStartingTaskRepository,
                                 TaskService taskService) {
        this.workflowRepository = workflowRepository;
        this.workflowRunRepository = workflowRunRepository;
        this.workflowStartingTaskRepository = workflowStartingTaskRepository;
        this.taskService = taskService;
    }

    @Override
    public WorkflowEntity create(CreateWorkflowRequest createWorkflowRequest) {
        if (!createWorkflowRequest.isValid()) {
            throw new InvalidRequestException("Invalid workflow create request", null);
        }
        WorkflowEntity workflow = workflowRepository.save(new WorkflowEntity(createWorkflowRequest.name(),
                createWorkflowRequest.desc()));
        try {
            createWorkflowTasks(createWorkflowRequest, workflow);
            return workflow;
        } catch (Exception e) {
            workflowRepository.delete(workflow);
            throw e;
        }
    }

    private void createWorkflowTasks(CreateWorkflowRequest createWorkflowRequest, WorkflowEntity workflowEntity) {
        // Run BFS
        List<TaskEntity> startingTasks = taskService.createAll(createWorkflowRequest.startingTasks());
        Set<CreateTaskRequest> visited = new HashSet<>(createWorkflowRequest.startingTasks());
        Queue<CreateTaskRequest> queue = new LinkedList<>(createWorkflowRequest.startingTasks());
        Map<CreateTaskRequest, TaskEntity> requestToEntityMap = new HashMap<>();
        int startingTasksCount = startingTasks.size();
        for (int i = 0; i < startingTasksCount; i++) {
            requestToEntityMap.put(createWorkflowRequest.startingTasks().get(i), startingTasks.get(i));
        }
        workflowStartingTaskRepository.saveAll(startingTasks
                .stream()
                .map(startingTask -> new WorkflowStartingTaskEntity(workflowEntity, startingTask))
                .toList());
        while (!queue.isEmpty()) {
            CreateTaskRequest current = queue.poll();
            List<TaskEntity> nextTaskEntities = new ArrayList<>();
            for (CreateTaskRequest taskRequest : current.nextTasks()) {
                if (!visited.contains(taskRequest)) {
                    visited.add(taskRequest);
                    queue.add(taskRequest);
                    requestToEntityMap.put(taskRequest, taskService.create(taskRequest));
                }
                nextTaskEntities.add(requestToEntityMap.get(taskRequest));
            }
            taskService.createNextTasks(requestToEntityMap.get(current), nextTaskEntities);
        }
    }
}
