import model.Epic;
import service.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        while (true) {
            taskManager.printMenu();
            int command = taskManager.scannerNumber();
            switch (command) {
                case 1:
                    taskManager.taskOrEpicMenu("Task");
                    break;
                case 2:
                    taskManager.taskOrEpicMenu("Epic");
                    break;
                case 0:
                    System.out.println("Выход");
                    System.exit(0);
                default:
                    System.out.println("Извините, такой команды пока нет.");
                    break;
            }
        }
    }
}
