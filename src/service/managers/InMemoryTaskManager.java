package service.managers;

import model.enums.TaskStatus;
import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import service.exceptions.TaskNotFoundException;
import service.exceptions.TasksIntersectException;
import service.interfaces.HistoryManager;
import service.interfaces.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> dateSortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTimeOrDefault));
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

        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }

        tasks.clear();
        dateSortedTasks.clear();
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
    public Task createNewTask(String taskName, String description, LocalDateTime startDate, int minutesDuration) {
        Task task = new Task(generateId(), taskName, description, startDate, minutesDuration);
        if (checkIntersection(task)) {
            tasks.put(task.getId(), task);
            dateSortedTasks.add(task);
            return task;
        }
        return null;
    }

    @Override
    public Task updateTask(Task newTask) {
        if (tasks.isEmpty()) {
            System.out.println("Обновлять нечего, список пустой");
            return null;
        }

        if (!tasks.containsKey(newTask.getId())) {
            System.out.println("Извините, у нас такой заадчи нет");
            return null;
        }

        if (tasks.get(newTask.getId()).equals(newTask)) {
            return null;
        }

        if (checkIntersection(newTask)) {
            dateSortedTasks.remove(tasks.get(newTask.getId()));
            dateSortedTasks.add(newTask);
            tasks.put(newTask.getId(), newTask);
            return newTask;
        }
        return null;
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
        historyManager.remove(taskId);
        dateSortedTasks.remove(tasks.get(taskId));
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

        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }

        epics.clear();
        dateSortedTasks.clear();

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
    public Epic updateEpic(Epic newEpic) {
        if (epics.isEmpty()) {
            System.out.println("Обновлять нечего, список пустой");
            return null;
        }

        if (!epics.containsKey(newEpic.getId())) {
            System.out.println("Извините, у нас такого эпика нет");
            return null;
        }

        if (epics.get(newEpic.getId()).equals(newEpic)) {
            return null;
        }

        HashMap<Integer, Subtask> oldSubtasks = getEpicById(newEpic.getId()).getSubtask();
        epics.put(newEpic.getId(), newEpic);
        newEpic.setSubtask(oldSubtasks);

        return newEpic;
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

        HashMap<Integer, Subtask> epicSubtasks = epics.get(epicId).getSubtask();

        historyManager.remove(epicId);
        epics.remove(epicId);

        epicSubtasks.keySet().forEach(subtaskId -> {
            dateSortedTasks.remove(subtasks.get(subtaskId));
            subtasks.remove(subtaskId);
        });
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public void deleteAllSubtasks() {
        if (subtasks.isEmpty()) {
            System.out.println("Удалять нечего, подзадач еще нет!");
            return;
        }

        subtasks.values().forEach(subtask -> {
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtask().clear();
        });

        subtasks.clear();
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
    public Subtask createNewSubtask(String taskName, String description, LocalDateTime startDate, int minutesDuration,
                                    int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Извините, у нас нет эпика с таким id");
            return null;
        }

        Subtask subtask = new Subtask(generateId(), taskName, description, startDate, minutesDuration, epicId);
        if (checkIntersection(subtask)) {
            Epic epic = epics.get(epicId);
            HashMap<Integer, Subtask> subtasksIdLists = epic.getSubtask();
            subtasksIdLists.put(subtask.getId(), subtask);
            subtasks.put(subtask.getId(), subtask);
            dateSortedTasks.add(subtask);

            updateEpicStatus(epic);
            updateEpicStartDateAndEndDateAndDuration(epic);

            return subtask;
        }
        return null;
    }

    @Override
    public Subtask updateSubtask(Subtask newSubtask) {
        if (epics.isEmpty()) {
            System.out.println("Вы еще не создали ни эдин эпик, снчала создайте его");
            return null;
        }

        if (subtasks.isEmpty()) {
            System.out.println("Вы еще не создали ни одну подзадачу, создайте ее");
            return null;
        }

        if (!subtasks.containsKey(newSubtask.getId())) {
            System.out.println("Извините, у нас нет подзадачи с таким id");
            return null;
        }

        if (!epics.containsKey(newSubtask.getEpicId())) {
            System.out.println("Извините, у нас нет эпика с таким id");
            return null;
        }

        if (subtasks.get(newSubtask.getId()).equals(newSubtask)) {
            return null;
        }

        if (checkIntersection(newSubtask)) {
            if (subtasks.get(newSubtask.getId()).getEpicId() != newSubtask.getEpicId()) {
                Subtask prevsSubtask = subtasks.get(newSubtask.getId());
                Epic prevEpic = epics.get(prevsSubtask.getEpicId());
                prevEpic.getSubtask().remove(prevsSubtask.getId());
                updateEpicStatus(prevEpic);
                updateEpicStartDateAndEndDateAndDuration(prevEpic);

                Epic newEpic = epics.get(newSubtask.getEpicId());
                newEpic.getSubtask().put(newSubtask.getId(), newSubtask);
                updateEpicStatus(newEpic);
                updateEpicStartDateAndEndDateAndDuration(newEpic);
            } else {
                Epic epic = epics.get(newSubtask.getEpicId());
                HashMap<Integer, Subtask> subtasksIdLists = epic.getSubtask();
                subtasksIdLists.put(newSubtask.getId(), newSubtask);
                updateEpicStatus(epic);
                updateEpicStartDateAndEndDateAndDuration(epic);
            }

            dateSortedTasks.remove(subtasks.get(newSubtask.getId()));
            dateSortedTasks.add(newSubtask);
            subtasks.put(newSubtask.getId(), newSubtask);
            return newSubtask;
        }
        return null;
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

        dateSortedTasks.remove(subtask);
        historyManager.remove(subtaskId);
        subtasks.remove(subtaskId);

        updateEpicStatus(epic);
        updateEpicStartDateAndEndDateAndDuration(epic);
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

    private void updateEpicStartDateAndEndDateAndDuration(Epic epic) {
        updateEpicStartDate(epic);
        updateEpicLastDate(epic);
        updateEpicDuration(epic);
    }

    private void updateEpicStartDate(Epic epic) {
        Map<Integer, Subtask> epicSubtasks = epic.getSubtask();
        if (epicSubtasks.isEmpty()) {
            epic.setStartDate(null);
            return;
        }
        List<Subtask> subtaskList = new ArrayList<>(epicSubtasks.values());
        Optional<Subtask> earliestSubtask = subtaskList.stream()
                .min(Comparator.comparing(Subtask::getStartDate));
        epic.setStartDate(earliestSubtask.orElseThrow(() -> new TaskNotFoundException("Подзадача не найдена")).
                getStartDate());
    }

    private void updateEpicDuration(Epic epic) {
        Map<Integer, Subtask> epicSubtasks = epic.getSubtask();
        if (epicSubtasks.isEmpty()) {
            epic.setMinutesDuration(null);
            return;
        }
        Duration epicDuration = Duration.ZERO;
        for (Subtask subtask : epicSubtasks.values()) {
            Duration duration = Duration.ofMinutes(subtask.getMinutesDuration());
            epicDuration = epicDuration.plus(duration);
        }
        epic.setMinutesDuration(epicDuration);
    }

    private void updateEpicLastDate(Epic epic) {
        Map<Integer, Subtask> epicSubtasks = epic.getSubtask();
        if (epicSubtasks.isEmpty()) {
            epic.setEndDate(null);
            return;
        }
        List<Subtask> subtaskList = new ArrayList<>(epicSubtasks.values());
        Optional<Subtask> latestSubtask = subtaskList.stream()
                .max(Comparator.comparing(Subtask::getStartDate));
        epic.setEndDate(latestSubtask.orElseThrow(() -> new TaskNotFoundException("Подзадача не найдена")).getEndDate());
    }

    private boolean checkIntersection(Task newTask) {
        List<Task> allTasks = getPrioritizedTasks();
        List<Integer> allTasksId = getPrioritizedTasks().stream().map(Task::getId).collect(Collectors.toList());

        if (allTasksId.contains(newTask.getId())) {
            Task curTask = allTasks.stream().filter(task -> task.getId() == newTask.getId()).findFirst().
                    orElseThrow(() -> new TaskNotFoundException("Задача не найдена"));
            if (curTask.getStartDate().equals(newTask.getStartDate()) && curTask.getEndDate().
                    equals(newTask.getEndDate())) {
                return true;
            } else {
                allTasks.remove(curTask);
                try {
                    return checkIntersectionForAllTasks(newTask, allTasks);
                } catch (TasksIntersectException e) {
                    System.out.println(e.getMessage());
                    return false;
                }
            }
        } else {
            try {
                return checkIntersectionForAllTasks(newTask, allTasks);
            } catch (TasksIntersectException e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
    }

    private boolean checkIntersectionForAllTasks(Task newTask, List<Task> allTasks) {
        for (Task task : allTasks) {
            if (notAllowToCreateTask(newTask, task))
                throw new TasksIntersectException("При создании, задачи не должны пересекаться!\nВыберите другое время");
        }
        return true;
    }

    private boolean notAllowToCreateTask(Task newTask, Task task) {
        return ((newTask.getStartDate().equals(task.getStartDate()) || newTask.getStartDate().equals(task.
                getEndDate())) || (newTask.getStartDate().isAfter(task.getStartDate()) && newTask.
                getStartDate().isBefore(task.getEndDate()))) || ((newTask.getEndDate().equals(task.
                getStartDate()) || newTask.getEndDate().equals(task.getEndDate())) || (newTask.getEndDate().
                isAfter(task.getStartDate()) && newTask.getEndDate().isBefore(task.getEndDate())));
    }


    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public int generateId() {
        return ++taskId;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(dateSortedTasks);
    }
}
