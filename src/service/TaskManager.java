package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.HashMap;

public class TaskManager extends Printer {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int taskId = 0;
    private int epicId = 0;
    private int subtasksId = 0;

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public void deleteAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Удалять нечего, задач еще нет!");
            return;
        }

        tasks.clear();
    }

    public Task getTaskById(int taskId) {
        if (tasks.isEmpty()) {
            System.out.println("Нельяз получить задачу по id, т.к вы еще не создали ни одну задачу");
            return null;
        }

        if (!tasks.containsKey(taskId)) {
            System.out.println("Извините, у нас нет задачи с таким id");
            return null;
        }

        return tasks.get(taskId);
    }

    public Task createNewTask(String taskName, String description) {
        Task task = new Task(taskName, description, taskId);
        tasks.put(taskId++, task);
        return task;
    }

    public void updateTask(Task newTask) {
        if (tasks.isEmpty()) {
            System.out.println("Обновлять нечего, список пустой");
            return;
        }

        if (!tasks.containsKey(newTask.getId())) {
            System.out.println("Извините, у нас такой заадчи нет");
            return;
        }

        tasks.put(taskId, newTask);
    }

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

    public String setNewTaskId(int taskId, String newStatus) {
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

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public void deleteAllEpics() {
        if (epics.isEmpty()) {
            System.out.println("Удалять нечего, эпиков еще нет!");
            return;
        }

        epics.clear();
    }

    public Epic getEpicById(int epicId) {
        if (epics.isEmpty()) {
            System.out.println("Нельяз получить эпик по id, т.к вы еще не создали ни один эпик");
            return null;
        }

        if (!epics.containsKey(epicId)) {
            System.out.println("Изаините, у нас нет эпика с таким id");
            return null;
        }

        return epics.get(epicId);
    }

    public Epic createNewEpic(String taskName, String description) {
        Epic epic = new Epic(taskName, description, epicId);
        epics.put(epicId++, epic);
        return epic;
    }

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
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public Subtask getSubtaskById(int subtasksId) {
        if (checkExceptionForEpicsAndSubtasks(subtasksId)){
            return null;
        }

        return subtasks.get(subtasksId);
    }

    public Subtask createNewSubtask(String taskName, String description, int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Извините, у нас нет эпика с таким id");
            return null;
        }

        Epic epic = epics.get(epicId);
        HashMap<Integer, Subtask> subtasksIdLists = epic.getSubtask();
        Subtask subtask = new Subtask(taskName, description, subtasksId, epicId);
        subtasksIdLists.put(subtasksId, subtask);
        subtasks.put(subtasksId++, subtask);

        return subtask;
    }

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
    }

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
    }

    public String setNewSubtaskId(int subtaskId, String newSubtaskStatus) {
        if (checkExceptionForEpicsAndSubtasks(subtaskId)){
            return null;
        }

        Subtask subtask = subtasks.get(subtaskId);

        Epic epic = epics.get(subtask.getEpicId());
        HashMap<Integer, Subtask> subtasksIdLists = epic.getSubtask();
        Subtask subtaskForSetNewStatus = subtasksIdLists.get(subtaskId);

        subtask.setStatus(newSubtaskStatus);
        subtaskForSetNewStatus.setStatus(newSubtaskStatus);

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
                case "Done":
                    countDoneStatusForTasks++;
                    break;
                case "New":
                    countNewStatusForTasks++;
                    break;
            }
        }

        if (countNewStatusForTasks == subtasksIdLists.size()) {
            if (!epic.getStatus().equals("New")) {
                epic.setStatus("New");
            }
        } else if (countDoneStatusForTasks == subtasksIdLists.size()) {
            if (!epic.getStatus().equals("Done")) {
                epic.setStatus("Done");
            }
        } else {
            if (!epic.getStatus().equals("In progress")) {
                epic.setStatus("In progress");
            }
        }
    }
}
