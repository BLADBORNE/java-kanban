package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Printer {
    public void printMenu() {
        System.out.println("Введите цифру...");
        System.out.println("1 - Работать с задачами");
        System.out.println("2 - Работать с эпиками");
        System.out.println("0 — Завершить программу");
    }

    public void taskMenuCommands() {
        System.out.println();
        System.out.println("Введите цифру...");
        System.out.println("1 - Получение списка всех задач");
        System.out.println("2 - Удаление всех задач");
        System.out.println("3 — Получение задачи по идентификатору");
        System.out.println("4 — Создание новой задачи");
        System.out.println("5 — Обновление задачи");
        System.out.println("6 — Удаление по идентификатору");
        System.out.println("7 — Проставление статуса задачи");
        System.out.println("6 — Просмотр статусов всех задач");
        System.out.println("0 — Выйти в главное меню");
    }

    public void printTasksList(HashMap<Integer, Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("У вас нет еще задач, создайте их");
        } else {
            System.out.println("Список простых задач");
            for (Task value : tasks.values()) {
                System.out.println(value.getTaskName());
            }
        }
    }

    public void epicMenuCommands() {
        System.out.println();
        System.out.println("Введите цифру...");
        System.out.println("1 - Получение списка всех эпиков");
        System.out.println("2 - Удаление всех эпиков");
        System.out.println("3 — Получение эпика по идентификатору");
        System.out.println("5 — Создание нового эпика");
        System.out.println("5 — Обновление эпика");
        System.out.println("6 — Удаление по идентификатору");
        System.out.println("0 — Выйти в главное меню");
    }

    public void printEpicsList(HashMap<Integer, ArrayList<Epic>> epics) {
        System.out.println("dddd");

    }


    public void printTextIfCmdNtFnd() {
        System.out.println("Извините, такой команды нет");
    }

}
