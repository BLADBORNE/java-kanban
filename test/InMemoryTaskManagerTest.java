import org.junit.jupiter.api.BeforeEach;
import service.managers.InMemoryTaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void createTaskManager() {
        taskManager = new InMemoryTaskManager();
    }
}
