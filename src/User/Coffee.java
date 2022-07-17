package User;

public class Coffee {
    //fields
    private String name;
    private String imgSrc;
    private double hotPrice;
    private double icedPrice;
    private double frappeePrice;
    public static final String CURRENCY = "$";

    //non-default construct
    Coffee (String name, Double hotPrice, Double icedPrice, Double frappeePrice, String imgSrc) {
        this.name = name;
        this.hotPrice = hotPrice;
        this.icedPrice = icedPrice;
        this.frappeePrice = frappeePrice;
        this.imgSrc = imgSrc;
    }
    
    //getter
    public String getName() {
        return name;
    }
    public double getHotPrice() {
        return hotPrice;
    }
    public double getIcedPrice() {
        return icedPrice;
    }
    public double getFrappeePrice() {
        return frappeePrice;
    }
    public String getImgSrc() {
        return imgSrc;
    }

    //setter
    public void setName(String name) {
        this.name = name;
    }
    public void setHotPrice(double hotPrice) {
        this.hotPrice = hotPrice;
    }
    public void setIcedPrice(double icedPrice) {
        this.icedPrice = icedPrice;
    }
    public void setFrappeePrice(double frappeePrice) {
        this.frappeePrice = frappeePrice;
    }
    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }
}
