package service.filetools;

import model.enums.TaskType;
import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import service.interfaces.HistoryManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class CSVFormat {
    private CSVFormat() {}
    public static Task taskFromString(String value) {
        String[] taskInfo = value.split(",");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy; HH:mm");
        LocalDateTime startDate = taskInfo[5].equals("null") ? null : LocalDateTime.parse(taskInfo[5], formatter);
        if (taskInfo[1].equals(TaskType.TASK.toString())) {
            return new Task(Integer.parseInt(taskInfo[0]), taskInfo[2], taskInfo[4], startDate,
                    Integer.parseInt(taskInfo[7]));
        } else if (taskInfo[1].equals(TaskType.EPIC.toString())) {
            return new Epic(Integer.parseInt(taskInfo[0]), taskInfo[2], taskInfo[4]);
        } else {
            return new Subtask(Integer.parseInt(taskInfo[0]), taskInfo[2], taskInfo[4],startDate,
                    Integer.parseInt(taskInfo[7]), Integer.parseInt(taskInfo[8]));
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
