package beans;

import java.util.List;

/**
 * Winery entity.
 */
public class Winery {
    String name;
    List<Review> reviewList;


    /**
     * Constructor
     * @param name
     */
    public Winery (String name){
        this.name = name;
    }

    /**
     * Winery getter.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Winery setter.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
}
