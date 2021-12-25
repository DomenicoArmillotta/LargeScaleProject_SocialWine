package beans;

/**
 * Review entity.
 */
public class Review {
    Integer points;
    String title;
    String description;
    String taster_name;
    String taster_twitter_handle;
    int price;
    String designation;
    String variety;
    String region_1;
    String region_2;
    String province;
    String country;
    String winery;

    /**
     * Constructor
     * @param points
     * @param title
     * @param description
     * @param taster_name
     * @param taster_twitter_handle
     * @param price
     * @param designation
     * @param variety
     * @param region_1
     * @param region_2
     * @param province
     * @param country
     * @param winery
     */
    public Review(String points, String title, String description, String taster_name, String taster_twitter_handle, int price, String designation, String variety, String region_1, String region_2, String province, String country, String winery) {
        setPoints(points);
        this.title = title;
        this.description = description;
        this.taster_name = taster_name;
        this.taster_twitter_handle = taster_twitter_handle;
        this.price = price;
        this.designation = designation;
        this.variety = variety;
        this.region_1 = region_1;
        this.region_2 = region_2;
        this.province = province;
        this.country = country;
        this.winery = winery;
    }

    /**
     * Points getter.
     * @return
     */
    public Integer getPoints() {
        return points;
    }

    /**
     * Points setter.
     * @param points
     */
    public void setPoints(String points) {

        int number = 0;
        try {
            if(points != null && points!="")
                number = Integer.parseInt(points);

        } catch (NumberFormatException e) {
            number = 0;
        }

        this.points = number;
    }

    /**
     * Title getter.
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Title setter.
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Description getter.
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Description setter.
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Taster name getter.
     * @return
     */
    public String getTaster_name() {
        return taster_name;
    }

    /**
     * Taster name setter.
     * @param taster_name
     */
    public void setTaster_name(String taster_name) {
        this.taster_name = taster_name;
    }

    /**
     * Taster twitter handle getter.
     * @return
     */
    public String getTaster_twitter_handle() {
        return taster_twitter_handle;
    }

    /**
     * Taster twitter handle setter.
     * @param taster_twitter_handle
     */
    public void setTaster_twitter_handle(String taster_twitter_handle) {
        this.taster_twitter_handle = taster_twitter_handle;
    }

    /**
     * Price getter.
     * @return
     */
    public Integer getPrice() {
        return price;
    }

    /**
     * Price setter.
     * @param price
     */
    public void setPrice(Integer price) {
        this.price = price;
    }

    /**
     * Designation getter.
     * @return
     */
    public String getDesignation() {
        return designation;
    }

    /**
     * Designation setter.
     * @param designation
     */
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    /**
     * Variety getter.
     * @return
     */
    public String getVariety() {
        return variety;
    }

    /**
     * Variety setter.
     * @param variety
     */
    public void setVariety(String variety) {
        this.variety = variety;
    }

    /**
     * Region 1 getter.
     * @return
     */
    public String getRegion_1() {
        return region_1;
    }

    /**
     * Region 1 setter.
     * @param region_1
     */
    public void setRegion_1(String region_1) {
        this.region_1 = region_1;
    }

    /**
     * Region 2 getter.
     * @return
     */
    public String getRegion_2() {
        return region_2;
    }

    /**
     * Region 2 setter.
     * @param region_2
     */
    public void setRegion_2(String region_2) {
        this.region_2 = region_2;
    }

    /**
     * Province getter.
     * @return
     */
    public String getProvince() {
        return province;
    }

    /**
     * Province setter.
     * @param province
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * Country getter.
     * @return
     */
    public String getCountry() {
        return country;
    }


    /**
     * Country setter.
     * @param country
     */
    public void setCountry(String country) {
        this.country = country;
    }


    /**
     * Winery getter.
     * @return
     */
    public String getWinery() {
        return winery;
    }

    /**
     * Winery setter.
     * @param winery
     */
    public void setWinery(String winery) {
        this.winery = winery;
    }
}
