package exception;

/**
 * Contains the ServerWinmagOutOfServiceException that is raised if for
 * some reason the Winmag website is down. In that case the execution will
 * resume but taking in consideration only the already inserted reviews
 */
public class ServerWinmagOufOfServiceException extends Exception {

    /**
     * Exception setter
     *
     * @param msgError: error string.
     */
    public ServerWinmagOufOfServiceException(String msgError) {
        super(msgError);
    }
}
