package model.tasks;

import model.enums.TaskType;

import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtask = new HashMap<>();

    public Epic(int id, String taskName, String description) {
        super(id, taskName, description, null, null);
        setTaskType(TaskType.EPIC);
    }

    public HashMap<Integer, Subtask> getSubtask() {
        return subtask;
    }

    public void setSubtask(HashMap<Integer, Subtask> subtask) {
        this.subtask = subtask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtask, epic.subtask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtask);
    }
}
