package Admin;

import java.sql.Date;
import java.sql.Time;

public class OrderTable {
    Integer num;
    Integer customerID;
    Integer totalDrink;
    String totalPrice;
    Time time;
    Date date;
    String status;
    String confirmer;
    String accepter;

    Integer drinkNum;
    String drinkDetail;
    Integer drinkQty;
    String drinkPrice;

    //History Table
    public OrderTable (Integer num, Integer customerID, Integer totalDrink, String totalPrice, Date date, String status, String confirmer, String accepter) {
        this.num = num;
        this.customerID = customerID;
        this.totalDrink = totalDrink;
        this.totalPrice = totalPrice;
        this.date = date;
        this.status = status;
        this.confirmer = confirmer;
        this.accepter = accepter;
    }

    //For Cashier Table
    public OrderTable (Integer num, Integer customerID, Integer totalDrink, String totalPrice, Time time, String status) {
        this.num = num;
        this.customerID = customerID;
        this.totalDrink = totalDrink;
        this.totalPrice = totalPrice;
        this.time = time;
        this.status = status;
    }

    //For Barista Table
    public OrderTable (Integer num, Integer customerID, Integer totalDrink, String totalPrice, Time time, String status, String accepter) {
        this.num = num;
        this.customerID = customerID;
        this.totalDrink = totalDrink;
        this.totalPrice = totalPrice;
        this.time = time;
        this.status = status;
        this.accepter = accepter;
    }

    //Detail Table
    public OrderTable (Integer drinkNum, String drinkDetail, String drinkPrice, Integer drinkQty, String totalPrice) {
        this.drinkNum = drinkNum;
        this.drinkDetail = drinkDetail;
        this.drinkPrice = drinkPrice;
        this.drinkQty = drinkQty;
        this.totalPrice = totalPrice;
    }

    //getter
    public Integer getNum() {
        return num;
    }
    public Integer getCustomerID() {
        return customerID;
    }
    public Integer getTotalDrink() {
        return totalDrink;
    }
    public String getTotalPrice() {
        return totalPrice;
    }
    public String getStatus() {
        return status;
    }
    public Date getDate() {
        return date;
    }
    public String getDrinkDetail() {
        return drinkDetail;
    }
    public String  getDrinkPrice() {
        return drinkPrice;
    }
    public Integer getDrinkQty() {
        return drinkQty;
    }
    public Integer getDrinkNum() {
        return drinkNum;
    }
    public Time getTime() {
        return time;
    }
    public String getAccepter() {
        return accepter;
    }
    public String getConfirmer() {
        return confirmer;
    }

    //setter
    public void setNum(Integer num) {
        this.num = num;
    }
    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }
    public void setTotalDrink(Integer totalDrink) {
        this.totalDrink = totalDrink;
    }
    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public void setDrinkDetail(String drinkDetail) {
        this.drinkDetail = drinkDetail;
    }
    public void setDrinkPrice(String drinkPrice) {
        this.drinkPrice = drinkPrice;
    }
    public void setDrinkQty(Integer drinkQty) {
        this.drinkQty = drinkQty;
    }
    public void setDrinkNum(Integer drinkNum) {
        this.drinkNum = drinkNum;
    }
    public void setTime(Time time) {
        this.time = time;
    }
    public void setConfirmer(String confirmer) {
        this.confirmer = confirmer;
    }
    public void setAccepter(String accepter) {
        this.accepter = accepter;
    }
}