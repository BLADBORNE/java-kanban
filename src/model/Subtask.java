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

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
