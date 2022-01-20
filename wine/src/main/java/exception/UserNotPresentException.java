package exception;

/**
 * Raised when a user doesn't exists
 */
public class UserNotPresentException extends Exception {

    /**
     * Exception setter
     *
     * @param message
     */
    public UserNotPresentException(String message) {
        super(message);
    }
}