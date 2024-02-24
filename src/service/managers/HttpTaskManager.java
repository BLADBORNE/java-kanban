package service.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import service.client.KVTaskClient;
import service.exceptions.ManagerSaveException;
import service.filetools.CSVFormat;

import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager {
    private static KVTaskClient client;

    public HttpTaskManager(String url) throws ManagerSaveException {
        super(null);
        client = new KVTaskClient(url);
    }

    @Override
    protected void save() throws ManagerSaveException {
        client.put("tasks", Managers.getGson().toJson(getTasks()));
        client.put("epics", Managers.getGson().toJson(getEpics()));
        client.put("subtasks", Managers.getGson().toJson(getSubtasks()));
        client.put("history", CSVFormat.historyToString(getHistoryManager()));
    }

    public static HttpTaskManager loadFromServer() throws ManagerSaveException {
        HttpTaskManager manager = new HttpTaskManager("http://localhost:8078");
        String responseTasks = client.load("tasks");
        String responseEpics = client.load("epics");
        String responseSubtasks = client.load("subtasks");
        String responseHistory = client.load("history");

        if (responseTasks != null) {
            JsonObject jsonTasksObjects = JsonParser.parseString(responseTasks).getAsJsonObject();
            for (Map.Entry<String, JsonElement> tasksObjects : jsonTasksObjects.entrySet()) {
                JsonObject taskObject = tasksObjects.getValue().getAsJsonObject();
                Task task = Managers.getGson().fromJson(taskObject, Task.class);
                manager.createNewTask(task.getTaskName(), task.getDescription(), task.getStartDate(),
                        task.getMinutesDuration());
            }
        }

        if (responseEpics != null) {
            JsonObject jsonEpicsObjects = JsonParser.parseString(responseEpics).getAsJsonObject();
            for (Map.Entry<String, JsonElement> epicsObjects : jsonEpicsObjects.entrySet()) {
                JsonObject epicObject = epicsObjects.getValue().getAsJsonObject();
                Epic epic = Managers.getGson().fromJson(epicObject, Epic.class);
                manager.createNewEpic(epic.getTaskName(), epic.getDescription());
            }
        }

        if (responseSubtasks != null) {
            JsonObject jsonSubtasksObjects = JsonParser.parseString(responseSubtasks).getAsJsonObject();
            for (Map.Entry<String, JsonElement> subtasksObjects : jsonSubtasksObjects.entrySet()) {
                JsonObject subtaskObject = subtasksObjects.getValue().getAsJsonObject();
                Subtask subtask = Managers.getGson().fromJson(subtaskObject, Subtask.class);
                manager.createNewSubtask(subtask.getTaskName(), subtask.getDescription(), subtask.getStartDate(),
                        subtask.getMinutesDuration(), subtask.getEpicId());
            }
        }

        if (responseHistory != null) {
            List<Integer> taskIdList = CSVFormat.historyFromString(responseHistory);
            restoreHistory(taskIdList, manager);
        }

        return manager;
    }
}
