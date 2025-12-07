package exception;

public class InvalidGPAException extends Exception {
    public InvalidGPAException(String message) {
        super(message);
    }

    public InvalidGPAException(String message, Throwable cause) {
        super(message, cause);
    }
}

