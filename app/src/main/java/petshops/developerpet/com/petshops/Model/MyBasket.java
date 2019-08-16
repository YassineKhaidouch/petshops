package petshops.developerpet.com.petshops.Model;

/**
 * Created by root on 2/1/18.
 */

public class MyBasket {
    String productid;
    String imagepath;
    String basketid;
    String datetime;
    String customerid;
    int price;
    int quantity;
    int total;

    public MyBasket(){}

    public MyBasket(
            String productid,
            String imagepath,
            String basketid,
            String datetime,
            String customerid,
            int price,
            int quantity,
            int total
    ){
        this.productid = productid;
        this.imagepath = imagepath;
        this.basketid = basketid;
        this.datetime = datetime;
        this.customerid = customerid;
        this.price = price;
        this.quantity = quantity;
        this.total = total;

    }

    public String getProductid(){return productid;};
    public String getImagepath(){return imagepath;};
    public String getBasketid(){return basketid;};
    public String getDatetime(){return datetime;};
    public String getCustomerid(){return customerid;};
    public int getPrice(){return price;};
    public int getQuantity(){return quantity;};
    public int getTotal(){return total;};



}
