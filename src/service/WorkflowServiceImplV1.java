package service;

import entity.Workflow;
import model.ExecuteWorkflowOutput;
import model.request.CreateWorkflowRequest;
import model.request.ExecuteWorkflowRequest;

import java.util.concurrent.*;

public class WorkflowServiceImplV1 implements WorkflowService {

    @Override
    public Workflow create(CreateWorkflowRequest createWorkflowRequest) {
        return null;
    }

    @Override
    public ExecuteWorkflowOutput execute(ExecuteWorkflowRequest executeWorkflowRequest) {
        ExecutorService tp = new ThreadPoolExecutor(10, 100, 1000,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.AbortPolicy());
        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
            // run
            return "s";
        }, tp);
        CompletableFuture
        f.handleAsync((result, exception) -> {

        })
    }
}
