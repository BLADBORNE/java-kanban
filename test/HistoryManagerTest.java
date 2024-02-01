import model.tasks.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.interfaces.HistoryManager;
import service.managers.InMemoryHistoryManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    private static HistoryManager historyManager;
    private static Task task1;
    private static Task task2;
    private static Task task3;

    @BeforeAll
    public static void create3Tasks() {
        LocalDateTime localDateTime1 = LocalDateTime.of(1999, 12, 1, 8, 30);
        LocalDateTime localDateTime2 = LocalDateTime.of(2021, 12, 1, 8, 30);
        LocalDateTime localDateTime3 = LocalDateTime.of(2041, 12, 1, 8, 30);

        task1 = new Task(1, "TestTask1", "Test1", localDateTime1, 10);
        task2 = new Task(2, "TestTask2", "Test2", localDateTime2, 20);
        task3 = new Task(3, "TestTask3", "Test3", localDateTime3, 30);
    }

    @BeforeEach
    public void createHistoryManager() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void add() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
    }

    @Test
    public void shouldReturnEmptyTasksHistory() {
        assertNotNull(historyManager.getHistory());
        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    public void shouldDeleteFirstTask() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task3.getId());

        final List<Task> history = historyManager.getHistory();
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task2, history.get(0));
    }

    @Test
    public void shouldDeleteLastTask() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getId());

        final List<Task> history = historyManager.getHistory();
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task2, history.get(1));
    }

    @Test
    public void shouldDeleteTask2() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());

        final List<Task> history = historyManager.getHistory();
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task1, history.get(1));
    }

    @Test
    void shouldReplaceATaskThatAlreadyExists() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        LocalDateTime localDateTime4 = LocalDateTime.of(2000, 5, 5, 10, 30);
        Task newTask2 = new Task(2, "newTestTask2", "newTest2", localDateTime4, 2);
        historyManager.add(newTask2);
        final List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(newTask2, history.get(0));
    }
}
