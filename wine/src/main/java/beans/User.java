package beans;

/**
 * User entity.
 */
public class User {
    String username;
    String password;
    String twitter_taster_handle;
    String country;
    String email;
    Boolean admin;

    /**
     * User constructor
     *
     * @param username:              user's name
     * @param password:              user's password
     * @param twitter_taster_handle: user's twitter nickname
     * @param country:               user's country
     * @param email:                 user's email
     * @param admin:                 flag to identify if user is an admin or not
     */
    public User(String username, String password, String twitter_taster_handle, String country, String email, Boolean admin) {
        this.username = username;
        this.password = password;
        this.twitter_taster_handle = twitter_taster_handle;
        this.country = country;
        this.email = email;
        this.admin = admin;
    }

    /**
     * Username getter
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Username setter
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Password getter
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }


    /**
     * Password setter
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * Twitter nickname getter
     *
     * @return twitter_taster_handle
     */
    public String getTwitter_taster_handle() {
        return twitter_taster_handle;
    }


    /**
     * Twitter nickname setter
     *
     * @param twitter_taster_handle
     */
    public void setTwitter_taster_handle(String twitter_taster_handle) {
        this.twitter_taster_handle = twitter_taster_handle;
    }


    /**
     * Country getter
     *
     * @return country
     */
    public String getCountry() {
        return country;
    }


    /**
     * Country setter
     *
     * @param country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Email getter
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }


    /**
     * Email setter
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * Admin flag admin
     *
     * @return admin
     */
    public Boolean getAdmin() {
        return admin;
    }


    /**
     * Admin flag setter
     *
     * @param admin
     */
    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
