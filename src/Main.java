import entity.WorkflowEntity;
import model.ExecuteWorkflowResult;
import model.request.CreateTaskRequest;
import model.request.CreateWorkflowRequest;
import model.request.ExecuteWorkflowRequest;
import repository.inmemory.*;
import service.TaskService;
import service.TaskServiceImplV1;
import service.WorkflowService;
import service.WorkflowServiceImplV1;

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
            return "Merge A + B results    depends on -> Task3, Task4";
        }, null, null, "Task5");
        CreateTaskRequest taskRequest6 = new CreateTaskRequest((inputResults, fixedInputs) -> {
            System.out.println("Task 6 executing with " + inputResults);
            return "Call Service C    depends on -> Task3";
        }, null, null, "Task6");
        CreateTaskRequest taskRequest4 = new CreateTaskRequest((inputResults, fixedInputs) -> {
            System.out.println("Task 4 executing with " + inputResults);
            return "Call Service B         depends on -> Task2";
        }, List.of(taskRequest5), null, "Task4");
        CreateTaskRequest taskRequest3 = new CreateTaskRequest((inputResults, fixedInputs) -> {
            System.out.println("Task 3 executing with " + inputResults);
            return "Call Service A         depends on -> Task2";
        }, List.of(taskRequest5, taskRequest6), null, "Task3");
        CreateTaskRequest taskRequest2 = new CreateTaskRequest((inputResults, fixedInputs) -> {
            System.out.println("Task 2 executing with " + inputResults);
            return "Task2: Process input          depends on -> Task1";
        }, List.of(taskRequest3, taskRequest4), null, "Task2");
        CreateTaskRequest taskRequest1 = new CreateTaskRequest((inputResults, fixedInputs) -> {
            System.out.println("Task 1 executing with " + inputResults);
            return "Task1: Read input";
        }, List.of(taskRequest2), null, "Task1");

        WorkflowEntity workflow = workflowService.create(new CreateWorkflowRequest("WorkflowExample",
                "https://docs.google.com/document/d/1eBQK_L7HQaEAXzOStf4GNhTMimpFwTRDE-fWgMWtZQg/edit?tab=t.0",
                List.of(taskRequest1), 1, 6));
        System.out.println(workflow);
        ExecuteWorkflowResult executeWorkflowResult = workflowService.execute(new ExecuteWorkflowRequest(
                workflow.getId(), null));
        System.out.println(executeWorkflowResult);
        for (Map.Entry<String, CompletableFuture<Object>> entry : executeWorkflowResult.endTaskFutures().entrySet()) {
            String taskId = entry.getKey();
            CompletableFuture<Object> future = entry.getValue();
            future.thenAccept(result -> {
                System.out.println("Task " + taskId + " completed with result: " + result);
            }).exceptionally(ex -> {
                System.err.println("Task " + taskId + " failed with exception: " + ex.getMessage());
                return null;
            });
        }
        System.out.println(executeWorkflowResult.workflowRun());
        workflowService.delete(workflow);
    }
}
