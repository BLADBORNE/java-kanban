package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TaskManagerWithInterface extends Printer {
    private Scanner scanner = new Scanner(System.in);
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, ArrayList<Epic>> epics = new HashMap<>();
    private HashMap<Integer, ArrayList<Subtask>> subtasks = new HashMap<>();
    private int taskId = 0;
    private int epicId = 0;
    private int subtasksId = 0;


    public int scannerNumber() {
        return scanner.nextInt();
    }

    public void printMainMenu() {
        while (true) {
            printMenu();
            int command = scanner.nextInt();
            switch (command) {
                case 1:
                    taskOrEpicMenu("Task");
                    break;
                case 2:
                    taskOrEpicMenu("Epic");
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

    private void taskOrEpicMenu(String className) {
        while (true) {
            if (className.equals("Task")) {
                taskMenuCommands();
            } else if (className.equals("Epic")) {
                epicMenuCommands();
            }
            int command = scanner.nextInt();
            switch (command) {
                case 1:
                    if (className.equals("Task")) {
                        printTasksList(tasks);
                    } else if (className.equals("Epic")) {
                        printEpicsList(epics);
                    }
                    break;
                case 2:
                    if (className.equals("Task")) {
                        deleteAllTasksOrEpics("Task");
                    } else if (className.equals("Epic")) {
                        deleteAllTasksOrEpics("Epic");
                    }
                    break;
                case 3:
                    if (className.equals("Task")) {
                        getTaskById();
                    } else if (className.equals("Epic")) {
                        System.out.println("....");
                    }
                    break;
                case 4:
                    if (className.equals("Task")) {
                        Task newTask = createNewTask();
                        tasks.put(newTask.getId(), newTask);
                    } else if (className.equals("Epic")) {
                        System.out.println("....");
                    }
                    break;
                case 5:
                    if (className.equals("Task")) {
                        updateTask();
                    } else if (className.equals("Epic")) {
                        System.out.println("....");
                    }
                    break;
                case 6:
                    System.out.println("www");
                    break;
                case 0:
                    return;
                default:
                    printTextIfCmdNtFnd();
                    break;
            }
        }
    }

    private void getTaskById() {
        if (tasks.isEmpty()) {
            System.out.println("Нельяз получить задачи по id, т.к вы еще не создали ни одну задачу");
        } else {
            System.out.print("Введите id задачи: ");
            int taskId = scannerNumber();
            if (!tasks.containsKey(taskId)) {
                System.out.println("Извините, у нас нет задачи с таким id");
            } else {
                Task task = tasks.get(taskId);
                System.out.println("Задача успешно найдена!\n" + "id " + taskId + " соответствует задаче "
                        + task.getTaskName());
            }
        }
    }

    private Task createNewTask() {
        scanner.nextLine();
        System.out.print("Введите название задачи: ");
        String taskName = scanner.nextLine();
        System.out.print("Введите описание задачи: ");
        String description = scanner.nextLine();
        System.out.println("Задача " + taskName + " успешно создана!");
        return new Task(taskName, description, taskId++);
    }

    private void updateTask() {
        if (tasks.isEmpty()) {
            System.out.println("Обновлять нечего, список пустой");
        } else {
            for (Task value : tasks.values()) {
                System.out.println("Задача: " + value.getTaskName() + " id - " + value.getId());
            }
            System.out.print("Введите id задачи, которую хотите обновить: ");
            int taskId = scanner.nextInt();
            if (!tasks.containsKey(taskId)) {
                System.out.println("Извините, у нас такой заадчи нет");
            } else {
                Task taskUpdate = createNewTask();
                System.out.println("Вы заменили задачу " + tasks.get(taskId).getTaskName() + " на задачу " + taskUpdate.getTaskName());
                tasks.put(taskId, taskUpdate);
            }
        }
    }


    private void deleteAllTasksOrEpics(String className) {
        if (className.equals("Task")) {
            if (tasks.isEmpty()) {
                System.out.println("Удалять нечего, задач еще нет!");
            } else {
                tasks.clear();
                System.out.println("Вы успешно удалили все задачи!");
            }
        } else if (className.equals("Epic")) {
            if (epics.isEmpty()) {
                System.out.println("Удалять нечего, эпиков еще нет!");
            } else {
                epics.clear();
                System.out.println("Вы успешно удалили все задачи!");
            }
        }
    }

}
