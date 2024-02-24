import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.exceptions.ManagerSaveException;
import service.managers.HttpTaskManager;
import service.managers.Managers;
import service.server.HttpTaskServer;
import service.server.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerAndKVServerTest {
    private static final String URL = "http://localhost:8080/tasks";
    private HttpTaskServer server;
    private KVServer kvServer;
    private HttpClient client = HttpClient.newHttpClient();


    @BeforeEach
    public void startServer() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        server = new HttpTaskServer();
        server.start();
    }

    @AfterEach
    public void stopServer() {
        server.stop();
        kvServer.stop();
    }

    @Test
    public void shouldCreateNewTaskAndKVServerTaskEqualsCreatedTask() throws ManagerSaveException {
        LocalDateTime localDateTime = LocalDateTime.of(2021, 12, 1, 8, 30);
        Task task = new Task(1, "TestTask", "Test", localDateTime, 10);

        URI uri = URI.create(URL + "/task");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(task));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        URI getTasksUri = URI.create(URL + "/task");
        HttpRequest getTasksRequest = HttpRequest.newBuilder()
                .GET()
                .uri(getTasksUri)
                .build();

        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(getTasksRequest, handler);
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject jsonTaskFromServer = jsonObject.get("1").getAsJsonObject();
            Task taskFromServer = Managers.getGson().fromJson(jsonTaskFromServer, Task.class);
            assertEquals(task, taskFromServer);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        URI getTaskUriById = URI.create(URL + "/task?id=1");
        HttpRequest getTaskRequestById = HttpRequest.newBuilder()
                .GET()
                .uri(getTaskUriById)
                .build();

        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(getTaskRequestById, handler);
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            Task taskFromServer = Managers.getGson().fromJson(jsonObject, Task.class);
            assertEquals(task, taskFromServer);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        HttpTaskManager newManager = HttpTaskManager.loadFromServer();
        assertNotNull(newManager);
        assertEquals(task, newManager.getTaskById(1));
        assertEquals(1, newManager.getTasks().size());
        assertEquals(1, newManager.getHistory().size());
        assertTrue(newManager.getHistory().contains(task));
        assertTrue(newManager.getPrioritizedTasks().contains(task));
    }

    @Test
    public void shouldUpdateCurrentTaskAndTaskOnTheServer() throws ManagerSaveException {
        LocalDateTime localDateTime = LocalDateTime.of(2021, 12, 1, 8, 30);
        Task task = new Task(1, "TestTask", "Test", localDateTime, 10);

        URI uri = URI.create(URL + "/task");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(task));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        HttpTaskManager newManager = HttpTaskManager.loadFromServer();
        assertNotNull(newManager);
        assertEquals(task, newManager.getTaskById(1));
        assertEquals(1, newManager.getTasks().size());

        Task updateTask = new Task(1, "UpdateTask", "UpdateTask", localDateTime, 10);

        URI updateUri = URI.create(URL + "/task");
        final HttpRequest.BodyPublisher updateBody = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(updateTask));
        HttpRequest updateRequest = HttpRequest.newBuilder()
                .POST(updateBody)
                .uri(updateUri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(updateRequest, handler);
            assertEquals(response.body(), "Задача успешно обновлена!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        HttpTaskManager newUpdateManager = HttpTaskManager.loadFromServer();
        assertNotNull(newUpdateManager);
        assertEquals(updateTask, newUpdateManager.getTaskById(1));
        assertEquals(1, newUpdateManager.getTasks().size());
    }

    @Test
    public void shouldDeleteTaskByIdAndDeleteTaskFromTheServer() throws ManagerSaveException {
        LocalDateTime localDateTime1 = LocalDateTime.of(2021, 12, 1, 8, 30);
        Task task = new Task(1, "TestTask", "Test", localDateTime1, 10);

        URI uri = URI.create(URL + "/task");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(task));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        LocalDateTime localDateTime2 = LocalDateTime.of(2004, 1, 28, 6, 30);
        Task task2 = new Task(2, "Task2", "Task2", localDateTime2, 10);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(task2));
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(uri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request2, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        URI deleteUri = URI.create(URL + "/task?id=2");
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(deleteUri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(deleteRequest, handler);
            assertEquals(response.body(), "Задача с id = 2 успешно удалена!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        HttpTaskManager newUpdateManager = HttpTaskManager.loadFromServer();
        assertNotNull(newUpdateManager);
        assertEquals(1, newUpdateManager.getTasks().size());
        assertFalse(newUpdateManager.getTasks().containsValue(task2));
    }

    @Test
    public void shouldDeleteAllTasksAndDeleteAllTasksFromTheServer() throws ManagerSaveException {
        LocalDateTime localDateTime1 = LocalDateTime.of(2021, 12, 1, 8, 30);
        Task task = new Task(1, "TestTask", "Test", localDateTime1, 10);

        URI uri = URI.create(URL + "/task");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(task));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        LocalDateTime localDateTime2 = LocalDateTime.of(2004, 1, 28, 6, 30);
        Task task2 = new Task(2, "Task2", "Task2", localDateTime2, 10);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(task2));
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(uri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request2, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        URI deleteUri = URI.create(URL + "/task");
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(deleteUri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(deleteRequest, handler);
            assertEquals(response.body(), "Все задачи успешно удалены!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        HttpTaskManager newUpdateManager = HttpTaskManager.loadFromServer();
        assertNotNull(newUpdateManager);
        assertEquals(0, newUpdateManager.getTasks().size());
        assertFalse(newUpdateManager.getTasks().containsValue(task2));
        assertFalse(newUpdateManager.getTasks().containsValue(task));
    }

    @Test
    public void shouldCreateNewEpicAndKVServerEpicEqualsCreatedEpic() throws ManagerSaveException {
        Epic epic = new Epic(1, "Epic", "Epic1");

        URI uri = URI.create(URL + "/epic");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(epic));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        URI getEpicUri = URI.create(URL + "/epic");
        HttpRequest getEpicsRequest = HttpRequest.newBuilder()
                .GET()
                .uri(getEpicUri)
                .build();

        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(getEpicsRequest, handler);
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject jsonEpicFromServer = jsonObject.get("1").getAsJsonObject();
            Epic epicFromServer = Managers.getGson().fromJson(jsonEpicFromServer, Epic.class);
            assertEquals(epic, epicFromServer);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        URI getEpicUriById = URI.create(URL + "/epic?id=1");
        HttpRequest getEpicRequestById = HttpRequest.newBuilder()
                .GET()
                .uri(getEpicUriById)
                .build();

        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(getEpicRequestById, handler);
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            Epic epicFromServer = Managers.getGson().fromJson(jsonObject, Epic.class);
            assertEquals(epic, epicFromServer);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        HttpTaskManager newManager = HttpTaskManager.loadFromServer();
        assertNotNull(newManager);
        assertEquals(epic, newManager.getEpicById(1));
        assertEquals(1, newManager.getEpics().size());
        assertEquals(1, newManager.getHistory().size());
        assertTrue(newManager.getHistory().contains(epic));
    }

    @Test
    public void shouldUpdateCurrentEpicAndEpicOnTheServer() throws ManagerSaveException {
        Epic epic = new Epic(1, "Epic", "Epic1");

        URI uri = URI.create(URL + "/epic");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(epic));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        HttpTaskManager newManager = HttpTaskManager.loadFromServer();
        assertNotNull(newManager);
        assertEquals(epic, newManager.getEpicById(1));
        assertEquals(1, newManager.getEpics().size());

        Epic updateEpic = new Epic(1, "NewEpic", "UpdateEpic");

        URI updateUri = URI.create(URL + "/epic");
        final HttpRequest.BodyPublisher updateBody = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(updateEpic));
        HttpRequest updateRequest = HttpRequest.newBuilder()
                .POST(updateBody)
                .uri(updateUri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(updateRequest, handler);
            assertEquals(response.body(), "Задача успешно обновлена!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        HttpTaskManager newUpdateManager = HttpTaskManager.loadFromServer();
        assertNotNull(newUpdateManager);
        assertEquals(updateEpic, newUpdateManager.getEpicById(1));
        assertEquals(1, newUpdateManager.getEpics().size());
    }

    @Test
    public void shouldDeleteEpicByIdAndDeleteEpicFromTheServer() throws ManagerSaveException {
        Epic epic = new Epic(1, "Epic", "Epic1");

        URI uri = URI.create(URL + "/epic");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(epic));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        HttpTaskManager newManager = HttpTaskManager.loadFromServer();
        assertNotNull(newManager);
        assertEquals(epic, newManager.getEpicById(1));
        assertEquals(1, newManager.getEpics().size());

        Epic epic2 = new Epic(2, "NewEpic", "Epic2");

        URI updateUri = URI.create(URL + "/epic");
        final HttpRequest.BodyPublisher updateBody = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(epic2));
        HttpRequest updateRequest = HttpRequest.newBuilder()
                .POST(updateBody)
                .uri(updateUri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(updateRequest, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        URI deleteUri = URI.create(URL + "/epic?id=2");
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(deleteUri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(deleteRequest, handler);
            assertEquals(response.body(), "Задача с id = 2 успешно удалена!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        HttpTaskManager newUpdateManager = HttpTaskManager.loadFromServer();
        assertNotNull(newUpdateManager);
        assertEquals(1, newUpdateManager.getEpics().size());
        assertFalse(newUpdateManager.getTasks().containsValue(epic2));
    }

    @Test
    public void shouldDeleteAllEpicsAndDeleteAllEpicsFromTheServer() throws ManagerSaveException {
        Epic epic = new Epic(1, "Epic", "Epic1");

        URI uri = URI.create(URL + "/epic");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(epic));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        HttpTaskManager newManager = HttpTaskManager.loadFromServer();
        assertNotNull(newManager);
        assertEquals(epic, newManager.getEpicById(1));
        assertEquals(1, newManager.getEpics().size());

        Epic epic2 = new Epic(2, "NewEpic", "Epic2");

        URI updateUri = URI.create(URL + "/epic");
        final HttpRequest.BodyPublisher updateBody = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(epic2));
        HttpRequest updateRequest = HttpRequest.newBuilder()
                .POST(updateBody)
                .uri(updateUri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(updateRequest, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        URI deleteUri = URI.create(URL + "/epic");
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(deleteUri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(deleteRequest, handler);
            assertEquals(response.body(), "Все задачи успешно удалены!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        HttpTaskManager newUpdateManager = HttpTaskManager.loadFromServer();
        assertNotNull(newUpdateManager);
        assertEquals(0, newUpdateManager.getEpics().size());
        assertFalse(newUpdateManager.getTasks().containsValue(epic));
        assertFalse(newUpdateManager.getTasks().containsValue(epic2));
    }

    @Test
    public void shouldReturnEpicsSubtask() throws ManagerSaveException {
        Epic epic = new Epic(1, "Epic", "EpicTest");

        URI Epicuri = URI.create(URL + "/epic");
        final HttpRequest.BodyPublisher EpicBody = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(epic));
        HttpRequest EpicRequest = HttpRequest.newBuilder()
                .POST(EpicBody)
                .uri(Epicuri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(EpicRequest, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        LocalDateTime localDateTime = LocalDateTime.of(2021, 12, 1, 8, 30);
        Subtask subtask = new Subtask(2, "TestTask", "Test", localDateTime, 10, 1);

        URI uri = URI.create(URL + "/subtask");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(subtask));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        URI uriGetEpicSubtask = URI.create(URL + "/subtask/epic?id=1");
        HttpRequest requestGetEpicSubtask = HttpRequest.newBuilder()
                .GET()
                .uri(uriGetEpicSubtask)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(requestGetEpicSubtask, handler);
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject jsonEpicSubtaskFromServer = jsonObject.get("2").getAsJsonObject();
            Subtask subtaskFromServer = Managers.getGson().fromJson(jsonEpicSubtaskFromServer, Subtask.class);
            assertEquals(subtask, subtaskFromServer);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }
    }

    @Test
    public void shouldCreateNewSubtaskAndKVServerSubtaskEqualsCreatedSubtask() throws ManagerSaveException {
        Epic epic = new Epic(1, "Epic", "EpicTest");

        URI Epicuri = URI.create(URL + "/epic");
        final HttpRequest.BodyPublisher EpicBody = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(epic));
        HttpRequest EpicRequest = HttpRequest.newBuilder()
                .POST(EpicBody)
                .uri(Epicuri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(EpicRequest, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        LocalDateTime localDateTime = LocalDateTime.of(2021, 12, 1, 8, 30);
        Subtask subtask = new Subtask(2, "TestSubtask", "Test", localDateTime, 10, 1);

        URI uri = URI.create(URL + "/subtask");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(subtask));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        URI getSubtasksUri = URI.create(URL + "/subtask");
        HttpRequest getSubtasksRequest = HttpRequest.newBuilder()
                .GET()
                .uri(getSubtasksUri)
                .build();

        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(getSubtasksRequest, handler);
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject jsonSubtaskFromServer = jsonObject.get("2").getAsJsonObject();
            Subtask subtaskFromServer = Managers.getGson().fromJson(jsonSubtaskFromServer, Subtask.class);
            assertEquals(subtask, subtaskFromServer);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        URI getSubtaskUriById = URI.create(URL + "/subtask?id=2");
        HttpRequest getSubtaskRequestById = HttpRequest.newBuilder()
                .GET()
                .uri(getSubtaskUriById)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(getSubtaskRequestById, handler);
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            Subtask subtaskFromServer = Managers.getGson().fromJson(jsonObject, Subtask.class);
            assertEquals(subtask, subtaskFromServer);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        HttpTaskManager newManager = HttpTaskManager.loadFromServer();
        assertNotNull(newManager);
        assertEquals(subtask, newManager.getSubtaskById(2));
        assertEquals(1, newManager.getSubtasks().size());
        assertEquals(1, newManager.getHistory().size());
        assertTrue(newManager.getHistory().contains(subtask));
        assertTrue(newManager.getPrioritizedTasks().contains(subtask));
    }

    @Test
    public void shouldUpdateCurrentSubtaskAndSubtaskOnTheServer() throws ManagerSaveException {
        Epic epic = new Epic(1, "Epic", "EpicTest");

        URI Epicuri = URI.create(URL + "/epic");
        final HttpRequest.BodyPublisher EpicBody = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(epic));
        HttpRequest EpicRequest = HttpRequest.newBuilder()
                .POST(EpicBody)
                .uri(Epicuri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(EpicRequest, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        LocalDateTime localDateTime = LocalDateTime.of(2021, 12, 1, 8, 30);
        Subtask subtask = new Subtask(2, "TestTask", "Test", localDateTime, 10, 1);

        URI uri = URI.create(URL + "/subtask");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(subtask));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        HttpTaskManager newManager = HttpTaskManager.loadFromServer();
        assertNotNull(newManager);
        assertEquals(subtask, newManager.getSubtaskById(2));
        assertEquals(1, newManager.getSubtasks().size());

        Subtask updateSubtask = new Subtask(2, "UpdateTask", "UpdateTask", localDateTime, 10, 1);

        URI updateUri = URI.create(URL + "/subtask");
        final HttpRequest.BodyPublisher updateBody = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(updateSubtask));
        HttpRequest updateRequest = HttpRequest.newBuilder()
                .POST(updateBody)
                .uri(updateUri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(updateRequest, handler);
            assertEquals(response.body(), "Задача успешно обновлена!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        HttpTaskManager newUpdateManager = HttpTaskManager.loadFromServer();
        assertNotNull(newUpdateManager);
        assertEquals(updateSubtask, newUpdateManager.getSubtaskById(2));
        assertEquals(1, newUpdateManager.getSubtasks().size());
    }

    @Test
    public void shouldDeleteSubtaskByIdAndDeleteSubtaskFromTheServer() throws ManagerSaveException, InterruptedException {
        Epic epic = new Epic(1, "Epic", "EpicTest");

        URI Epicuri = URI.create(URL + "/epic");
        final HttpRequest.BodyPublisher EpicBody = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(epic));
        HttpRequest EpicRequest = HttpRequest.newBuilder()
                .POST(EpicBody)
                .uri(Epicuri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(EpicRequest, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        LocalDateTime localDateTime = LocalDateTime.of(2021, 12, 1, 8, 30);
        Subtask subtask = new Subtask(2, "TestTask", "Test", localDateTime, 10, 1);

        URI uri = URI.create(URL + "/subtask");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(subtask));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        LocalDateTime localDateTime2 = LocalDateTime.of(2019, 12, 1, 8, 30);
        Subtask subtask2 = new Subtask(3, "TestTask2", "Test2", localDateTime2, 10, 1);

        URI uri2 = URI.create(URL + "/subtask");
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(subtask2));
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(uri2)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request2, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        URI deleteUri = URI.create(URL + "/subtask?id=3");
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(deleteUri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(deleteRequest, handler);
            assertEquals(response.body(), "Задача с id = 3 успешно удалена!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        Thread.sleep(10);
        HttpTaskManager newUpdateManager = HttpTaskManager.loadFromServer();
        assertNotNull(newUpdateManager);
        assertEquals(1, newUpdateManager.getSubtasks().size());
        assertFalse(newUpdateManager.getSubtasks().containsValue(subtask2));
    }

    @Test
    public void shouldDeleteAllSubtasksAndDeleteAllSubtasksFromTheServer() throws ManagerSaveException, InterruptedException {
        Epic epic = new Epic(1, "Epic", "EpicTest");

        URI Epicuri = URI.create(URL + "/epic");
        final HttpRequest.BodyPublisher EpicBody = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(epic));
        HttpRequest EpicRequest = HttpRequest.newBuilder()
                .POST(EpicBody)
                .uri(Epicuri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(EpicRequest, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        LocalDateTime localDateTime = LocalDateTime.of(2021, 12, 1, 8, 30);
        Subtask subtask = new Subtask(2, "TestTask", "Test", localDateTime, 10, 1);

        URI uri = URI.create(URL + "/subtask");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(subtask));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        LocalDateTime localDateTime2 = LocalDateTime.of(2019, 12, 1, 8, 30);
        Subtask subtask2 = new Subtask(3, "TestTask2", "Test2", localDateTime2, 10, 1);

        URI uri2 = URI.create(URL + "/subtask");
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(Managers.getGson().toJson(subtask2));
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(uri2)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request2, handler);
            assertEquals(response.body(), "Задача успешно создана!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        URI deleteUri = URI.create(URL + "/subtask");
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(deleteUri)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(deleteRequest, handler);
            assertEquals(response.body(), "Все задачи успешно удалены!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при выполнении запроса");
        }

        Thread.sleep(100);
        HttpTaskManager newUpdateManager = HttpTaskManager.loadFromServer();
        assertNotNull(newUpdateManager);
        assertEquals(0, newUpdateManager.getSubtasks().size());
        assertFalse(newUpdateManager.getSubtasks().containsValue(subtask));
        assertFalse(newUpdateManager.getSubtasks().containsValue(subtask2));
    }
}
