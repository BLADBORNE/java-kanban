package service.api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.enums.Endpoint;
import model.tasks.Epic;
import service.interfaces.TaskManager;
import service.managers.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer server;

    private TaskManager backedTasksManager;

    private Gson gson;

    public HttpTaskServer() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new PostsHandler());
        this.backedTasksManager = Managers.getBackedManager();
        this.gson = Managers.getGson();
    }


    private class PostsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            List<String> methods = List.of("GET", "POST", "DELETE");

            if (!methods.contains(method)) {
                String message = "Мы можем обрабатывать только методы: GET POST DELETE, ваш метод - " + method;
                sendText(exchange, message, 405);
            }

            Endpoint endpoint = getEndpoint(method, exchange);
            switch (method) {
                case "GET":
                    switch (endpoint) {
                        case GET_TASKS:
                            getTasks(exchange);
                            break;
                        case GET_TASK_BY_ID:
                            getTaskById(exchange);
                            break;
                        case GET_EPICS_SUBTASKS:
                            getEpicsSubtasks(exchange);
                            break;
                        case GET_HISTORY:
                            getHistory(exchange);
                            break;
                        case GET_PRIORITIZED_TASKS:
                            getPrioritizedTasks(exchange);
                            break;
                        default:
                            sendText(exchange, "Ошибка со стороны клиента\nПроверьте, пожалуйста, адрес и " +
                                    "повторите попытку.", 405);
                    }
                case "POST":
                    switch (endpoint) {
                        case POST_TASK:
                            // postTask(exchange);
                            break;
                        case POST_TASK_BY_ID:
                            break;
                        case POST_EPIC:
                            System.out.println(1);
                            break;
                        case POST_EPIC_BY_ID:
                            System.out.println(1);
                            break;
                        case POST_SUBTASK:
                            System.out.println(1);
                            break;
                        case POST_SUBTASK_BY_ID:
                            System.out.println(1);
                            break;
                    }
                case "DELETE":
                    switch (endpoint) {
                        case DELETE_ALL_TASKS:
                            deleteTasks(exchange);
                            break;
                        case DELETE_TASK_BY_ID:
                            deleteTaskById(exchange);
                            break;
                        default:
                            sendText(exchange, "Ошибка со стороны клиента\nПроверьте, пожалуйста, адрес и " +
                                    "повторите попытку.", 405);
                    }
                default:
                    String message = "аааа";
                    sendText(exchange, message, 405);
            }
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
                        return Endpoint.POST_EPIC;
                    }

                    if (pathValues[1].equals("tasks") && pathValues[2].equals("subtask")) {
                        return Endpoint.POST_SUBTASK;
                    }
                } else {
                    String[] queryValues = query.split("=");
                    if (queryValues.length == 2 && queryValues[0].equals("id")) {
                        if (pathValues[1].equals("tasks") && pathValues[2].equals("task")) {
                            return Endpoint.POST_TASK_BY_ID;
                        }

                        if (pathValues[1].equals("tasks") && pathValues[2].equals("epic")) {
                            return Endpoint.POST_EPIC_BY_ID;
                        }

                        if (pathValues[1].equals("tasks") && pathValues[2].equals("subtask")) {
                            return Endpoint.POST_SUBTASK_BY_ID;
                        }
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
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        h.sendResponseHeaders(status, resp.length);
        h.getResponseBody().write(resp);
    }

    private void getTasks(HttpExchange exchange) throws IOException {
        String task = exchange.getRequestURI().getPath();
        String[] tasksPath = task.split("/");
        String response = "";

        switch (tasksPath[2]) {
            case "task":
                response = gson.toJson(backedTasksManager.getTasks());
                break;
            case "epic":
                response = gson.toJson(backedTasksManager.getEpics());
                break;
            case "subtask":
                response = gson.toJson(backedTasksManager.getSubtasks());
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
                if (backedTasksManager.getTasks().containsKey(taskId)) {
                    response = gson.toJson(backedTasksManager.getTaskById(taskId));
                    sendText(exchange, response, 200);
                    return;
                }
                break;
            case "epic":
                if (backedTasksManager.getEpics().containsKey(taskId)) {
                    response = gson.toJson(backedTasksManager.getEpicById(taskId));
                    sendText(exchange, response, 200);
                    return;
                }
                break;
            case "subtask":
                if (backedTasksManager.getSubtasks().containsKey(taskId)) {
                    response = gson.toJson(backedTasksManager.getSubtaskById(taskId));
                    sendText(exchange, response, 200);
                    return;
                }
                break;
        }
        sendText(exchange, "Извините, у нас нет задачи с таким id", 405);
    }


    private void getHistory(HttpExchange exchange) throws IOException {
        if (backedTasksManager.getHistory().isEmpty()) {
            sendText(exchange, "История просмотра пуста", 200);
            return;
        }

        sendText(exchange, gson.toJson(backedTasksManager.getHistory()), 200);
    }

    private void getPrioritizedTasks(HttpExchange exchange) throws IOException {
        if (backedTasksManager.getPrioritizedTasks().isEmpty()) {
            sendText(exchange, "Список задач пуст", 200);
            return;
        }

        sendText(exchange, gson.toJson(backedTasksManager.getPrioritizedTasks()), 200);
    }


    private void getEpicsSubtasks(HttpExchange exchange) throws IOException {
        int epicId = checkNumberFormatException(exchange);

        if (epicId == -1) {
            return;
        }

        Epic epic = backedTasksManager.getEpicById(epicId);

        if (epic == null) {
            sendText(exchange, "Извините, у нас нет эпика с таким id", 405);
            return;
        }

        String epicsSubtasks = gson.toJson(epic.getSubtask());

        if (epicsSubtasks.equals("{}")) {
            sendText(exchange, "Список подзадач эпика пуст", 200);
            return;
        }

        sendText(exchange, epicsSubtasks, 200);
    }

//    private void postTask(HttpExchange exchange) throws IOException {
//        String body = readText(exchange)
//
//
//        Task task;
//
////        try {
////            task = gson.fromJson(body, Task.class);
////        } catch (JsonSyntaxException e) {
////            sendText(exchange, "Получен некорректный JSON", 405);
////            return;
////        }
////
////        Task checkTask = backedTasksManager.createNewTask(task.getTaskName(), task.getDescription(), task.getStartDate(),
////                task.getMinutesDuration());
////        if (checkTask == null) {
////            sendText(exchange, "При создании, задачи не должны пересекаться!\n" +
////                    "Выберите другое время", 405);
////            return;
////        }
////        sendText(exchange, "Задача успешно создана!", 200);
//    }


    private void deleteTasks(HttpExchange exchange) throws IOException {
        String task = exchange.getRequestURI().getPath();
        String[] tasksPath = task.split("/");
        String textIfTaskNotDeleted = "Удалять нечего, список задач пуст";
        String textIfTaskDeleted = "Задача успешно удалена";

        switch (tasksPath[2]) {
            case "task":
                if (!backedTasksManager.getTasks().isEmpty()) {
                    sendText(exchange, textIfTaskDeleted, 200);
                    backedTasksManager.deleteAllTasks();
                    return;
                }
                break;
            case "epic":
                if (!backedTasksManager.getEpics().isEmpty()) {
                    sendText(exchange, textIfTaskDeleted, 200);
                    backedTasksManager.deleteAllEpics();
                    return;
                }
                break;
            case "subtask":
                if (!backedTasksManager.getSubtasks().isEmpty()) {
                    sendText(exchange, textIfTaskDeleted, 200);
                    backedTasksManager.deleteAllSubtasks();
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
                if (!backedTasksManager.getTasks().isEmpty()) {
                    tasksIsEmpty = false;
                    if (backedTasksManager.getTasks().containsKey(taskId)) {
                        sendText(exchange, response, 200);
                        backedTasksManager.deleteTaskById(taskId);
                        return;
                    }
                }
                break;
            case "epic":
                if (!backedTasksManager.getEpics().isEmpty()) {
                    tasksIsEmpty = false;
                    if (backedTasksManager.getEpics().containsKey(taskId)) {
                        sendText(exchange, response, 200);
                        backedTasksManager.deleteEpicById(taskId);
                        return;
                    }
                }
                break;
            case "subtask":
                if (!backedTasksManager.getSubtasks().isEmpty()) {
                    tasksIsEmpty = false;
                    if (backedTasksManager.getSubtasks().containsKey(taskId)) {
                        sendText(exchange, response, 200);
                        backedTasksManager.deleteSubtaskById(taskId);
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


