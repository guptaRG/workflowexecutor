package service;

import entity.*;
import exception.InvalidRequestException;
import model.ExecuteWorkflowResult;
import model.TaskInput;
import model.request.CreateTaskRequest;
import model.request.CreateWorkflowRequest;
import model.request.ExecuteWorkflowRequest;
import repository.inmemory.NextTaskRepository;
import repository.inmemory.WorkflowRepository;
import repository.inmemory.WorkflowRunRepository;
import repository.inmemory.WorkflowStartingTaskRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WorkflowServiceImplV1 implements WorkflowService {
    private final WorkflowRepository workflowRepository;
    private final WorkflowRunRepository workflowRunRepository;
    private final WorkflowStartingTaskRepository workflowStartingTaskRepository;
    private final NextTaskRepository nextTaskRepository;
    private final TaskService taskService;

    public WorkflowServiceImplV1(WorkflowRepository workflowRepository, WorkflowRunRepository workflowRunRepository,
                                 WorkflowStartingTaskRepository workflowStartingTaskRepository,
                                 NextTaskRepository nextTaskRepository, TaskService taskService) {
        this.workflowRepository = workflowRepository;
        this.workflowRunRepository = workflowRunRepository;
        this.workflowStartingTaskRepository = workflowStartingTaskRepository;
        this.nextTaskRepository = nextTaskRepository;
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

    @Override
    public ExecuteWorkflowResult execute(ExecuteWorkflowRequest request) {
        if (!request.isValid()) {
            throw new InvalidRequestException("Invalid execute workflow request", null);
        }
        WorkflowEntity workflow = workflowRepository.getById(request.workflowId()).orElseThrow();
        List<WorkflowStartingTaskEntity> startingTasks = workflowStartingTaskRepository.getByWorkflowId(request
                .workflowId());
        Map<String, TaskInput> taskInputMap = request.startingTaskInputs()
                .stream()
                .collect(Collectors.toMap(TaskInput::taskId, Function.identity()));
        validateStartingInputs(taskInputMap, startingTasks);
        WorkflowRunEntity workflowRunEntity = workflowRunRepository.save(new WorkflowRunEntity(workflow));
        Map<String, TaskRunEntity> taskIdToTaskRunMap = taskService.createTaskRunEntities(workflowRunEntity, startingTasks)
                .stream()
                .collect(Collectors.toMap(taskRun -> taskRun.getTask().getId(), Function.identity()));
        return null;
    }

    private ExecuteWorkflowResult runWorkflowTasks(List<WorkflowStartingTaskEntity> startingTasks,
                                  Map<String, TaskRunEntity> taskIdToTaskRunMap, List<TaskInput> taskFixedInputs) {
        
    }

    private void validateStartingInputs(Map<String, TaskInput> taskInputRequestMap,
                                        List<WorkflowStartingTaskEntity> startingTasks) {
        for (WorkflowStartingTaskEntity startingTask : startingTasks) {
            if (!taskInputRequestMap.containsKey(startingTask.getStartingTask().getId())) {
                throw new InvalidRequestException(String.format("Missing starting task %s",
                        startingTask.getStartingTask().getId()), null);
            }
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
