package model;

import java.util.Objects;

public class Task {
    protected String taskName;
    protected String description;
    protected String Status = "New";
    protected int id;

    public Task(String taskName, String description, int id) {
        this.taskName = taskName;
        this.description = description;
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return Status;
    }

    public int getId() {
        return id;
    }

    public void setStatus(String status) {
        Status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description.length() + '\'' +
                ", Status='" + Status + '\'' +
                ", id=" + id +
                '}';
    }
}
