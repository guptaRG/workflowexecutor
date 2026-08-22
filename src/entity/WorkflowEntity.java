package entity;

import java.util.concurrent.ExecutorService;

public class WorkflowEntity extends BaseEntity {
    private final String name;
    private final String desc;
    private final ExecutorService threadPool;

    public WorkflowEntity(String name, String desc, ExecutorService threadPool) {
        super();
        this.name = name;
        this.desc = desc;
        this.threadPool = threadPool;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "WorkflowEntity{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
