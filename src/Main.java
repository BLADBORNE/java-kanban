import com.google.gson.*;
import model.tasks.Epic;
import model.tasks.Task;
import service.exceptions.ManagerSaveException;
import service.filetools.CSVFormat;
import service.interfaces.TaskManager;
import service.managers.HttpTaskManager;
import service.managers.InMemoryTaskManager;
import service.managers.Managers;
import service.server.HttpTaskServer;
import service.server.KVServer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws ManagerSaveException {
        InMemoryTaskManager inMemoryTaskManager =new InMemoryTaskManager();
        LocalDateTime localDateTime4 = LocalDateTime.of(2021, 12, 1, 8, 30);
        LocalDateTime localDateTime5 = LocalDateTime.of(2041, 12, 1, 8, 30);
        inMemoryTaskManager.createNewTask("Посмотреть видео на ютубе",
                "Посмотреть последнее видео Димы Масленникова", localDateTime4, 1);

        inMemoryTaskManager.createNewTask("Погулять",
                "Прогуляться по парку", localDateTime5, 5);

//        System.out.println(inMemoryTaskManager.getTasks());
//        System.out.println();


        LocalDateTime localDateTime1 = LocalDateTime.of(2001,12,10, 10,10);
        LocalDateTime localDateTime2 = LocalDateTime.of(2002,12,10, 10,10);
        LocalDateTime localDateTime3 = LocalDateTime.of(2003,12,10, 10,10);

        inMemoryTaskManager.createNewEpic("Переезд", "Нужно переехать в новый дом");
        inMemoryTaskManager.createNewSubtask("Собрать вещи", "Нужно положить все вещи в коробки", localDateTime1, 10, 3);
        inMemoryTaskManager.createNewSubtask("Выкинуть хлам", "Все ненужное отнести на помойку", localDateTime2, 20, 3);
        inMemoryTaskManager.createNewSubtask("Купить коробки", "Заехать в домострой", localDateTime3, 30, 3);

        inMemoryTaskManager.createNewEpic("Позаниматься", "Подкачать мышцы");

//        System.out.println(inMemoryTaskManager.getEpics());
//        System.out.println();

        inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.getTaskById(2);
        inMemoryTaskManager.getEpicById(3);
        inMemoryTaskManager.getEpicById(7);
        System.out.println(inMemoryTaskManager.getHistory());
        String a = CSVFormat.historyToString(inMemoryTaskManager.getHistoryManager());
        System.out.println(a);

//
//        List<Task> tasks = inMemoryTaskManager.getHistory();
//
//        for (Task task : tasks) {
//            System.out.println("id: " + task.getId() + " " + task.getDescription() + " - " + task.getClass());
//        }
//
//        System.out.println();
//        System.out.println();
//
//        inMemoryTaskManager.getTaskById(1);
//
//        tasks = inMemoryTaskManager.getHistory();
//        for (Task task : tasks) {
//            System.out.println("id: " + task.getId() + " " + task.getDescription() + " - " + task.getClass());
//        }
//        System.out.println();
//        System.out.println();
//
//        inMemoryTaskManager.deleteEpicById(3);
//
//        tasks = inMemoryTaskManager.getHistory();
//        for (Task task : tasks) {
//            System.out.println("id: " + task.getId() + " " + task.getDescription() + " - " + task.getClass());
//        }
//        System.out.println();
//        System.out.println();
//
//        inMemoryTaskManager.getTaskById(2);
//
//        tasks = inMemoryTaskManager.getHistory();
////        for (Task task : tasks) {
////            System.out.println("id: " + task.getId() + " " + task.getDescription() + " - " + task.getClass());
////        }
//
//        System.out.println();
//        System.out.println();
//
//        inMemoryTaskManager.getEpicById(7);

//        tasks = inMemoryTaskManager.getHistory();
//        for (Task task : tasks) {
//            System.out.println("id: " + task.getId() + " " + task.getDescription() + " - " + task.getClass());
//        }
//
//        System.out.println();
//        System.out.println();
//
//        inMemoryTaskManager.deleteTaskById(1);
//
//        tasks = inMemoryTaskManager.getHistory();
//        for (Task task : tasks) {
//            System.out.println("id: " + task.getId() + " " + task.getDescription() + " - " + task.getClass());
//        }
//        System.out.println();
//        List<Task> dateSortedTask = inMemoryTaskManager.getPrioritizedTasks();
//        for (Task task : dateSortedTask) {
//            System.out.println(task.getId() + " " + task.getStartDate() + " " + task.getDescription() + "\n");
//        }
    }
//public static void main(String[] args) throws IOException {
////    KVServer kvServer = new KVServer();
////    kvServer.start();
////
////    HttpTaskServer server = new HttpTaskServer();
////    server.start();
////   // HttpTaskManager.loadFromFile();
//
//    }
}
