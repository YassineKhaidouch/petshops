package petshops.developerpet.com.petshops.Model;

/**
 * Created by root on 1/20/18.
 */

public class Notify_me {

    public long timestamp;
    public long timestampAdoption;
    public String idBid;
    public String idBider;
    public String idowner;
    public String idpost;
    public String nameBider;
    public String nameOwner;
    public String typeBid; //adoption | sale
    public String description ;
    public boolean notified ; // true | false
    public String status ; // waiting | accepted | refused | deleted

    //   public String avata;

    public Notify_me(){}


}
