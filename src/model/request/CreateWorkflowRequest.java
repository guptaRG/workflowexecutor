package model.request;

import constants.IntegerConstants;
import exception.InvalidRequestException;

import java.util.List;
import java.util.Objects;

public record CreateWorkflowRequest(String name, String desc, List<CreateTaskRequest> startingTasks,
                                    int threadPoolCoreSize, int threadPoolMaxSize) {
    public CreateWorkflowRequest(String name, String desc, List<CreateTaskRequest> startingTasks) {
        this(name, desc, startingTasks, IntegerConstants.DEFAULT_THREAD_POOL_CORE_SIZE,
                IntegerConstants.DEFAULT_THREAD_POOL_MAX_SIZE);
    }

    public CreateWorkflowRequest(String name, String desc, List<CreateTaskRequest> startingTasks,
                                 int threadPoolCoreSize, int threadPoolMaxSize) {
        this.name = name;
        this.desc = desc;
        this.startingTasks = startingTasks;
        if (threadPoolCoreSize > IntegerConstants.MAX_THREAD_POOL_CORE_SIZE ||
                threadPoolMaxSize > IntegerConstants.MAX_THREAD_POOL_MAX_SIZE) {
            throw new InvalidRequestException(
                    "Max parallelism supported is 20 tasks. Please check the passed core and max thread pool size",
                    null);
        }
        this.threadPoolCoreSize = threadPoolCoreSize;
        this.threadPoolMaxSize = threadPoolMaxSize;
    }

    public boolean isValid() {
        return Objects.nonNull(startingTasks) && !startingTasks.isEmpty();
    }
}
