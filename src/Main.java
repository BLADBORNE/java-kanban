import model.Task;
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
        inMemoryTaskManager.createNewSubtask("Купить коробки", "Заехать в домострой", 3);

        inMemoryTaskManager.createNewEpic("Позаниматься", "Подкачать мышцы");

        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println();

        inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.getTaskById(2);
        inMemoryTaskManager.getEpicById(3);
        inMemoryTaskManager.getEpicById(7);


        List<Task> tasks = inMemoryTaskManager.getHistory();

        for (Task task : tasks) {
            System.out.println("id: " + task.getId() + " " + task.getDescription() + " - " + task.getClass());
        }

        System.out.println();
        System.out.println();

        inMemoryTaskManager.getTaskById(1);

        tasks = inMemoryTaskManager.getHistory();
        for (Task task : tasks) {
            System.out.println("id: " + task.getId() + " " + task.getDescription() + " - " + task.getClass());
        }
        System.out.println();
        System.out.println();

        inMemoryTaskManager.deleteEpicById(3);

        tasks = inMemoryTaskManager.getHistory();
        for (Task task : tasks) {
            System.out.println("id: " + task.getId() + " " + task.getDescription() + " - " + task.getClass());
        }
        System.out.println();
        System.out.println();

        inMemoryTaskManager.getTaskById(2);

        tasks = inMemoryTaskManager.getHistory();
        for (Task task : tasks) {
            System.out.println("id: " + task.getId() + " " + task.getDescription() + " - " + task.getClass());
        }

        System.out.println();
        System.out.println();

        inMemoryTaskManager.getEpicById(7);

        tasks = inMemoryTaskManager.getHistory();
        for (Task task : tasks) {
            System.out.println("id: " + task.getId() + " " + task.getDescription() + " - " + task.getClass());
        }

        System.out.println();
        System.out.println();

        inMemoryTaskManager.deleteTaskById(1);

        tasks = inMemoryTaskManager.getHistory();
        for (Task task : tasks) {
            System.out.println("id: " + task.getId() + " " + task.getDescription() + " - " + task.getClass());
        }
    }
}
