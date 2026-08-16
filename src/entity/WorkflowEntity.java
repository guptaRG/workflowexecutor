package entity;

public class WorkflowEntity extends BaseEntity {
    private final String name;
    private final String desc;

    public WorkflowEntity(String name, String desc) {
        super();
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
