package service.exceptions;

import javax.annotation.processing.FilerException;

public class FileIsEmptyException extends Exception {
    public FileIsEmptyException(String message) {
        super(message);
    }
}
