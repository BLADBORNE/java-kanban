package model.tasks;

import model.enums.TaskStatus;
import model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected int id;
    protected String taskName;
    protected String description;
    protected TaskStatus status;
    protected TaskType taskType;
    protected Duration minutesDuration;
    protected LocalDateTime startDate;
    protected LocalDateTime endDate;

    public Task(int id, String taskName, String description, LocalDateTime startDate, Integer minutesDuration) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.taskType = TaskType.TASK;
        if (startDate == null && minutesDuration == null) {
            this.startDate = null;
            this.minutesDuration = null;
            this.endDate = null;
        } else {
            this.startDate = startDate;
            this.minutesDuration = Duration.ofMinutes(minutesDuration);
            this.endDate = startDate.plus(this.minutesDuration);
        }
    }

    public Task() {

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

    public TaskType getType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getMinutesDuration() {
        return (int) minutesDuration.toMinutes();
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }


    public void setMinutesDuration(Duration minutesDuration) {
        this.minutesDuration = minutesDuration;
    }

    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy; HH:mm");
    }

    public static LocalDateTime getStartTimeOrDefault(Task task) {
        if (task == null) {
            return LocalDateTime.MAX;
        }
        return task.getStartDate();
    }

    @Override
    public String toString() {
        String startDateStr = (startDate != null) ? startDate.format(dateTimeFormatter()) : null;
        String endDateStr = (endDate != null) ? endDate.format(dateTimeFormatter()) : null;
        String minutesDurationStr = (minutesDuration != null) ? String.valueOf(minutesDuration.toMinutes()) : null;

        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,", id, getType(), taskName, status, description,
                startDateStr, endDateStr, minutesDurationStr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(taskName, task.taskName) && Objects.equals(description, task.description) && status == task.status && taskType == task.taskType && Objects.equals(minutesDuration, task.minutesDuration) && Objects.equals(startDate, task.startDate) && Objects.equals(endDate, task.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, description, status, taskType, minutesDuration, startDate, endDate);
    }
}
