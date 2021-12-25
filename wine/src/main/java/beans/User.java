package beans;

/**
 * User entity.
 */
public class User {
    String taster_name;

    /**
     * Constructor
     * @param taster_name
     */
    public User (String taster_name){
        this.taster_name = taster_name;
    }

    /**
     * Taster_name getter.
     * @return taster_name
     */
    public String getTaster_name() {
        return taster_name;
    }

    /**
     * Taster_name setter.
     * @param taster_name
     */
    public void setTaster_name(String taster_name) {
        this.taster_name = taster_name;
    }
}
