package petshops.developerpet.com.petshops.Model;

/**
 * Created by root on 2/2/18.
 */

public class DataPayment {
    String pricepayed;
    String idpayed;
    String statepayed;
    public DataPayment(){}
    public DataPayment(String pricepayed, String idpayed, String statepayed){
        this.pricepayed = pricepayed;
        this.idpayed = idpayed;
        this.statepayed = statepayed;
    }
    public String pricepayed(){return pricepayed;}
    public String idpayed(){return idpayed;}
    public String statepayed(){return statepayed;}

}
