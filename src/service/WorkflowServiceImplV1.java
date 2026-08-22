package service;

import constants.IntegerConstants;
import entity.*;
import exception.InvalidRequestException;
import exception.TaskFailureException;
import model.ExecuteWorkflowResult;
import model.TaskInput;
import model.enums.TaskStatus;
import model.enums.WorkflowStatus;
import model.request.CreateTaskRequest;
import model.request.CreateWorkflowRequest;
import model.request.ExecuteWorkflowRequest;
import repository.inmemory.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WorkflowServiceImplV1 implements WorkflowService {
    private final WorkflowRepository workflowRepository;
    private final WorkflowRunRepository workflowRunRepository;
    private final WorkflowStartingTaskRepository workflowStartingTaskRepository;
    private final NextTaskRepository nextTaskRepository;
    private final TaskRunRepository taskRunRepository;
    private final TaskRunFixedInputRepository taskRunFixedInputRepository;
    private final TaskService taskService;

    public WorkflowServiceImplV1(WorkflowRepository workflowRepository, WorkflowRunRepository workflowRunRepository,
                                 WorkflowStartingTaskRepository workflowStartingTaskRepository,
                                 NextTaskRepository nextTaskRepository, TaskRunRepository taskRunRepository,
                                 TaskRunFixedInputRepository taskRunFixedInputRepository, TaskService taskService) {
        this.workflowRepository = workflowRepository;
        this.workflowRunRepository = workflowRunRepository;
        this.workflowStartingTaskRepository = workflowStartingTaskRepository;
        this.nextTaskRepository = nextTaskRepository;
        this.taskRunRepository = taskRunRepository;
        this.taskRunFixedInputRepository = taskRunFixedInputRepository;
        this.taskService = taskService;
    }

    @Override
    public WorkflowEntity create(CreateWorkflowRequest createWorkflowRequest) {
        if (!createWorkflowRequest.isValid()) {
            throw new InvalidRequestException("Invalid workflow create request", null);
        }
        ExecutorService threadPoolExecutor = new ThreadPoolExecutor(createWorkflowRequest.threadPoolCoreSize(),
                createWorkflowRequest.threadPoolMaxSize(), IntegerConstants.DEFAULT_THREAD_POOL_KEEP_ALIVE_TIME_SEC,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());
        WorkflowEntity workflow = workflowRepository.save(new WorkflowEntity(createWorkflowRequest.name(),
                createWorkflowRequest.desc(), threadPoolExecutor));
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
        WorkflowRunEntity workflowRunEntity = workflowRunRepository.save(new WorkflowRunEntity(workflow));
        Map<String, TaskRunEntity> taskIdToTaskRunMap = taskService.createTaskRunEntities(workflowRunEntity,
                        startingTasks, request.taskFixedInputs())
                .stream()
                .collect(Collectors.toMap(taskRun ->taskRun.getTask().getId(), Function.identity()));
        return null;
    }

    private ExecuteWorkflowResult runWorkflowTasks(List<WorkflowStartingTaskEntity> startingTasks,
                                                   WorkflowRunEntity workflowRun,
                                                   Map<String, TaskRunEntity> taskIdToTaskRunMap,
                                                   List<TaskInput> taskFixedInputs) {
        final String workflowRunId = workflowRun.getId();
        workflowRun = workflowRunRepository.updateStatus(workflowRun, WorkflowStatus.IN_PROGRESS);
        List<TaskRunEntity> startingTaskRuns = startingTasks.stream().map(t ->
                        taskRunRepository.getByWorkflowRunTaskId(workflowRunId,
                                t.getStartingTask().getId()).orElseThrow()).toList();
        Queue<TaskRunEntity> q = new LinkedList<>(startingTaskRuns);
        Set<TaskRunEntity> visited = new HashSet<>(startingTaskRuns);
        Map<String, CompletableFuture<Object>> endTaskFutures = new HashMap<>();
        try {
            while (!q.isEmpty()) {
                TaskRunEntity taskRun = q.poll();
                if (Objects.isNull(taskRun.getFutureResult())) {
                    final List<TaskRunEntity> prevTasks = nextTaskRepository.getAllByNextTaskId(taskRun.getTask().getId())
                            .stream()
                            .map(prevTask -> taskRunRepository.getByWorkflowRunTaskId(workflowRunId,
                                    prevTask.getTask().getId()).orElseThrow())
                            .toList();
                    boolean allHaveFuture = allPrevTaskQueued(prevTasks, taskRun);
                    if (!allHaveFuture) {
                        q.add(taskRun);
                        continue;
                    }
                    taskRun = taskService.runTask(taskRun, prevTasks, workflowRun);
                }
                List<NextTaskEntity> nextTasks = nextTaskRepository.getAllByTaskId(taskRun.getTask().getId());
                if (nextTasks.isEmpty()) {
                    endTaskFutures.put(taskRun.getTask().getId(), taskRun.getFutureResult());
                }
                for (NextTaskEntity nextTask : nextTasks) {
                    TaskRunEntity nextTaskRun = taskRunRepository.getByWorkflowRunTaskId(workflowRunId,
                            nextTask.getNextTask().getId()).orElseThrow();
                    if (!visited.contains(nextTaskRun)) {
                        q.add(nextTaskRun);
                        visited.add(nextTaskRun);
                    }
                }
            }
            return new ExecuteWorkflowResult(workflowRun, endTaskFutures);
        } catch (Exception e) {
            workflowRun.setFailureReason(e.getMessage());
            workflowRunRepository.updateStatus(workflowRun, WorkflowStatus.FAILED);
            return new ExecuteWorkflowResult(workflowRun, e.getMessage());
        }
    }

    private static boolean allPrevTaskQueued(List<TaskRunEntity> prevTasks, TaskRunEntity taskRun) {
        boolean allHaveFuture = true;
        for (TaskRunEntity prevTask : prevTasks) {
            if (prevTask.getStatus() == TaskStatus.FAILED) {
                throw new TaskFailureException(String.format("Failing task %s because parent task failed with %s",
                        taskRun, prevTask.getFailureReason()), null);
            }
            if (Objects.isNull(prevTask.getFutureResult())) {
                allHaveFuture = false;
            }
        }
        return allHaveFuture;
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
