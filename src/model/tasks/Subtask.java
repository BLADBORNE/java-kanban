package model.tasks;

import model.enums.TaskType;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String taskName, String description, int epicId) {
        super(id, taskName, description);
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
}
