package model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String taskName, String description, int id, int epicId) {
        super(taskName, description, id);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", subTaskName='" + taskName + '\'' +
                ", description='" + description.length() + '\'' +
                ", Status='" + Status + '\'' +
                ", id=" + id +
                '}';
    }
}
