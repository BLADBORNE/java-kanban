package service.exceptions;

public class TasksIntersectException extends IllegalArgumentException {
    public TasksIntersectException(String message) {
        super(message);
    }
}
