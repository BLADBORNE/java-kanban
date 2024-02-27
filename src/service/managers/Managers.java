package service.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import service.adapter.DurationAdapter;
import service.adapter.LocalDateTimeAdapter;
import service.adapter.TaskAdapter;
import service.exceptions.ManagerSaveException;
import service.interfaces.HistoryManager;
import service.interfaces.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {
    public static TaskManager getDefault() throws ManagerSaveException {
        return new HttpTaskManager("http://localhost:8078");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Task.class, new TaskAdapter());
        gsonBuilder.registerTypeAdapter(Epic.class, new TaskAdapter());
        gsonBuilder.registerTypeAdapter(Subtask.class, new TaskAdapter());
        return gsonBuilder.create();
    }
}