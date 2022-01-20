package exception;

/**
 * Raised when  user insert string instead of numbers or viceversa.
 */
public class WrongInsertionException extends Exception {

    /**
     * Exception setter
     *
     * @param msgError: error string.
     */
    public WrongInsertionException(String msgError) {
        super(msgError);
    }
}
