package model.tasks;

import model.enums.TaskType;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String taskName, String description, LocalDateTime startDate, int minutesDuration,
                   int epicId) {
        super(id, taskName, description, startDate, minutesDuration);
        this.epicId = epicId;
        setTaskType(TaskType.SUBTASK);
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return super.toString() + epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    public static int compareByDate(Subtask subtask1, Subtask subtask2) {
        return subtask1.getStartDate().compareTo(subtask2.getStartDate());
    }
}
