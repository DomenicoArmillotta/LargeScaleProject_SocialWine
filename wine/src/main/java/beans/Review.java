package beans;

/**
 * Review entity.
 */
public class Review {
    String description;
    Integer rating;

    /**
     * Review constructor
     *
     * @param description
     * @param rating
     */
    public Review(String description, Integer rating) {
        this.description = description;
        this.rating = rating;
    }

    /**
     * Description getter
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Description setter
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Rating getter
     *
     * @return rating
     */
    public Integer getRating() {
        return rating;
    }

    /**
     * Rating setter
     *
     * @param rating
     */
    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
