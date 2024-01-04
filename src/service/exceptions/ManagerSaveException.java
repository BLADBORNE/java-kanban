package service.exceptions;

import javax.imageio.IIOException;

public class ManagerSaveException extends IIOException {
    public ManagerSaveException(String message) {
        super(message);
    }
}
