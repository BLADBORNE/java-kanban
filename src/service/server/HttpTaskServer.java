package service.server;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import model.enums.Endpoint;
import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import service.interfaces.TaskManager;
import service.managers.Managers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer server;
    private TaskManager manager;

    public HttpTaskServer() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", this::handleTasks);
        this.manager = Managers.getDefault();
    }

    public void handleTasks(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        List<String> methods = List.of("GET", "POST", "DELETE");

        if (!methods.contains(method)) {
            String message = "Мы можем обрабатывать только методы: GET POST DELETE, ваш метод - " + method;
            sendText(exchange, message, 405);
            return;
        }

        Endpoint endpoint = getEndpoint(method, exchange);
        switch (endpoint) {
            case POST_TASK:
                postTask(exchange);
                return;
            case GET_TASKS:
                getTasks(exchange);
                return;
            case GET_TASK_BY_ID:
                getTaskById(exchange);
                return;
            case GET_EPICS_SUBTASKS:
                getEpicsSubtasks(exchange);
                return;
            case GET_HISTORY:
                getHistory(exchange);
                return;
            case GET_PRIORITIZED_TASKS:
                getPrioritizedTasks(exchange);
                return;
            case DELETE_ALL_TASKS:
                deleteTasks(exchange);
                return;
            case DELETE_TASK_BY_ID:
                deleteTaskById(exchange);
                return;
            default:
                sendText(exchange, "Ошибка со стороны клиента\nПроверьте, пожалуйста, адрес и " +
                        "повторите попытку.", 405);
        }
    }

    private Endpoint getEndpoint(String method, HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] pathValues = path.split("/");
        String query = exchange.getRequestURI().getQuery();
        if (method.equals("GET")) {
            if (pathValues.length == 2 && pathValues[1].equals("tasks") && query == null) {
                return Endpoint.GET_PRIORITIZED_TASKS;
            }
            if (pathValues.length == 3) {
                if (query == null) {
                    if (pathValues[1].equals("tasks") && pathValues[2].equals("history")) {
                        return Endpoint.GET_HISTORY;
                    }

                    if (pathValues[1].equals("tasks") && pathValues[2].equals("task")) {
                        return Endpoint.GET_TASKS;
                    }

                    if (pathValues[1].equals("tasks") && pathValues[2].equals("epic")) {
                        return Endpoint.GET_TASKS;
                    }

                    if (pathValues[1].equals("tasks") && pathValues[2].equals("subtask")) {
                        return Endpoint.GET_TASKS;
                    }
                } else {
                    String[] queryValues = query.split("=");
                    if (queryValues.length == 2 && queryValues[0].equals("id")) {
                        if (pathValues[1].equals("tasks") && pathValues[2].equals("task")) {
                            return Endpoint.GET_TASK_BY_ID;
                        }

                        if (pathValues[1].equals("tasks") && pathValues[2].equals("epic")) {
                            return Endpoint.GET_TASK_BY_ID;
                        }

                        if (pathValues[1].equals("tasks") && pathValues[2].equals("subtask")) {
                            return Endpoint.GET_TASK_BY_ID;
                        }
                    }
                }
            }

            if (pathValues.length == 4 && query != null) {
                String[] queryValues = query.split("=");
                if (queryValues.length == 2 && queryValues[0].equals("id")) {
                    return Endpoint.GET_EPICS_SUBTASKS;
                }
            }
        }

        if (method.equals("POST")) {
            if (pathValues.length == 3) {
                if (query == null) {
                    if (pathValues[1].equals("tasks") && pathValues[2].equals("task")) {
                        return Endpoint.POST_TASK;
                    }

                    if (pathValues[1].equals("tasks") && pathValues[2].equals("epic")) {
                        return Endpoint.POST_TASK;
                    }

                    if (pathValues[1].equals("tasks") && pathValues[2].equals("subtask")) {
                        return Endpoint.POST_TASK;
                    }
                }
            }
        }

        if (method.equals("DELETE")) {
            if (pathValues.length == 3) {
                if (query == null) {
                    if (pathValues[1].equals("tasks") && pathValues[2].equals("task")) {
                        return Endpoint.DELETE_ALL_TASKS;
                    }

                    if (pathValues[1].equals("tasks") && pathValues[2].equals("epic")) {
                        return Endpoint.DELETE_ALL_TASKS;
                    }

                    if (pathValues[1].equals("tasks") && pathValues[2].equals("subtask")) {
                        return Endpoint.DELETE_ALL_TASKS;
                    }
                } else {
                    String[] queryValues = query.split("=");
                    if (queryValues.length == 2 && queryValues[0].equals("id")) {
                        if (pathValues[1].equals("tasks") && pathValues[2].equals("task")) {
                            return Endpoint.DELETE_TASK_BY_ID;
                        }

                        if (pathValues[1].equals("tasks") && pathValues[2].equals("epic")) {
                            return Endpoint.DELETE_TASK_BY_ID;
                        }

                        if (pathValues[1].equals("tasks") && pathValues[2].equals("subtask")) {
                            return Endpoint.DELETE_TASK_BY_ID;
                        }
                    }
                }
            }
        }

        return Endpoint.UNKNOWN;
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    private void sendText(HttpExchange h, String text, int status) throws IOException {
        h.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        h.sendResponseHeaders(status, 0);
        byte[] bytes = text.getBytes(UTF_8);
        try (OutputStream os = h.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void getTasks(HttpExchange exchange) throws IOException {
        String task = exchange.getRequestURI().getPath();
        String[] tasksPath = task.split("/");
        String response = "";

        switch (tasksPath[2]) {
            case "task":
                response = Managers.getGson().toJson(manager.getTasks());
                break;
            case "epic":
                response = Managers.getGson().toJson(manager.getEpics());
                break;
            case "subtask":
                response = Managers.getGson().toJson(manager.getSubtasks());
                break;
        }

        if (response.equals("{}")) {
            sendText(exchange, "Список задач пуст", 200);
            return;
        }

        sendText(exchange, response, 200);
    }

    private void getTaskById(HttpExchange exchange) throws IOException {
        int taskId = checkNumberFormatException(exchange);

        if (taskId == -1) {
            return;
        }

        String response;
        String task = exchange.getRequestURI().getPath();
        String[] tasksPath = task.split("/");

        switch (tasksPath[2]) {
            case "task":
                if (manager.getTasks().containsKey(taskId)) {
                    response = Managers.getGson().toJson(manager.getTaskById(taskId));
                    sendText(exchange, response, 200);
                    return;
                }
                break;
            case "epic":
                if (manager.getEpics().containsKey(taskId)) {
                    response = Managers.getGson().toJson(manager.getEpicById(taskId));
                    sendText(exchange, response, 200);
                    return;
                }
                break;
            case "subtask":
                if (manager.getSubtasks().containsKey(taskId)) {
                    response = Managers.getGson().toJson(manager.getSubtaskById(taskId));
                    sendText(exchange, response, 200);
                    return;
                }
                break;
        }
        sendText(exchange, "Извините, у нас нет задачи с таким id", 405);
    }

    private void getHistory(HttpExchange exchange) throws IOException {
        if (manager.getHistory().isEmpty()) {
            sendText(exchange, "История просмотра пуста", 200);
            return;
        }

        sendText(exchange, Managers.getGson().toJson(manager.getHistory()), 200);
    }

    private void getPrioritizedTasks(HttpExchange exchange) throws IOException {
        if (manager.getPrioritizedTasks().isEmpty()) {
            sendText(exchange, "Список задач пуст", 200);
            return;
        }

        sendText(exchange, Managers.getGson().toJson(manager.getPrioritizedTasks()), 200);
    }

    private void getEpicsSubtasks(HttpExchange exchange) throws IOException {
        int epicId = checkNumberFormatException(exchange);

        if (epicId == -1) {
            return;
        }

        Epic epic = manager.getEpicById(epicId);

        if (epic == null) {
            sendText(exchange, "Извините, у нас нет эпика с таким id", 405);
            return;
        }

        String epicsSubtasks = Managers.getGson().toJson(epic.getSubtask());

        if (epicsSubtasks.equals("{}")) {
            sendText(exchange, "Список подзадач эпика пуст", 200);
            return;
        }

        sendText(exchange, epicsSubtasks, 200);
    }

    private void postTask(HttpExchange exchange) throws IOException {
        String body = readText(exchange);
        String path = exchange.getRequestURI().getPath();
        String[] tasksPath = path.split("/");
        boolean isNotUpdated = false;

        switch (tasksPath[2]) {
            case "task":
                Task task;

                try {
                    task = Managers.getGson().fromJson(body, Task.class);
                } catch (JsonSyntaxException e) {
                    sendText(exchange, "Получен некорректный JSON", 405);
                    return;
                }

                if (manager.getTasks().containsKey(task.getId())) {
                    Task updateTask = manager.updateTask(task);

                    if (updateTask != null) {
                        sendText(exchange, "Задача успешно обновлена!", 200);
                        return;
                    }
                    isNotUpdated = true;
                } else {
                    Task createNewTask = manager.createNewTask(task.getTaskName(), task.getDescription(),
                            task.getStartDate(), task.getMinutesDuration());

                    if (createNewTask != null) {
                        sendText(exchange, "Задача успешно создана!", 200);
                        return;
                    }
                }
                break;
            case "epic":
                Epic epic;

                try {
                    epic = Managers.getGson().fromJson(body, Epic.class);
                } catch (JsonSyntaxException e) {
                    sendText(exchange, "Получен некорректный JSON", 405);
                    return;
                }

                if (manager.getEpics().containsKey(epic.getId())) {
                    Epic updateEpic = manager.updateEpic(epic);

                    if (updateEpic != null) {
                        sendText(exchange, "Задача успешно обновлена!", 200);
                        return;
                    }
                    isNotUpdated = true;
                } else {
                    Epic createNewEpic = manager.createNewEpic(epic.getTaskName(), epic.getDescription());

                    if (createNewEpic != null) {
                        sendText(exchange, "Задача успешно создана!", 200);
                        return;
                    }
                }
                break;
            case "subtask":
                Subtask subtask;

                try {
                    subtask = Managers.getGson().fromJson(body, Subtask.class);
                } catch (JsonSyntaxException e) {
                    sendText(exchange, "Получен некорректный JSON", 405);
                    return;
                }

                if (manager.getSubtasks().containsKey(subtask.getId())) {
                    Subtask updateSubtask = manager.updateSubtask(subtask);

                    if (updateSubtask != null) {
                        sendText(exchange, "Задача успешно обновлена!", 200);
                        return;
                    }
                    isNotUpdated = true;
                } else {
                    Subtask createNewSubtask = manager.createNewSubtask(subtask.getTaskName(),
                            subtask.getDescription(), subtask.getStartDate(), subtask.getMinutesDuration(),
                            subtask.getEpicId());

                    if (createNewSubtask != null) {
                        sendText(exchange, "Задача успешно создана!", 200);
                        return;
                    }
                }
                break;
        }

        if (isNotUpdated) {
            sendText(exchange, "Задача не была обновлена, так задачи идентичны", 405);
            return;
        }

        sendText(exchange, "При создании, задачи не должны пересекаться!\nВыберите другое время", 405);
    }

    private void deleteTasks(HttpExchange exchange) throws IOException {
        String task = exchange.getRequestURI().getPath();
        String[] tasksPath = task.split("/");
        String textIfTaskNotDeleted = "Удалять нечего, список задач пуст";
        String textIfTaskDeleted = "Все задачи успешно удалены!";

        switch (tasksPath[2]) {
            case "task":
                if (!manager.getTasks().isEmpty()) {
                    sendText(exchange, textIfTaskDeleted, 200);
                    manager.deleteAllTasks();
                    return;
                }
                break;
            case "epic":
                if (!manager.getEpics().isEmpty()) {
                    sendText(exchange, textIfTaskDeleted, 200);
                    manager.deleteAllEpics();
                    return;
                }
                break;
            case "subtask":
                if (!manager.getSubtasks().isEmpty()) {
                    sendText(exchange, textIfTaskDeleted, 200);
                    manager.deleteAllSubtasks();
                    return;
                }
                break;
        }
        sendText(exchange, textIfTaskNotDeleted, 200);
    }

    private void deleteTaskById(HttpExchange exchange) throws IOException {
        int taskId = checkNumberFormatException(exchange);
        String response = "Задача с id = " + taskId + " успешно удалена!";

        if (taskId == -1) {
            return;
        }

        String task = exchange.getRequestURI().getPath();
        String[] tasksPath = task.split("/");
        boolean tasksIsEmpty = true;

        switch (tasksPath[2]) {
            case "task":
                if (!manager.getTasks().isEmpty()) {
                    tasksIsEmpty = false;
                    if (manager.getTasks().containsKey(taskId)) {
                        sendText(exchange, response, 200);
                        manager.deleteTaskById(taskId);
                        return;
                    }
                }
                break;
            case "epic":
                if (!manager.getEpics().isEmpty()) {
                    tasksIsEmpty = false;
                    if (manager.getEpics().containsKey(taskId)) {
                        sendText(exchange, response, 200);
                        manager.deleteEpicById(taskId);
                        return;
                    }
                }
                break;
            case "subtask":
                if (!manager.getSubtasks().isEmpty()) {
                    tasksIsEmpty = false;
                    if (manager.getSubtasks().containsKey(taskId)) {
                        sendText(exchange, response, 200);
                        manager.deleteSubtaskById(taskId);
                        return;
                    }
                }
                break;
        }

        if (tasksIsEmpty) {
            sendText(exchange, "Удалять нечего, список задач пуст", 200);
            return;
        }

        sendText(exchange, "Извините, у нас нет задачи с таким id", 405);
    }

    private int checkNumberFormatException(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String[] queryValues = query.split("=");
        int taskId;

        try {
            taskId = Integer.parseInt(queryValues[1]);
        } catch (NumberFormatException e) {
            sendText(exchange, "Некорректный идентификатор задачи", 405);
            return -1;
        }
        return taskId;
    }
}