import model.Task;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        taskManager.createNewTask("Посмотреть видео на ютубе",
                "Посмотреть последнее видео Димы Масленникова");

        taskManager.createNewTask("Погулять",
                "Прогуляться по парку");

        System.out.println(taskManager.getTasks());
        System.out.println();

        taskManager.createNewEpic("Переезд", "Нужно переехать в новый дом");
        taskManager.createNewSubtask("Собрать вещи", "Нужно положить все вещи в коробки", 3);
        taskManager.createNewSubtask("Выкинуть хлам", "Все ненужное отнести на помойку", 3);


        taskManager.createNewEpic("Позаниматься", "Подкачать мышцы");
        taskManager.createNewSubtask("Сходить в зал", "Побегать 10, Потом силовая", 6);

        System.out.println(taskManager.getEpics());
        System.out.println();

        taskManager.setNewTaskId(1, "DONE");
        taskManager.setNewTaskId(2, "IN_PROGRESS");

        System.out.println(taskManager.getTaskById(1).getStatus());
        System.out.println(taskManager.getTaskById(2).getStatus());
        System.out.println();

        taskManager.setNewSubtaskId(4, "DONE");
        taskManager.setNewSubtaskId(5, "DONE");

        System.out.println(taskManager.getSubtaskById(4).getStatus());
        System.out.println(taskManager.getSubtaskById(5).getStatus());

        System.out.println(taskManager.getEpicById(3).getStatus());
        System.out.println();

        taskManager.deleteTaskById(2);

        System.out.println(taskManager.getTasks());
        System.out.println();

        taskManager.deleteEpicById(6);

        System.out.println(taskManager.getEpics());
        System.out.println();
    }
}
