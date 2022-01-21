package exception;

/**
 * Raised when a review is already inserted
 */
public class ReviewAlreadyInserted extends Exception {

    /**
     * Exception setter
     *
     * @param message
     */
    public ReviewAlreadyInserted(String message) {
        super(message);
    }

}
