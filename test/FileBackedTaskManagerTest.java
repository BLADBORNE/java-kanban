import model.tasks.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.exceptions.ManagerSaveException;
import service.managers.FileBackedTasksManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private final File file = new File("src/service/files/saveTasks.csv");
    private Epic epic;

    @BeforeEach
    public void createBackedManager() {
        taskManager = new FileBackedTasksManager(file);
    }

    @Test
    public void shouldReturnEmptyHistoryList() throws ManagerSaveException {
        FileBackedTasksManager newFileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        assertNotNull(newFileBackedTasksManager);
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    public void shouldReturnEpicWithoutSubtasks() throws ManagerSaveException {
        LocalDateTime localDateTime3 = LocalDateTime.of(2041, 12, 1, 8, 30);
        taskManager.createNewTask("Купить автомобиль", "we", localDateTime3, 1);
        epic = taskManager.createNewEpic("new Epic1", "Новый Эпик");

        FileBackedTasksManager newFileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        assertNotNull(newFileBackedTasksManager);
        assertTrue(taskManager.getEpicById(epic.getId()).getSubtask().isEmpty());
    }

    @Test
    public void shouldSaveAndLoadEmptyTasksAndHistory() throws ManagerSaveException {
        LocalDateTime localDateTime3 = LocalDateTime.of(2041, 12, 1, 8, 30);
        taskManager.createNewTask("Купить автомобиль", "we", localDateTime3, 1);
        epic = taskManager.createNewEpic("new Epic1", "Новый Эпик");

        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();

        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (br.ready()) {
                lines.add(br.readLine());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Упс... Во время работы с файлом произошла ошибка");
        }

        assertEquals(1, lines.size());
        assertEquals(lines.get(0), "id,type,name,status,description,startDate,endDate,duration,epic");

        FileBackedTasksManager newFileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);

        assertEquals(0, newFileBackedTasksManager.getHistory().size());
        assertEquals(0, newFileBackedTasksManager.getTasks().size());
        assertEquals(0, newFileBackedTasksManager.getEpics().size());
        assertEquals(0, newFileBackedTasksManager.getSubtasks().size());
    }
}
