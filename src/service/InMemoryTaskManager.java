package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int taskId = 0;

    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public void deleteAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Удалять нечего, задач еще нет!");
            return;
        }

        tasks.clear();
    }

    @Override
    public Task getTaskById(int taskId) {
        if (tasks.isEmpty()) {
            System.out.println("Нельяз получить задачу по id, т.к вы еще не создали ни одну задачу");
            return null;
        }

        if (!tasks.containsKey(taskId)) {
            System.out.println("Извините, у нас нет задачи с таким id");
            return null;
        }

        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public Task createNewTask(String taskName, String description) {
        Task task = new Task(generateId(), taskName, description);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void updateTask(Task newTask) {
        if (tasks.isEmpty()) {
            System.out.println("Обновлять нечего, список пустой");
            return;
        }

        if (!tasks.containsKey(newTask.getId())) {
            System.out.println("Извините, у нас такой заадчи нет");
            return;
        }

        tasks.put(newTask.getId(), newTask);
    }

    @Override
    public void deleteTaskById(int taskId) {
        if (tasks.isEmpty()) {
            System.out.println("Удалять нечего, задач еще нет!");
            return;
        }

        if (!tasks.containsKey(taskId)) {
            System.out.println("Извините, у нас такой заадчи нет");
            return;
        }
        tasks.remove(taskId);
    }

    @Override
    public TaskStatus setNewTaskId(int taskId, TaskStatus newStatus) {
        if (tasks.isEmpty()) {
            System.out.println("Получать нечего, задач еще нет!");
            return null;
        }

        if (!tasks.containsKey(taskId)) {
            System.out.println("Извините, у нас такой заадчи нет");
            return null;
        }

        Task task = tasks.get(taskId);
        task.setStatus(newStatus);

        return task.getStatus();
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public void deleteAllEpics() {
        if (epics.isEmpty()) {
            System.out.println("Удалять нечего, эпиков еще нет!");
            return;
        }

        epics.clear();

        if (!subtasks.isEmpty()) {
            subtasks.clear();
        }
    }

    @Override
    public Epic getEpicById(int epicId) {
        if (epics.isEmpty()) {
            System.out.println("Нельяз получить эпик по id, т.к вы еще не создали ни один эпик");
            return null;
        }

        if (!epics.containsKey(epicId)) {
            System.out.println("Изаините, у нас нет эпика с таким id");
            return null;
        }
        historyManager.add(epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    public Epic createNewEpic(String taskName, String description) {
        Epic epic = new Epic(generateId(), taskName, description);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic newEpic) {
        if (epics.isEmpty()) {
            System.out.println("Обновлять нечего, список пустой");
            return;
        }

        if (!epics.containsKey(newEpic.getId())) {
            System.out.println("Извините, у нас такого эпика нет");
            return;
        }

        epics.put(newEpic.getId(), newEpic);
    }

    @Override
    public void deleteEpicById(int epicId) {
        if (epics.isEmpty()) {
            System.out.println("Удалять нечего, эпиков еще нет!");
            return;
        }

        if (!epics.containsKey(epicId)) {
            System.out.println("Извините, у нас нет эпика с таким id");
            return;
        }

        epics.remove(epicId);

        for (Subtask value : subtasks.values()) {
            if (value.getEpicId() == epicId) {
                subtasks.remove(value.getId());
            }
        }
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public Subtask getSubtaskById(int subtasksId) {
        if (checkExceptionForEpicsAndSubtasks(subtasksId)) {
            return null;
        }

        historyManager.add(subtasks.get(subtasksId));
        return subtasks.get(subtasksId);
    }

    @Override
    public Subtask createNewSubtask(String taskName, String description, int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Извините, у нас нет эпика с таким id");
            return null;
        }

        Epic epic = epics.get(epicId);
        HashMap<Integer, Subtask> subtasksIdLists = epic.getSubtask();
        Subtask subtask = new Subtask(generateId(), taskName, description, epicId);
        subtasksIdLists.put(subtask.getId(), subtask);
        subtasks.put(subtask.getId(), subtask);

        updateEpicStatus(epic);

        return subtask;
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        if (epics.isEmpty()) {
            System.out.println("Вы еще не создали ни эдин эпик, снчала создайте его");
            return;
        }

        if (subtasks.isEmpty()) {
            System.out.println("Вы еще не создали ни одну подзадачу, создайте ее");
            return;
        }

        if (!subtasks.containsKey(newSubtask.getId())) {
            System.out.println("Извините, у нас нет подзадачи с таким id");
            return;
        }

        Epic epic = epics.get(newSubtask.getEpicId());
        HashMap<Integer, Subtask> subtasksIdLists = epic.getSubtask();
        subtasksIdLists.put(newSubtask.getId(), newSubtask);
        subtasks.put(newSubtask.getId(), newSubtask);

        updateEpicStatus(epic);
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        if (epics.isEmpty()) {
            System.out.println("Вы еще не создали ни эдин эпик, снчала создайте его");
            return;
        }

        if (subtasks.isEmpty()) {
            System.out.println("Вы еще не создали ни одну подзадачу, создайте ее");
            return;
        }

        if (!subtasks.containsKey(subtaskId)) {
            System.out.println("Извините, у нас нет подзадачи с таким id");
            return;
        }

        Subtask subtask = subtasks.get(subtaskId);

        Epic epic = epics.get(subtask.getEpicId());
        HashMap<Integer, Subtask> subtasksIdLists = epic.getSubtask();
        subtasksIdLists.remove(subtaskId);
        subtasks.remove(subtaskId);

        updateEpicStatus(epic);
    }

    @Override
    public TaskStatus setNewSubtaskId(int subtaskId, TaskStatus newSubtaskStatus) {
        if (checkExceptionForEpicsAndSubtasks(subtaskId)) {
            return null;
        }

        Subtask subtask = subtasks.get(subtaskId);

        Epic epic = epics.get(subtask.getEpicId());
        HashMap<Integer, Subtask> subtasksIdLists = epic.getSubtask();
        Subtask epicSubtaskForSetNewStatus = subtasksIdLists.get(subtaskId);

        subtask.setStatus(newSubtaskStatus);
        epicSubtaskForSetNewStatus.setStatus(newSubtaskStatus);

        updateEpicStatus(epic);

        return subtask.getStatus();
    }

    private boolean checkExceptionForEpicsAndSubtasks(int subtasksId) {
        if (epics.isEmpty()) {
            System.out.println("Вы еще не создали ни эдин эпик, снчала создайте его");
            return true;
        }

        if (subtasks.isEmpty()) {
            System.out.println("Вы еще не создали ни одну подзадачу, создайте ее");
            return true;
        }

        if (!subtasks.containsKey(subtasksId)) {
            System.out.println("Извините, у нас нет подзадачи с таким id");
            return true;
        }
        return false;
    }

    private void updateEpicStatus(Epic epic) {
        HashMap<Integer, Subtask> subtasksIdLists = epic.getSubtask();
        int countNewStatusForTasks = 0;
        int countDoneStatusForTasks = 0;

        for (Subtask value : subtasksIdLists.values()) {
            switch (value.getStatus()) {
                case DONE:
                    countDoneStatusForTasks++;
                    break;
                case NEW:
                    countNewStatusForTasks++;
                    break;
            }
        }

        if (countNewStatusForTasks == subtasksIdLists.size()) {
            if (!epic.getStatus().equals(TaskStatus.NEW)) {
                epic.setStatus(TaskStatus.NEW);
            }
        } else if (countDoneStatusForTasks == subtasksIdLists.size()) {
            if (!epic.getStatus().equals(TaskStatus.DONE)) {
                epic.setStatus(TaskStatus.DONE);
            }
        } else {
            if (!epic.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    @Override
    public int generateId() {
        return ++taskId;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
