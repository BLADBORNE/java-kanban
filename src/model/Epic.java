package model;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtask = new HashMap<>();

    public Epic(String taskName, String description, int id) {
       super(taskName, description, id);
    }

    public HashMap<Integer, Subtask> getSubtask() {
        return subtask;
    }

    public void setSubtask(HashMap<Integer, Subtask> subtask) {
        this.subtask = subtask;
    }

    //    @Override
//    public String getStatus() {
//
//        return super.getStatus();
//    }
//
//    @Override
//    public void setStatus(String status) {
//        throw new RuntimeException("Нельзя напрямую ставить статус у Эпиков");
//    }
}
