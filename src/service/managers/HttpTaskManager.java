package service.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import service.client.KVTaskClient;
import service.exceptions.ManagerSaveException;
import service.filetools.CSVFormat;

import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private static KVTaskClient client;

    public HttpTaskManager(String url) throws ManagerSaveException {
        super(null);
        client = new KVTaskClient(url);
    }

    @Override
    protected void save() throws ManagerSaveException {
        List<Task> tasks = new ArrayList<>();

        tasks.addAll(getEpics().values());
        tasks.addAll(getSubtasks().values());
        tasks.addAll(getTasks().values());

        client.put("tasks", Managers.getGson().toJson(tasks));
        client.put("history", CSVFormat.historyToString(getHistoryManager()));
    }

    public static HttpTaskManager loadFromServer() throws ManagerSaveException {
        HttpTaskManager manager = new HttpTaskManager("http://localhost:8078");
        String responseTasks = client.load("tasks");
        String responseHistory = client.load("history");

        if (responseTasks != null) {
            JsonArray jsonTasksObjects = JsonParser.parseString(responseTasks).getAsJsonArray();
            for (JsonElement jsonTasksElement : jsonTasksObjects) {
                String taskType = jsonTasksElement.getAsJsonObject().get("taskType").getAsString();

                switch (taskType) {
                    case "TASK":
                        Task task = Managers.getGson().fromJson(jsonTasksElement, Task.class);
                        manager.createNewTask(task.getTaskName(), task.getDescription(), task.getStartDate(),
                                task.getMinutesDuration());
                        break;
                    case "EPIC":
                        Epic epic = Managers.getGson().fromJson(jsonTasksElement, Epic.class);
                        manager.createNewEpic(epic.getTaskName(), epic.getDescription());
                        break;
                    case "SUBTASK":
                        Subtask subtask = Managers.getGson().fromJson(jsonTasksElement, Subtask.class);
                        manager.createNewSubtask(subtask.getTaskName(), subtask.getDescription(), subtask.getStartDate(),
                                subtask.getMinutesDuration(), subtask.getEpicId());
                }
            }
        }

        if (responseHistory != null) {
            List<Integer> taskIdList = CSVFormat.historyFromString(responseHistory);
            restoreHistory(taskIdList, manager);
        }

        return manager;
    }
}