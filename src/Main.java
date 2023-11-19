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
        taskManager.createNewSubtask("Собрать вещи", "Нужно положить все вещи в коробки", 0);
        taskManager.createNewSubtask("Выкинуть хлам", "Все ненужное отнести на помойку", 0);


        taskManager.createNewEpic("Позаниматься", "Подкачать мышцы");
        taskManager.createNewSubtask("Сходить в зал", "Побегать 10, Потом силовая", 1);

        System.out.println(taskManager.getEpics());
        System.out.println();

        taskManager.setNewTaskId(0, "Done");
        taskManager.setNewTaskId(1, "In progress");

        System.out.println(taskManager.getTaskById(0).getStatus());
        System.out.println(taskManager.getTaskById(1).getStatus());
        System.out.println();

        taskManager.setNewSubtaskId(0, "Done");
        taskManager.setNewSubtaskId(1, "Done");

        System.out.println(taskManager.getSubtaskById(0).getStatus());
        System.out.println(taskManager.getSubtaskById(1).getStatus());

        System.out.println(taskManager.getEpicById(0).getStatus());
        System.out.println();

        taskManager.deleteTaskById(1);

        System.out.println(taskManager.getTasks());
        System.out.println();

        taskManager.deleteEpicById(1);

        System.out.println(taskManager.getEpics());
        System.out.println();
    }
}
