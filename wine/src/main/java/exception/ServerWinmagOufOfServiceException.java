package exception;

/**
 * Contains the ServerWinmagOutOfServiceException that is raised if for
 * some reason the Winmag website is down. In that case the execution will
 * resume but taking in consideration only the 96 k reviews of Review MongoDB
 * collection.
 */
public class ServerWinmagOufOfServiceException extends Exception{

    /**
     * Exception
     * @param msgError: error string.
     */
    public ServerWinmagOufOfServiceException (String msgError){
        super (msgError);
    }
}
