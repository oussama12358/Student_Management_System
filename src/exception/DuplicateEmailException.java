package exception;

public class DuplicateEmailException extends Exception {
    public DuplicateEmailException(String email) {
        super("Email '" + email + "' is already registered in the system.");
    }
}
