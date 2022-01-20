package exception;

/**
 * Raised when for one query the result set is empty
 */
public class ResultsNotFoundException extends Exception {

    /**
     * Exception setter
     *
     * @param message
     */
    public ResultsNotFoundException(String message) {
        super(message);
    }

}
