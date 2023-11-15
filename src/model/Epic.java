package model;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, ArrayList<Subtask>> subtask = new HashMap<>();

    public Epic(String taskName, String description, int id) {
       super(taskName, description, id);
    }

    public HashMap<Integer, ArrayList<Subtask>> getSubtask() {
        return subtask;
    }

    public void setSubtask(HashMap<Integer, ArrayList<Subtask>> subtask) {
        this.subtask = subtask;
    }


}
