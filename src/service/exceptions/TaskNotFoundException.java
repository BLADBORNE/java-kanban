package service.exceptions;

public class TaskNotFoundException extends IllegalArgumentException{
    public TaskNotFoundException(String message) {
        super(message);
    }
}
