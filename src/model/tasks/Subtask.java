package model.tasks;

import model.enums.TasksForFile;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String taskName, String description, int epicId) {
        super(id, taskName, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TasksForFile getType() {
        return TasksForFile.SUBTASK;
    }

    @Override
    public String toString() {
        return super.toString() + epicId;
    }
}
