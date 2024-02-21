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

    public HttpTaskManager() throws ManagerSaveException {
        super(null);
        client = new KVTaskClient();
    }

    @Override
    protected void save() throws ManagerSaveException {
        if (!getTasks().isEmpty()) {
            client.put("tasks", Managers.getGson().toJson(getTasks()));
        }

        if (!getEpics().isEmpty()) {
            client.put("epics", Managers.getGson().toJson(getEpics()));
        }

        if (!getSubtasks().isEmpty()) {
            client.put("subtasks", Managers.getGson().toJson(getSubtasks()));
        }

        if (!getHistory().isEmpty()) {
            client.put("history", CSVFormat.historyToString(getHistoryManager()));
        }
    }

    public static HttpTaskManager loadFromFile() throws ManagerSaveException {
        HttpTaskManager manager = new HttpTaskManager();
        String textIfTasksIsEmpty = "Извините, у нас нет данного ключа";
        String responseTasks = client.load("tasks");
        String responseEpics = client.load("epics");
        String responseSubtasks = client.load("subtasks");
        String responseHistory = client.load("history");

        if (!responseTasks.equals(textIfTasksIsEmpty)) {
            JsonObject jsonTasksObjects = JsonParser.parseString(responseTasks).getAsJsonObject();
            for (Map.Entry<String, JsonElement> tasksObjects : jsonTasksObjects.entrySet()) {
                JsonObject taskObject = tasksObjects.getValue().getAsJsonObject();
                Task task = Managers.getGson().fromJson(taskObject, Task.class);
                manager.createNewTask(task.getTaskName(), task.getDescription(), task.getStartDate(),
                        task.getMinutesDuration());
            }
        }

        if (!responseEpics.equals(textIfTasksIsEmpty)) {
            JsonObject jsonEpicsObjects = JsonParser.parseString(responseEpics).getAsJsonObject();
            for (Map.Entry<String, JsonElement> epicsObjects : jsonEpicsObjects.entrySet()) {
                JsonObject epicObject = epicsObjects.getValue().getAsJsonObject();
                Epic epic = Managers.getGson().fromJson(epicObject, Epic.class);
                manager.createNewEpic(epic.getTaskName(), epic.getDescription());
            }
        }

        if (!responseSubtasks.equals(textIfTasksIsEmpty)) {
            JsonObject jsonSubtasksObjects = JsonParser.parseString(responseSubtasks).getAsJsonObject();
            for (Map.Entry<String, JsonElement> subtasksObjects : jsonSubtasksObjects.entrySet()) {
                JsonObject subtaskObject = subtasksObjects.getValue().getAsJsonObject();
                Subtask subtask = Managers.getGson().fromJson(subtaskObject, Subtask.class);
                manager.createNewSubtask(subtask.getTaskName(), subtask.getDescription(), subtask.getStartDate(),
                        subtask.getMinutesDuration(), subtask.getEpicId());
            }
        }

        if (!responseHistory.equals(textIfTasksIsEmpty)) {
            List<Integer> taskIdList = CSVFormat.historyFromString(responseHistory);
            restoreHistory(taskIdList, manager);
        }

        return manager;
    }
}
