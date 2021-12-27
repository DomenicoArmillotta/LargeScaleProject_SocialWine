package exception;

/**
 * Contains AlreadyPopulatedExcpetion that is raised when the admin want to
 * load in the graph the same first 96 k reviews coming from review MongoDB
 * collection.
 */
public class AlreadyPopulatedException extends Exception{

    /**
     * Exception
     * @param msgError: error string.
     */
    public AlreadyPopulatedException (String msgError){
            super (msgError);
        }
}
