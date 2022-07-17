package User;

public class Table {
    Integer num;
    String drinkName;
    String coffeeType;
    String price;
    Integer quantity;
    String totalPrice;

    //non-d constructor
    public Table (Integer num, String drinkName, String coffeeType, String price, Integer quantity, String totalPrice) {
        this.num = num;
        this.drinkName = drinkName;
        this.coffeeType = coffeeType;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    //getter
    public Integer getNum() {
        return num;
    }
    public String getDrinkName() {
        return drinkName;
    }
    public String getCoffeeType() {
        return coffeeType;
    }
    public String getPrice() {
        return price;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public String getTotalPrice() {
        return totalPrice;
    }

    //setter
    public void setNum(Integer num) {
        this.num = num;
    }
    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }
    public void setCoffeeType(String coffeeType) {
        this.coffeeType = coffeeType;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
