import entity.TaskEntity;
import entity.WorkflowEntity;
import model.ExecuteWorkflowResult;
import model.TaskInput;
import model.request.CreateTaskRequest;
import model.request.CreateWorkflowRequest;
import model.request.ExecuteWorkflowRequest;
import repository.inmemory.*;
import service.TaskService;
import service.TaskServiceImplV1;
import service.WorkflowService;
import service.WorkflowServiceImplV1;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws InterruptedException, TimeoutException {
        Scanner sc = new Scanner(System.in);
        // Repository init
        NextTaskRepository nextTaskRepository = new NextTaskRepository();
        TaskFixedInputRepository taskFixedInputRepository = new TaskFixedInputRepository();
        TaskRepository taskRepository = new TaskRepository();
        TaskRunFixedInputRepository taskRunFixedInputRepository = new TaskRunFixedInputRepository();
        TaskRunRepository taskRunRepository = new TaskRunRepository();
        WorkflowRepository workflowRepository = new WorkflowRepository();
        WorkflowRunRepository workflowRunRepository = new WorkflowRunRepository();
        WorkflowStartingTaskRepository workflowStartingTaskRepository = new WorkflowStartingTaskRepository();

        // Service init
        TaskService taskService = new TaskServiceImplV1(taskRepository, nextTaskRepository,
                taskFixedInputRepository, taskRunRepository, taskRunFixedInputRepository);
        WorkflowService workflowService = new WorkflowServiceImplV1(workflowRepository, workflowRunRepository,
                workflowStartingTaskRepository, nextTaskRepository, taskRunRepository, taskService);

        // Entity init
        CreateTaskRequest taskRequest5 = new CreateTaskRequest((inputResults, fixedInputs) -> {
            System.out.println("Task 5 executing with " + inputResults);
            System.out.println("Task 5 fixed inputs: " + fixedInputs);
            return "Merge A + B results    depends on -> Task3, Task4";
        }, null, List.of(1, 2), "Task5");
        CreateTaskRequest taskRequest6 = new CreateTaskRequest((inputResults, fixedInputs) -> {
            System.out.println("Task 6 executing with " + inputResults);
            System.out.println("Task 6 fixed inputs: " + fixedInputs);
            return "Call Service C    depends on -> Task3";
        }, null, null, "Task6");
        CreateTaskRequest taskRequest4 = new CreateTaskRequest((inputResults, fixedInputs) -> {
            System.out.println("Task 4 executing with " + inputResults);
            System.out.println("Task 4 fixed inputs: " + fixedInputs);
            return "Call Service B         depends on -> Task2";
        }, List.of(taskRequest5), List.of(1000, 700), "Task4");
        CreateTaskRequest taskRequest3 = new CreateTaskRequest((inputResults, fixedInputs) -> {
            System.out.println("Task 3 executing with " + inputResults);
            System.out.println("Task 3 fixed inputs: " + fixedInputs);
            throw new RuntimeException("Exception while calling Service A         depends on -> Task2");
        }, List.of(taskRequest5, taskRequest6), null, "Task3");
        CreateTaskRequest taskRequest2 = new CreateTaskRequest((inputResults, fixedInputs) -> {
            System.out.println("Task 2 executing with " + inputResults);
            System.out.println("Task 2 fixed inputs: " + fixedInputs);
            return "Task2: Process input          depends on -> Task1";
        }, List.of(taskRequest3, taskRequest4), List.of(1000), "Task2");
        CreateTaskRequest taskRequest1 = new CreateTaskRequest((inputResults, fixedInputs) -> {
            System.out.println("Task 1 executing with " + inputResults);
            System.out.println("Task 1 fixed inputs: " + fixedInputs);
            return "Task1: Read input";
        }, List.of(taskRequest2), null, "Task1");

        WorkflowEntity workflow = workflowService.create(new CreateWorkflowRequest("WorkflowExample",
                "https://docs.google.com/document/d/1eBQK_L7HQaEAXzOStf4GNhTMimpFwTRDE-fWgMWtZQg/edit?tab=t.0",
                List.of(taskRequest1), 1, 6));
        System.out.println(workflow);
        List<TaskEntity> allTasks = taskRepository.getAll();
        allTasks.sort(Comparator.comparing(TaskEntity::getName));
        System.out.println(allTasks);

        ExecuteWorkflowResult executeWorkflowResult = workflowService.execute(new ExecuteWorkflowRequest(
                workflow.getId(), List.of(new TaskInput(allTasks.get(0).getId(), 10))));
        System.out.println(executeWorkflowResult);
        for (Map.Entry<String, CompletableFuture<Object>> entry : executeWorkflowResult.endTaskFutures().entrySet()) {
            String taskId = entry.getKey();
            CompletableFuture<Object> future = entry.getValue();
            try {
                Object result = future.get();
                System.out.println("Task " + taskId + " completed with result: " + result);
            } catch (Exception ex) {
                System.err.println("Task " + taskId + " failed with exception: " + ex);
            }
        }
        System.out.println(executeWorkflowResult.workflowRun());
        workflowService.delete(workflow);
    }
}
