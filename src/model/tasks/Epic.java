package model.tasks;

import model.enums.TaskType;

import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtask = new HashMap<>();

    public Epic(int id, String taskName, String description) {
        super(id, taskName, description);
        setTaskType(TaskType.EPIC);
    }

    public HashMap<Integer, Subtask> getSubtask() {
        return subtask;
    }
}
