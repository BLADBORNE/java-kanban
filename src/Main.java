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

        System.out.println(taskManager.getEpics());
        System.out.println();


    }

//    TaskManager taskManager = new TaskManager();
//        taskManager.createNewTaskTest("Помыться", "-");
//        System.out.println("Задача успешно создана");
}
