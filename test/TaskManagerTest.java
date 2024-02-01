import model.tasks.Task;
import org.junit.jupiter.api.Test;
import service.interfaces.TaskManager;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected TaskManager taskManager;

    @Test
    public void createNewTask() {
        LocalDateTime localDateTime = LocalDateTime.of(2021, 12, 1, 8,30);
        Task task = new Task(1, "TestTask", "Test",localDateTime, 10);
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

}
