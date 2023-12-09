import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        inMemoryTaskManager.createNewTask("Посмотреть видео на ютубе",
                "Посмотреть последнее видео Димы Масленникова");

        inMemoryTaskManager.createNewTask("Погулять",
                "Прогуляться по парку");

        System.out.println(inMemoryTaskManager.getTasks());
        System.out.println();

        inMemoryTaskManager.createNewEpic("Переезд", "Нужно переехать в новый дом");
        inMemoryTaskManager.createNewSubtask("Собрать вещи", "Нужно положить все вещи в коробки", 3);
        inMemoryTaskManager.createNewSubtask("Выкинуть хлам", "Все ненужное отнести на помойку", 3);


        inMemoryTaskManager.createNewEpic("Позаниматься", "Подкачать мышцы");
        inMemoryTaskManager.createNewSubtask("Сходить в зал", "Побегать 10, Потом силовая", 6);

        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println();

        inMemoryTaskManager.setNewTaskId(1, TaskStatus.DONE);
        inMemoryTaskManager.setNewTaskId(2, TaskStatus.IN_PROGRESS);

        System.out.println(inMemoryTaskManager.getTaskById(1).getStatus());
        System.out.println(inMemoryTaskManager.getTaskById(2).getStatus());
        System.out.println();

        inMemoryTaskManager.setNewSubtaskId(4, TaskStatus.DONE);
        inMemoryTaskManager.setNewSubtaskId(5, TaskStatus.DONE);

        System.out.println(inMemoryTaskManager.getSubtaskById(4).getStatus());
        System.out.println(inMemoryTaskManager.getSubtaskById(5).getStatus());

        System.out.println(inMemoryTaskManager.getEpicById(3).getStatus());
        System.out.println();

        inMemoryTaskManager.deleteTaskById(2);

        System.out.println(inMemoryTaskManager.getTasks());
        System.out.println();

        inMemoryTaskManager.deleteEpicById(6);

        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println();

        List<Task> tasks = inMemoryTaskManager.getHistory();
        for (Task task : tasks) {
            System.out.println(task.getId());
        }
    }
}
