package model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String taskName, String description, int epicId) {
        super(id, taskName, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", epicId=" + epicId +
                ", taskName='" + taskName + '\'' +
                ", description='" + description.length() + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
