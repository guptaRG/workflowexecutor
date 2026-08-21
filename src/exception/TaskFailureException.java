package exception;

public class TaskFailureException extends RuntimeException {
    public TaskFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
