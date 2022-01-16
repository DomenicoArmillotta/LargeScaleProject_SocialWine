package beans;

public class Wine {
    String wineName;
    String designation;
    Integer price;
    String province;
    String variety;
    String winery;
    String country;

    public Wine(String wineName, String designation, Integer price, String province, String variety, String winery, String country) {
        this.wineName = wineName;
        this.designation = designation;
        this.price = price;
        this.province = province;
        this.variety = variety;
        this.winery = winery;
        this.country = country;
    }

    public String getWineName() {
        return wineName;
    }

    public void setWineName(String wineName) {
        this.wineName = wineName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public String getWinery() {
        return winery;
    }

    public void setWinery(String winery) {
        this.winery = winery;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String winery) {
        this.country = country;
    }



}
