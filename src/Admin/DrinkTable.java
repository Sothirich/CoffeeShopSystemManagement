package Admin;

public class DrinkTable {
    Integer num;
    String drinkName;
    String hotPrice;
    String icedPrice;
    String frappeePrice;

    //non-d constructor
    public DrinkTable (Integer num, String drinkName, String hotPrice, String icedPrice, String frappeePrice) {
        this.num = num;
        this.drinkName = drinkName;
        this.hotPrice = hotPrice;
        this.icedPrice = icedPrice;
        this.frappeePrice = frappeePrice;
    }

    //getter
    public Integer getNum() {
        return num;
    }
    public String getDrinkName() {
        return drinkName;
    }
    public String getHotPrice() {
        return hotPrice;
    }
    public String getFrappeePrice() {
        return frappeePrice;
    }
    public String getIcedPrice() {
        return icedPrice;
    }


    //setter
    public void setNum(Integer num) {
        this.num = num;
    }
    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }
    public void setFrappeePrice(String frappeePrice) {
        this.frappeePrice = frappeePrice;
    }
    public void setHotPrice(String hotPrice) {
        this.hotPrice = hotPrice;
    }
    public void setIcedPrice(String icedPrice) {
        this.icedPrice = icedPrice;
    }
}