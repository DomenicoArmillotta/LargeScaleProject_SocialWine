package exception;

/**
 * Raised when a given wine doesn't exists
 */
public class WineNotExistsException extends Exception {

    /**
     * Exception setter
     *
     * @param message
     */
    public WineNotExistsException(String message) {
        super(message);
    }
}
