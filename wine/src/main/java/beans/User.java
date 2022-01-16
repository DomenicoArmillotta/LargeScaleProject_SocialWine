package beans;

/**
 * User entity.
 */
public class User {
    String username;
    String password;
    String  twitter_taster_handle;
    String country;
    String email;
    Boolean admin;

    public User(String username, String password, String twitter_taster_handle, String country, String email, Boolean admin) {
        this.username = username;
        this.password = password;
        this.twitter_taster_handle = twitter_taster_handle;
        this.country = country;
        this.email = email;
        this.admin = admin;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTwitter_taster_handle() {
        return twitter_taster_handle;
    }

    public void setTwitter_taster_handle(String twitter_taster_handle) {
        this.twitter_taster_handle = twitter_taster_handle;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
