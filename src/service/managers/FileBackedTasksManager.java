package service.managers;

import model.enums.TaskType;
import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import service.exceptions.FileIsEmptyException;
import service.exceptions.ManagerSaveException;
import service.filetools.CSVFormat;
import service.interfaces.TaskManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    @Override
    public Task createNewTask(String taskName, String description, LocalDateTime startDate, int minutesDuration) {
        Task task = super.createNewTask(taskName, description, startDate, minutesDuration);
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
    public Task updateTask(Task newTask) {
        Task task = super.updateTask(newTask);
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
    public void deleteAllTasks() {
        super.deleteAllTasks();
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
    public Epic updateEpic(Epic newEpic) {
        Epic epic = super.updateEpic(newEpic);
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
    public void deleteAllEpics() {
        super.deleteAllEpics();
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public Subtask createNewSubtask(String taskName, String description, LocalDateTime startDate, int minutesDuration,
                                    int epicId) {
        Subtask subtask = super.createNewSubtask(taskName, description, startDate, minutesDuration, epicId);
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
    public Subtask updateSubtask(Subtask newSubtask) {
        Subtask subtask = super.updateSubtask(newSubtask);
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

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        try {
            save();
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getMessage());
        }
    }

    protected void save() throws ManagerSaveException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bufferedWriter.write("id,type,name,status,description,startDate,endDate,duration,epic\n");

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

        if (stringList.size() == 1) {
            return new FileBackedTasksManager(file);
        }

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        List<TaskType> taskTypes = List.of(TaskType.TASK, TaskType.EPIC, TaskType.SUBTASK);
        String lastString = stringList.get(stringList.size() - 1);
        String[] elementsOfString = lastString.split(",");

        if (taskTypes.toString().contains(elementsOfString[1])) {
            for (int i = 1; i < stringList.size(); i++) {
                Task task = CSVFormat.taskFromString(stringList.get(i));
                createInstanceofTask(task, fileBackedTasksManager);
            }
        } else {
            for (int i = 1; i < stringList.size() - 2; i++) {
                Task task = CSVFormat.taskFromString(stringList.get(i));
                createInstanceofTask(task, fileBackedTasksManager);
            }

            List<Integer> taskIdList = CSVFormat.historyFromString(stringList.get(stringList.size() - 1));
            restoreHistory(taskIdList, fileBackedTasksManager);
        }
        return fileBackedTasksManager;
    }

    protected static void restoreHistory(List<Integer> taskIdList, FileBackedTasksManager manager) {
        Collections.reverse(taskIdList);

        for (Integer id : taskIdList) {
            if (manager.getTasks().containsKey(id)) {
                manager.getTaskById(id);
            } else if (manager.getEpics().containsKey(id)) {
                manager.getEpicById(id);
            } else {
                manager.getSubtaskById(id);
            }
        }
    }

    private static void createInstanceofTask(Task task, FileBackedTasksManager fileBackedTasksManager) {
        if (task instanceof Epic) {
            Epic epic = (Epic) task;
            fileBackedTasksManager.createNewEpic(epic.getTaskName(), epic.getDescription());
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            fileBackedTasksManager.createNewSubtask(subtask.getTaskName(), subtask.getDescription(),
                    subtask.getStartDate(), subtask.getMinutesDuration(), subtask.getEpicId());
        } else {
            fileBackedTasksManager.createNewTask(task.getTaskName(), task.getDescription(), task.getStartDate(),
                    task.getMinutesDuration());
        }
    }

    public static void main(String[] args) {
        String path = "src/service/files/saveTasks.csv";
        TaskManager fileManager = new FileBackedTasksManager(new File(path));
        LocalDateTime localDateTime1 = LocalDateTime.of(1999, 12, 1, 8, 30);
        LocalDateTime localDateTime2 = LocalDateTime.of(2021, 12, 1, 8, 30);
        LocalDateTime localDateTime3 = LocalDateTime.of(2041, 12, 1, 8, 30);
        Task task = fileManager.createNewTask("Купить автомобиль", "we", localDateTime3, 1);
        Epic epic = fileManager.createNewEpic("new Epic1", "Новый Эпик");
        Subtask subtask = fileManager.createNewSubtask("New Subtask", "Подзадача", localDateTime1,
                5, epic.getId());
        Subtask subtask1 = fileManager.createNewSubtask("New Subtask2", "Подзадача2", localDateTime2, 11,
                epic.getId());
        fileManager.getEpicById(epic.getId());
        fileManager.getTaskById(task.getId());
        fileManager.getSubtaskById(subtask.getId());
        fileManager.getSubtaskById(subtask1.getId());
        System.out.println(fileManager.getTasks());
        System.out.println(fileManager.getEpics());
        System.out.println(fileManager.getSubtasks());
        System.out.println(fileManager.getHistory());
        System.out.println("\n\n" + "new" + "\n\n");
        fileManager.deleteAllTasks();
        fileManager.deleteAllEpics();
        fileManager.deleteAllEpics();

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
