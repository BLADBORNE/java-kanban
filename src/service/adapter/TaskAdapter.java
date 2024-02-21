package service.adapter;

import com.google.gson.*;
import model.enums.TaskStatus;
import model.enums.TaskType;
import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;


public class TaskAdapter implements JsonDeserializer<Task> {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    @Override
    public Task deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (type.getTypeName().equals(Subtask.class.getTypeName())) {
            Subtask task = gson.fromJson(jsonElement, Subtask.class);
            task.setEndDate(task.getStartDate().plus(Duration.ofMinutes(task.getMinutesDuration())));
            task.setStatus(TaskStatus.NEW);
            task.setTaskType(TaskType.SUBTASK);
            return task;
        } else if (type.getTypeName().equals(Epic.class.getTypeName())) {
            Epic task = gson.fromJson(jsonElement, Epic.class);
            task.setStatus(TaskStatus.NEW);
            task.setTaskType(TaskType.EPIC);
            task.setSubtask(new HashMap<>());
            return task;
        } else {
            Task task = gson.fromJson(jsonElement, Task.class);
            task.setEndDate(task.getStartDate().plus(Duration.ofMinutes(task.getMinutesDuration())));
            task.setStatus(TaskStatus.NEW);
            task.setTaskType(TaskType.TASK);
            return task;
        }
    }
}

