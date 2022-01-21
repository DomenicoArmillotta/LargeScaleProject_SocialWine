package beans;

/**
 * Wine entity
 */
public class Wine {
    String wineName;
    String designation;
    Integer price;
    String province;
    String variety;
    String winery;
    String country;

    /**
     * Wine constructor
     *
     * @param wineName:    wine name
     * @param designation: the name of the wine given to the wine by the producer
     * @param price:       wine price
     * @param province:    production province
     * @param variety:     types of grapes used
     * @param winery:      producer
     * @param country:     production country
     */
    public Wine(String wineName, String designation, Integer price, String province, String variety, String winery, String country) {
        this.wineName = wineName;
        this.designation = designation;
        this.price = price;
        this.province = province;
        this.variety = variety;
        this.winery = winery;
        this.country = country;
    }

    /**
     * Wine name getter
     *
     * @return wineName
     */
    public String getWineName() {
        return wineName;
    }

    /**
     * Wine name setter
     *
     * @param wineName
     */
    public void setWineName(String wineName) {
        this.wineName = wineName;
    }

    /**
     * Designation getter
     *
     * @return designation
     */
    public String getDesignation() {
        return designation;
    }

    /**
     * Designation setter
     *
     * @param designation
     */
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    /**
     * Price getter
     *
     * @return price
     */
    public Integer getPrice() {
        return price;
    }

    /**
     * Price setter
     *
     * @param price
     */
    public void setPrice(Integer price) {
        this.price = price;
    }

    /**
     * Province getter
     *
     * @return province
     */
    public String getProvince() {
        return province;
    }

    /**
     * Province setter
     *
     * @param province
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * Variety getter
     *
     * @return variety
     */
    public String getVariety() {
        return variety;
    }

    /**
     * Variety setter
     *
     * @param variety
     */
    public void setVariety(String variety) {
        this.variety = variety;
    }

    /**
     * Winery getter
     *
     * @return winery
     */
    public String getWinery() {
        return winery;
    }

    /**
     * Winery setter
     *
     * @param winery
     */
    public void setWinery(String winery) {
        this.winery = winery;
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
     * @param winery
     */
    public void setCountry(String winery) {
        this.country = country;
    }

}
