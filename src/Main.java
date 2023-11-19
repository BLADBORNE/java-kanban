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






    }

//    TaskManager taskManager = new TaskManager();
//        taskManager.createNewTaskTest("Помыться", "-");
//        System.out.println("Задача успешно создана");
}
