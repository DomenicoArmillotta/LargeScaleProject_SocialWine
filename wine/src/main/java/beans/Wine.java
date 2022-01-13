package beans;

public class Wine {
    String wineName;
    String designation;
    String price;
    String province;
    String variety;
    String winery;

    public Wine(String wineName, String designation, String price, String province, String variety, String winery) {
        this.wineName = wineName;
        this.designation = designation;
        this.price = price;
        this.province = province;
        this.variety = variety;
        this.winery = winery;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
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



}
