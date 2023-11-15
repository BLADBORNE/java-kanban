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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id && Objects.equals(taskName, task.taskName) && Objects.equals(description, task.description) && Objects.equals(Status, task.Status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, description, Status, id);
    }
}
