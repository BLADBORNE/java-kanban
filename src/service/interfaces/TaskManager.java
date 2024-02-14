package service.interfaces;

import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import model.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    public HashMap<Integer, Task> getTasks();

    void deleteAllTasks();

    Task getTaskById(int taskId);

    Task createNewTask(String taskName, String description, LocalDateTime startDate, int minutesDuration);

    void updateTask(Task newTask);

    void deleteTaskById(int taskId);

    TaskStatus setNewTaskId(int taskId, TaskStatus newStatus);

    HashMap<Integer, Epic> getEpics();

    void deleteAllEpics();

    Epic getEpicById(int epicId);

    Epic createNewEpic(String taskName, String description);

    void updateEpic(Epic newEpic);

    void deleteEpicById(int epicId);

    HashMap<Integer, Subtask> getSubtasks();

    Subtask getSubtaskById(int subtasksId);

    Subtask createNewSubtask(String taskName, String description, LocalDateTime startDate, int minutesDuration,
                             int epicId);

    void updateSubtask(Subtask newSubtask);

    void deleteSubtaskById(int subtaskId);

    void deleteAllSubtasks();

    TaskStatus setNewSubtaskId(int subtaskId, TaskStatus newSubtaskStatus);

    int generateId();

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}