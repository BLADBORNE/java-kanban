package model.tasks;

import model.enums.TaskStatus;
import model.enums.TasksForFile;

public class Task {
    protected int id;
    protected String taskName;
    protected String description;
    protected TaskStatus status;

    public Task(int id, String taskName, String description) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.status = TaskStatus.NEW;
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

    public TaskStatus getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    protected TasksForFile getType() {
        return TasksForFile.TASK;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,", id, getType(), taskName, status, description);
    }
}
