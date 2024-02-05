import model.enums.TaskStatus;
import model.enums.TaskType;
import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import org.junit.jupiter.api.Test;
import service.exceptions.TasksIntersectException;
import service.interfaces.TaskManager;

import java.lang.reflect.Executable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected TaskManager taskManager;

    @Test
    public void shouldCreateNewTask() {
        LocalDateTime localDateTime = LocalDateTime.of(2021, 12, 1, 8, 30);
        Task task = new Task(1, "TestTask", "Test", localDateTime, 10);
        taskManager.createNewTask(task.getTaskName(), task.getDescription(), task.getStartDate(),
                task.getMinutesDuration());

        Task task2 = taskManager.getTaskById(1);
        assertNotNull(task2, "Задача не найдена.");
        assertEquals(task, task2, "Задачи не совпадают.");

        HashMap<Integer, Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    public void shouldSetInProgressStatusForCurrentTask() {
        LocalDateTime localDateTime1 = LocalDateTime.of(2041, 12, 1, 8, 30);
        Task task = taskManager.createNewTask("Купить автомобиль", "we", localDateTime1, 1);
        TaskStatus newTaskStatus = TaskStatus.IN_PROGRESS;

        TaskStatus newStatus = taskManager.setNewTaskId(task.getId(), newTaskStatus);
        assertNotNull(newStatus);
        assertEquals(newTaskStatus, newStatus);
        assertEquals(newTaskStatus, taskManager.getTasks().get(task.getId()).getStatus());

        Task getTask = taskManager.getTaskById(task.getId());
        assertEquals(newTaskStatus, getTask.getStatus());
    }

    @Test
    public void shouldDeleteTaskById() {
        LocalDateTime localDateTime1 = LocalDateTime.of(2041, 12, 1, 8, 30);
        taskManager.createNewTask("Купить автомобиль", "we", localDateTime1, 1);
        LocalDateTime localDateTime2 = LocalDateTime.of(2021, 12, 1, 8, 30);
        Task task2 = taskManager.createNewTask("Посмотреть видео на ютубе",
                "Посмотреть последнее видео Димы Масленникова", localDateTime2, 1);

        taskManager.deleteTaskById(task2.getId());
        HashMap<Integer, Task> allTasks = taskManager.getTasks();
        assertNotNull(allTasks);
        assertEquals(1, allTasks.size());
        assertFalse(allTasks.containsValue(task2));
    }

    @Test
    public void shouldDeleteAllTasks() {
        LocalDateTime localDateTime1 = LocalDateTime.of(2041, 12, 1, 8, 30);
        taskManager.createNewTask("Купить автомобиль", "we", localDateTime1, 1);
        LocalDateTime localDateTime2 = LocalDateTime.of(2021, 12, 1, 8, 30);
        taskManager.createNewTask("Посмотреть видео на ютубе",
                "Посмотреть последнее видео Димы Масленникова", localDateTime2, 1);

        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void shouldCreateNewEpic() {
        Epic epic = new Epic(1, "new Epic1", "Новый Эпик");
        taskManager.createNewEpic(epic.getTaskName(), epic.getDescription());

        Epic epic2 = taskManager.getEpicById(epic.getId());
        assertNotNull(epic2, "Эпик не найден.");
        assertEquals(epic, epic2, "Эпики не совпадают.");

        HashMap<Integer, Epic> epics = taskManager.getEpics();
        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(epic.getId()), "Эпики не совпадают.");
    }

    @Test
    public void shouldReturnNewEpicStatusWithEmptySubtaskList() {
        Epic epic = taskManager.createNewEpic("new Epic1", "Новый Эпик");

        assertNotNull(epic);
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void shouldReturnNewEpicStatusAndAllSubtasksStatusIsNew() {
        LocalDateTime localDateTime1 = LocalDateTime.of(1999, 12, 1, 8, 30);
        LocalDateTime localDateTime2 = LocalDateTime.of(2021, 12, 1, 8, 30);
        Epic epic = taskManager.createNewEpic("new Epic1", "Новый Эпик");

        assertNotNull(epic);
        assertEquals(TaskStatus.NEW, epic.getStatus());

        taskManager.createNewSubtask("New Subtask", "Подзадача", localDateTime1,
                5, epic.getId());

        assertEquals(TaskStatus.NEW, epic.getStatus());

        taskManager.createNewSubtask("New Subtask2", "Подзадача2", localDateTime2, 11,
                epic.getId());

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void shouldReturnDoneEpicStatusAndAllSubtasksStatusIsDone() {
        LocalDateTime localDateTime1 = LocalDateTime.of(1999, 12, 1, 8, 30);
        LocalDateTime localDateTime2 = LocalDateTime.of(2021, 12, 1, 8, 30);
        Epic epic = taskManager.createNewEpic("new Epic1", "Новый Эпик");

        assertNotNull(epic);
        assertEquals(TaskStatus.NEW, epic.getStatus());

        Subtask subtask1 = taskManager.createNewSubtask("New Subtask", "Подзадача", localDateTime1,
                5, epic.getId());
        Subtask subtask2 = taskManager.createNewSubtask("New Subtask2", "Подзадача2", localDateTime2, 11,
                epic.getId());

        assertEquals(TaskStatus.NEW, epic.getStatus());
        TaskStatus doneSubtaskStatus = TaskStatus.DONE;

        TaskStatus newSubtaskStatus1 = taskManager.setNewSubtaskId(subtask1.getId(), doneSubtaskStatus);
        assertNotNull(newSubtaskStatus1);
        assertEquals(doneSubtaskStatus, newSubtaskStatus1);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());

        TaskStatus newSubtaskStatus2 = taskManager.setNewSubtaskId(subtask2.getId(), doneSubtaskStatus);
        assertNotNull(newSubtaskStatus2);
        assertEquals(doneSubtaskStatus, newSubtaskStatus2);
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void shouldReturnInProgressEpicStatusAndOneSubtaskStatusIsDoneAndSecondStatusSubtaskIsNew() {
        LocalDateTime localDateTime1 = LocalDateTime.of(1999, 12, 1, 8, 30);
        LocalDateTime localDateTime2 = LocalDateTime.of(2021, 12, 1, 8, 30);
        Epic epic = taskManager.createNewEpic("new Epic1", "Новый Эпик");

        assertNotNull(epic);
        assertEquals(TaskStatus.NEW, epic.getStatus());

        Subtask subtask1 = taskManager.createNewSubtask("New Subtask", "Подзадача", localDateTime1,
                5, epic.getId());
        taskManager.createNewSubtask("New Subtask2", "Подзадача2", localDateTime2, 11,
                epic.getId());

        assertEquals(TaskStatus.NEW, epic.getStatus());
        TaskStatus doneSubtaskStatus = TaskStatus.DONE;

        TaskStatus newSubtaskStatus1 = taskManager.setNewSubtaskId(subtask1.getId(), doneSubtaskStatus);
        assertNotNull(newSubtaskStatus1);
        assertEquals(doneSubtaskStatus, newSubtaskStatus1);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldDeleteEpicByIdWithoutSubtasks() {
        taskManager.createNewEpic("new Epic1", "Новый Эпик");
        Epic epic2 = taskManager.createNewEpic("Позаниматься", "Подкачать мышцы");

        taskManager.deleteEpicById(epic2.getId());
        HashMap<Integer, Epic> allEpics = taskManager.getEpics();
        assertNotNull(allEpics);
        assertEquals(1, allEpics.size());
        assertFalse(allEpics.containsValue(epic2));
    }

    @Test
    public void shouldDeleteEpicByIdAndDeleteEpicsSubtasks() {
        LocalDateTime localDateTime1 = LocalDateTime.of(1999, 12, 1, 8, 30);
        LocalDateTime localDateTime2 = LocalDateTime.of(2021, 12, 1, 8, 30);
        taskManager.createNewEpic("new Epic1", "Новый Эпик");
        Epic epic2 = taskManager.createNewEpic("Позаниматься", "Подкачать мышцы");

        Subtask subtask1 = taskManager.createNewSubtask("New Subtask", "Подзадача", localDateTime1,
                5, epic2.getId());
        Subtask subtask2 = taskManager.createNewSubtask("New Subtask2", "Подзадача2", localDateTime2, 11,
                epic2.getId());

        assertEquals(2, taskManager.getSubtasks().size());

        taskManager.deleteEpicById(epic2.getId());
        HashMap<Integer, Epic> allEpics = taskManager.getEpics();
        assertNotNull(allEpics);
        assertEquals(1, allEpics.size());
        assertFalse(allEpics.containsValue(epic2));

        assertEquals(0, taskManager.getSubtasks().size());
        assertFalse(taskManager.getSubtasks().containsValue(subtask1));
        assertFalse(taskManager.getSubtasks().containsValue(subtask2));
    }

    @Test
    public void shouldDeleteAllEpics() {
        taskManager.createNewEpic("new Epic1", "Новый Эпик");
        taskManager.createNewEpic("Позаниматься", "Подкачать мышцы");

        taskManager.deleteAllEpics();

        assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    public void shouldCreateNewSubtask() {
        Epic epic = taskManager.createNewEpic("new Epic1", "Новый Эпик");
        LocalDateTime localDateTime3 = LocalDateTime.of(2003, 12, 10, 10, 10);
        Subtask subtask = new Subtask(2, "Купить коробки", "Заехать в домострой",
                localDateTime3, 30, epic.getId());
        Subtask managerSubtask = taskManager.createNewSubtask("Купить коробки", "Заехать в домострой",
                localDateTime3, 30, epic.getId());

        assertNotNull(managerSubtask);
        Subtask subtaskById = taskManager.getSubtaskById(managerSubtask.getId());
        assertNotNull(subtaskById, "Подзадача не найдена.");
        assertEquals(subtask, subtaskById, "Подзадачи не совпадают.");

        HashMap<Integer, Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(managerSubtask.getId()), "Подзадачи не совпадают.");
        assertEquals(1, epic.getSubtask().size());
        assertTrue(epic.getSubtask().containsValue(managerSubtask));
    }

    @Test
    public void shouldDeleteSubtaskByIdAndDeleteThisSubtaskFromEpic() {
        Epic epic = taskManager.createNewEpic("new Epic1", "Новый Эпик");
        taskManager.createNewEpic(epic.getTaskName(), epic.getDescription());
        LocalDateTime localDateTime3 = LocalDateTime.of(2003, 12, 10, 10, 10);
        Subtask subtask = taskManager.createNewSubtask("Купить коробки", "Заехать в домострой",
                localDateTime3, 30, epic.getId());

        assertNotNull(subtask);
        assertEquals(1, epic.getSubtask().size());
        assertTrue(epic.getSubtask().containsValue(subtask));
        taskManager.deleteSubtaskById(subtask.getId());
        HashMap<Integer, Subtask> allSubtask = taskManager.getSubtasks();
        assertNotNull(allSubtask);
        assertEquals(0, allSubtask.size());
        assertFalse(allSubtask.containsValue(subtask));
        assertEquals(0, epic.getSubtask().size());
        assertFalse(epic.getSubtask().containsValue(subtask));
    }

    @Test
    public void shouldReturnPrioritizedTasks() {
        LocalDateTime localDateTime1 = LocalDateTime.of(2042, 12, 1, 8, 30);
        LocalDateTime localDateTime2 = LocalDateTime.of(1998, 12, 1, 8, 30);
        LocalDateTime localDateTime3 = LocalDateTime.of(2021, 12, 1, 8, 30);
        Task task1 = taskManager.createNewTask("Купить автомобиль", "we", localDateTime1, 1);
        Task task2 = taskManager.createNewTask("Посмотреть видео на ютубе",
                "Посмотреть последнее видео Димы Масленникова", localDateTime2, 1);
        Task task3 = taskManager.createNewTask("Погулять",
                "Прогуляться по парку", localDateTime3, 5);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertNotNull(prioritizedTasks);
        assertEquals(3, prioritizedTasks.size());
        assertTrue(prioritizedTasks.contains(task1));
        assertTrue(prioritizedTasks.contains(task2));
        assertTrue(prioritizedTasks.contains(task3));

        assertEquals(task1, prioritizedTasks.get(prioritizedTasks.size() - 1));
        assertEquals(task2, prioritizedTasks.get(0));
    }

    @Test
    public void shouldSetEarliestStartDateAndLatestEndDateForEpicFromAllSubtasks() {
        Epic epic = taskManager.createNewEpic("new Epic1", "Новый Эпик");
        LocalDateTime localDateTime1 = LocalDateTime.of(2042, 12, 1, 8, 30);
        LocalDateTime localDateTime2 = LocalDateTime.of(1998, 12, 1, 8, 30);
        LocalDateTime localDateTime3 = LocalDateTime.of(2021, 12, 1, 8, 30);
        Task subtask1 = taskManager.createNewSubtask("Купить автомобиль", "we", localDateTime1, 1, epic.getId());
        Task subtask2 = taskManager.createNewSubtask("Посмотреть видео на ютубе",
                "Посмотреть последнее видео Димы Масленникова", localDateTime2, 1, epic.getId());
        taskManager.createNewSubtask("Погулять",
                "Прогуляться по парку", localDateTime3, 5, epic.getId());
        assertEquals(subtask2.getStartDate(), epic.getStartDate());
        assertEquals(subtask1.getEndDate(), epic.getEndDate());
    }

    @Test
    public void shouldSetAllSubtasksDurationForEpic() {
        Epic epic = taskManager.createNewEpic("new Epic1", "Новый Эпик");
        LocalDateTime localDateTime1 = LocalDateTime.of(2042, 12, 1, 8, 30);
        LocalDateTime localDateTime2 = LocalDateTime.of(1998, 12, 1, 8, 30);
        LocalDateTime localDateTime3 = LocalDateTime.of(2021, 12, 1, 8, 30);
        taskManager.createNewSubtask("Купить автомобиль", "we", localDateTime1, 1, epic.getId());
        taskManager.createNewSubtask("Посмотреть видео на ютубе",
                "Посмотреть последнее видео Димы Масленникова", localDateTime2, 1, epic.getId());
        taskManager.createNewSubtask("Погулять",
                "Прогуляться по парку", localDateTime3, 5, epic.getId());
        Duration expectedDuration = Duration.ZERO;

        for (Subtask subtask : epic.getSubtask().values()) {
            expectedDuration = expectedDuration.plus(Duration.ofMinutes(subtask.getMinutesDuration()));
        }
        assertEquals(expectedDuration.toMinutes(), epic.getMinutesDuration());
    }

    @Test
    public void shouldReturnNewUpdateTaskWhenNewUpdateTasksTimeEqualsCreatedTime() {


    }

    @Test
    public void shouldReturnNewUpdateTaskWhenNewUpdateTasksTimeHasNotCrossingWithOneDifferentCreatedTask() {
        LocalDateTime localDateTime = LocalDateTime.of(2021, 12, 1, 8, 30);
        Task task = taskManager.createNewTask("TestTask", "Test", localDateTime, 10);

        Task newTask = new Task(task.getId(), "Купить автомобиль", "we", localDateTime,
                10);
        assertTrue(taskManager.getTasks().containsValue(task));
        taskManager.updateTask(newTask);
        assertFalse(taskManager.getTasks().containsValue(task));
        assertTrue(taskManager.getTasks().containsValue(newTask));
        assertEquals(newTask, taskManager.getTaskById(newTask.getId()));

    }

    @Test
    public void shouldThrownAnExceptionWhenNewUpdateTaskHasCrossingWithOneDifferentCreatedTask() {
        LocalDateTime localDateTime1 = LocalDateTime.of(2021, 12, 1, 8, 30);
        LocalDateTime localDateTime2 = LocalDateTime.of(2021, 12, 1, 8, 50);
        taskManager.createNewTask("TestTask", "Test", localDateTime1, 35);
        Task newTask = taskManager.createNewTask("Купить автомобиль", "we",
                localDateTime2, 5);
        assertNull(newTask);
    }
}
