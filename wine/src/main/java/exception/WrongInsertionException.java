package exception;

/**
 * Contains exception that is raised when the user doesn't insert nothing
 * or a character when he is asked to press button 1,2,0 to declare himself like user,
 * admin or to terminate program.
 */
public class WrongInsertionException extends Exception{

    /**
     * Exception
     * @param msgError: error string.
     */
    public WrongInsertionException (String msgError) {
        super(msgError);
    }
}
