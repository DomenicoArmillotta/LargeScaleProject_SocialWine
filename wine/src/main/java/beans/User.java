package beans;

public class User {
    String taster_name;

    public User (String taster_name){
        this.taster_name = taster_name;
    }

    public String getTaster_name() {
        return taster_name;
    }

    public void setTaster_name(String taster_name) {
        this.taster_name = taster_name;
    }
}
