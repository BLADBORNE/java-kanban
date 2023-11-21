package model;

import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtask = new HashMap<>();

    public Epic(int id, String taskName, String description) {
        super(id, taskName, description);
    }

    public HashMap<Integer, Subtask> getSubtask() {
        return subtask;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", description='" + description.length() + '\'' +
                ", status='" + status + '\'' +
                "subtask=" + subtask +
                '}';
    }
}
