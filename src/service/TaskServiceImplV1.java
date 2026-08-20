package service;

import entity.*;
import exception.InvalidRequestException;
import model.TaskInput;
import model.request.CreateTaskRequest;
import repository.inmemory.*;

import java.util.*;
import java.util.stream.Collectors;

public class TaskServiceImplV1 implements TaskService {
    private final TaskRepository taskRepository;
    private final NextTaskRepository nextTaskRepository;
    private final TaskFixedInputRepository taskFixedInputRepository;
    private final TaskRunRepository taskRunRepository;
    private final TaskRunFixedInputRepository taskRunFixedInputRepository;

    public TaskServiceImplV1(TaskRepository taskRepository, NextTaskRepository nextTaskRepository,
                             TaskFixedInputRepository taskFixedInputRepository, TaskRunRepository taskRunRepository,
                             TaskRunFixedInputRepository taskRunFixedInputRepository) {
        this.taskRepository = taskRepository;
        this.nextTaskRepository = nextTaskRepository;
        this.taskFixedInputRepository = taskFixedInputRepository;
        this.taskRunRepository = taskRunRepository;
        this.taskRunFixedInputRepository = taskRunFixedInputRepository;
    }

    @Override
    public TaskEntity create(CreateTaskRequest createTaskRequest) {
        if (!createTaskRequest.isValid()) {
            throw new InvalidRequestException("Invalid create task request", null);
        }
        TaskEntity t = taskRepository.save(createTaskRequest.toTaskEntity());
        try {
            if (Objects.nonNull(createTaskRequest.fixedInputs())) {
                taskFixedInputRepository.saveAll(createTaskRequest
                        .fixedInputs()
                        .stream()
                        .map(input -> new TaskFixedInputEntity(input, t))
                        .toList());
            }
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
                if (Objects.isNull(createTaskRequests.get(i).fixedInputs())) {
                    continue;
                }
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

    @Override
    public List<TaskRunEntity> createTaskRunEntities(WorkflowRunEntity workflow,
                                                     List<WorkflowStartingTaskEntity> startingTasks,
                                                     List<TaskInput> taskInputs) {
        List<TaskRunEntity> taskRunEntities = new ArrayList<>();
        List<TaskEntity> startingTaskEntities = startingTasks.stream()
                .map(WorkflowStartingTaskEntity::getStartingTask)
                .toList();
        Set<TaskEntity> visited = new HashSet<>(startingTaskEntities);
        Queue<TaskEntity> queue = new LinkedList<>(startingTaskEntities);
        Map<String, List<Object>> taskInputMap = new HashMap<>();
        for (TaskInput taskInput : taskInputs) {
            taskInputMap.putIfAbsent(taskInput.taskId(), new ArrayList<>());
            taskInputMap.get(taskInput.taskId()).add(taskInput);
        }
        List<TaskRunFixedInputEntity> taskRunFixedInputs = new ArrayList<>();
        while (!queue.isEmpty()) {
            TaskEntity taskEntity = queue.poll();
            TaskRunEntity tr = new TaskRunEntity(taskEntity, workflow);
            taskRunEntities.add(tr);
            if (taskInputMap.containsKey(taskEntity.getId())) {
                taskRunFixedInputs.addAll(taskInputMap.get(taskEntity.getId()).stream().map(ti ->
                        new TaskRunFixedInputEntity(ti, tr)).toList());
            } else {
                taskRunFixedInputs.addAll(taskFixedInputRepository
                        .getAllByTaskId(taskEntity.getId())
                        .stream()
                        .map(taskFixed -> new TaskRunFixedInputEntity(taskFixed.getInput(), tr))
                        .toList());
            }
            for (NextTaskEntity nextTask : nextTaskRepository.getAllByTaskId(taskEntity.getId())) {
                if (!visited.contains(nextTask.getNextTask())) {
                    queue.add(nextTask.getNextTask());
                    visited.add(nextTask.getNextTask());
                }
            }
        }
        taskRunEntities = taskRunRepository.saveAll(taskRunEntities);
        try {
            taskRunFixedInputRepository.saveAll(taskRunFixedInputs);
            return taskRunEntities;
        } catch (Exception e) {
            taskRunEntities.forEach(taskRunRepository::delete);
            throw e;
        }
    }
}
