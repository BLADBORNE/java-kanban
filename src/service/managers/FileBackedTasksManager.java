package service.managers;

import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import service.exceptions.FileIsEmptyException;
import service.exceptions.ManagerSaveException;
import service.filetools.CSVFormat;
import service.interfaces.TaskManager;

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
        Task task = super.createNewTask(taskName, description);
        try {
            save();
            return task;
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = super.getTaskById(taskId);
        try {
            save();
            return task;
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public Epic createNewEpic(String taskName, String description) {
        Epic epic = super.createNewEpic(taskName, description);
        try {
            save();
            return epic;
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = super.getEpicById(epicId);
        try {
            save();
            return epic;
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public Subtask createNewSubtask(String taskName, String description, int epicId) {
        Subtask subtask = super.createNewSubtask(taskName, description, epicId);
        try {
            save();
            return subtask;
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    @Override
    public Subtask getSubtaskById(int subtasksId) {
        Subtask subtask = super.getSubtaskById(subtasksId);
        try {
            save();
            return subtask;
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        super.deleteSubtaskById(subtaskId);
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bufferedWriter.write("id,type,name,status,description,epic\n");

            if (!getTasks().isEmpty()) {
                for (Task value : getTasks().values()) {
                    String info = CSVFormat.toString(value);
                    bufferedWriter.write(info);
                    bufferedWriter.newLine();
                }
            }

            if (!getEpics().isEmpty()) {
                for (Epic value : getEpics().values()) {
                    String info = CSVFormat.toString(value);
                    bufferedWriter.write(info);
                    bufferedWriter.newLine();
                }
            }

            if (!getSubtasks().isEmpty()) {
                for (Subtask value : getSubtasks().values()) {
                    String info = CSVFormat.toString(value);
                    bufferedWriter.write(info);
                    bufferedWriter.newLine();
                }
            }

            if (!getHistoryManager().getHistory().isEmpty()) {
                String tasksId = CSVFormat.historyToString(getHistoryManager());
                bufferedWriter.newLine();
                bufferedWriter.write(tasksId);
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Упс... Во время работы с файлом произошла ошибка");
        }
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
            Task task = CSVFormat.taskFromString(stringList.get(i));
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

        List<Integer> taskIdList = CSVFormat.historyFromString(stringList.get(stringList.size() - 1));
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
        String path = "src/service/files/saveTasks.csv";
        TaskManager fileManager = new FileBackedTasksManager(new File(path));
        Task task = fileManager.createNewTask("Купить автомобиль", "we");
        Epic epic = fileManager.createNewEpic("new Epic1", "Новый Эпик");
        Subtask subtask = fileManager.createNewSubtask("New Subtask", "Подзадача", 2);
        fileManager.createNewSubtask("New Subtask2", "Подзадача2", 2);
        fileManager.getTaskById(task.getId());
        fileManager.getEpicById(epic.getId());
        fileManager.getSubtaskById(subtask.getId());
        System.out.println(fileManager.getTasks());
        System.out.println(fileManager.getEpics());
        System.out.println(fileManager.getSubtasks());
        System.out.println(fileManager.getHistory());
        System.out.println("\n\n" + "new" + "\n\n");

        try {
            TaskManager fileBackedTasksManager = loadFromFile(new File(path));
            System.out.println(fileBackedTasksManager.getTasks());
            System.out.println(fileBackedTasksManager.getEpics());
            System.out.println(fileBackedTasksManager.getSubtasks());
            System.out.println(fileBackedTasksManager.getHistory());
        } catch (NullPointerException nullPointerException) {
            System.out.println("Попытка обратиться к несущ. объекту");
            for (StackTraceElement stack : nullPointerException.getStackTrace()) {
                System.out.println("Класс: " + stack.getClassName() + ", " +
                        "метод: " + stack.getMethodName() + ", " +
                        "имя файла: " + stack.getFileName() + ", " +
                        "строка кода: " + stack.getLineNumber());
            }
        } catch (ManagerSaveException managerSaveException) {
            System.out.println(managerSaveException.getMessage());
            for (StackTraceElement stack : managerSaveException.getStackTrace()) {
                System.out.println("Класс: " + stack.getClassName() + ", " +
                        "метод: " + stack.getMethodName() + ", " +
                        "имя файла: " + stack.getFileName() + ", " +
                        "строка кода: " + stack.getLineNumber());
            }
        }
    }
}
