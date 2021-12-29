package exception;

/**
 *Contains InsertedWrongNumberException that is raised when the user want to
 *follow a user, unfollow a friend and put like on a post choising the post or the user/friend
 * with a number that don't correspond to the number link to that user/friend or post.
 */
public class InsertedWrongNumberException extends Exception{

    /**
     * Exception
     * @param msgError: error string.
     */
    public InsertedWrongNumberException (String msgError){
        super (msgError);
    }

}
