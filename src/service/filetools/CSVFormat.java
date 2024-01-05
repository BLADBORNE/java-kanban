package service.filetools;

import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import service.interfaces.HistoryManager;

import java.util.ArrayList;
import java.util.List;

public final class CSVFormat {
    private CSVFormat() {}
    public static Task taskFromString(String value) {
        String[] taskInfo = value.split(",");
        if (taskInfo[1].equals("TASK")) {
            return new Task(Integer.parseInt(taskInfo[0]), taskInfo[2], taskInfo[4]);
        } else if (taskInfo[1].equals("EPIC")) {
            return new Epic(Integer.parseInt(taskInfo[0]), taskInfo[2], taskInfo[4]);
        } else {
            return new Subtask(Integer.parseInt(taskInfo[0]), taskInfo[2], taskInfo[4], Integer.parseInt(taskInfo[5]));
        }
    }

    public static String historyToString(HistoryManager manager) {
        List<Task> taskList = manager.getHistory();
        List<String> stringId = new ArrayList<>();

        for (Task task : taskList) {
            stringId.add(String.valueOf(task.getId()));
        }

        return String.join(",", stringId);
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> taskId = new ArrayList<>();
        String[] tasksStringId = value.split(",");

        for (String s : tasksStringId) {
            taskId.add(Integer.parseInt(s));
        }

        return taskId;
    }

    public static String toString(Task task) {
        return task.toString();
    }
}
