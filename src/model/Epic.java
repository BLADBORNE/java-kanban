package model;

import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtask = new HashMap<>();

    public Epic(String taskName, String description, int id) {
        super(taskName, description, id);
    }

    public HashMap<Integer, Subtask> getSubtask() {
        return subtask;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description.length() + '\'' +
                ", Status='" + Status + '\'' +
                ", id=" + id +
                ", \nsubtask=" + subtask +
                '}';
    }
}
