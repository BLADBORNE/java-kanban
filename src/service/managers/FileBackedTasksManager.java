package service.managers;

import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import service.exceptions.FileIsEmptyException;
import service.exceptions.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    @Override
    public Task createNewTask(String taskName, String description) {
        try {
            Task task = super.createNewTask(taskName, description);
            save();
            return task;
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    @Override
    public Task getTaskById(int taskId) {
        try {
            Task task = super.getTaskById(taskId);
            save();
            return task;
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    @Override
    public void deleteTaskById(int taskId) {
        try {
            super.deleteTaskById(taskId);
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }


    @Override
    public Epic createNewEpic(String taskName, String description) {
        try {
            Epic epic = super.createNewEpic(taskName, description);
            save();
            return epic;
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    @Override
    public Epic getEpicById(int epicId) {
        try {
            Epic epic = super.getEpicById(epicId);
            save();
            return epic;
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    @Override
    public void deleteEpicById(int epicId) {
        try {
            super.deleteEpicById(epicId);
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public Subtask createNewSubtask(String taskName, String description, int epicId) {
        try {
            Subtask subtask = super.createNewSubtask(taskName, description, epicId);
            save();
            return subtask;
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    @Override
    public Subtask getSubtaskById(int subtasksId) {
        try {
            Subtask subtask = super.getSubtaskById(subtasksId);
            save();
            return subtask;
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        try {
            super.deleteSubtaskById(subtaskId);
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void save() throws ManagerSaveException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bufferedWriter.write("id,type,name,status,description,epic\n");

            if (!getTasks().isEmpty()) {
                for (Task value : getTasks().values()) {
                    String info = toString(value);
                    bufferedWriter.write(info);
                    bufferedWriter.newLine();
                }
            }

            if (!getEpics().isEmpty()) {
                for (Epic value : getEpics().values()) {
                    String info = toString(value);
                    bufferedWriter.write(info);
                    bufferedWriter.newLine();
                }
            }

            if (!getSubtasks().isEmpty()) {
                for (Subtask value : getSubtasks().values()) {
                    String info = toString(value);
                    bufferedWriter.write(info);
                    bufferedWriter.newLine();
                }
            }

            String tasksId = historyToString(getHistoryManager());
            bufferedWriter.newLine();
            bufferedWriter.write(tasksId);

        } catch (IOException e) {
            throw new ManagerSaveException("Упс... Во время работы с файлом произошла ошибка");
        }
    }

    private String toString(Task task) {
        return task.toString();
    }

    private static Task taskFromString(String value) {
        String[] taskInfo = value.split(",");
        if (taskInfo[1].equals("TASK")) {
            return new Task(Integer.parseInt(taskInfo[0]), taskInfo[2], taskInfo[4]);
        } else if (taskInfo[1].equals("EPIC")) {
            return new Epic(Integer.parseInt(taskInfo[0]), taskInfo[2], taskInfo[4]);
        } else {
            return new Subtask(Integer.parseInt(taskInfo[0]), taskInfo[2], taskInfo[4], Integer.parseInt(taskInfo[5]));
        }
    }

    private String historyToString(HistoryManager manager) {
        List<Task> taskList = manager.getHistory();
        List<String> stringId = new ArrayList<>();

        for (Task task : taskList) {
            stringId.add(String.valueOf(task.getId()));
        }

        return String.join(",", stringId);
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> taskId = new ArrayList<>();
        String[] tasksStringId = value.split(",");

        for (String s : tasksStringId) {
            taskId.add(Integer.parseInt(s));
        }

        return taskId;
    }

    public static FileBackedTasksManager loadFromFile(File file) throws ManagerSaveException {
        try {
            if (file.length() == 0) {
                throw new FileIsEmptyException("Считывать нечего, файл пустой");
            }
        } catch (FileIsEmptyException fileIsEmptyException) {
            System.out.println(fileIsEmptyException.getMessage());
            return null;
        }

        List<String> stringList = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (bufferedReader.ready()) {
                stringList.add(bufferedReader.readLine());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Упс... Во время работы с файлом произошла ошибка");
        }

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);

        for (int i = 1; i < stringList.size() - 2; i++) {
            Task task = taskFromString(stringList.get(i));
            if (task instanceof Epic) {
                Epic epic = (Epic) task;
                fileBackedTasksManager.createNewEpic(epic.getTaskName(), epic.getDescription());
            } else if (task instanceof Subtask) {
                Subtask subtask = (Subtask) task;
                fileBackedTasksManager.createNewSubtask(subtask.getTaskName(), subtask.getDescription(),
                        subtask.getEpicId());
            } else {
                fileBackedTasksManager.createNewTask(task.getTaskName(), task.getDescription());
            }
        }

        List<Integer> taskIdList = historyFromString(stringList.get(stringList.size() - 1));
        Collections.reverse(taskIdList);

        for (Integer id : taskIdList) {
            if (fileBackedTasksManager.getTasks().containsKey(id)) {
                fileBackedTasksManager.getTaskById(id);
            } else if (fileBackedTasksManager.getEpics().containsKey(id)) {
                fileBackedTasksManager.getEpicById(id);
            } else {
                fileBackedTasksManager.getSubtaskById(id);
            }
        }
        return fileBackedTasksManager;
    }

    public static void main(String[] args) {
        File file = new File("src/service/files/TasksInfo.csv");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        Task task = fileBackedTasksManager.createNewTask("Посмотреть видео на ютубе",
                "Посмотреть последнее видео Димы Масленникова");
        Epic epic = fileBackedTasksManager.createNewEpic("Переезд", "Нужно переехать в новый дом");
        Subtask subtask = fileBackedTasksManager.createNewSubtask("Собрать вещи",
                "Нужно положить все вещи в коробки", epic.getId());
        fileBackedTasksManager.getTaskById(task.getId());
        fileBackedTasksManager.getEpicById(epic.getId());
        fileBackedTasksManager.getSubtaskById(subtask.getId());


        try {
            FileBackedTasksManager staticFileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
            Task task1 = staticFileBackedTasksManager.createNewTask("Новая обычная таска",
                    "таска для теста");
            staticFileBackedTasksManager.getTaskById(task1.getId());
            Epic epic1 = staticFileBackedTasksManager.createNewEpic("Тестовый эпик",
                    "Эпик для теста");
            Subtask subtask1 = staticFileBackedTasksManager.createNewSubtask("Загрузить 4 отчет",
                    "docx по продажам",
                    epic1.getId());
            staticFileBackedTasksManager.getEpicById(epic1.getId());
            staticFileBackedTasksManager.getSubtaskById(subtask1.getId());
        } catch (NullPointerException exception) {
            System.out.println("Попытка обратить к несущ. объекту");
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
