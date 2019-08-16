package petshops.developerpet.com.petshops.Model;

/**
 * Created by root on 2/13/18.
 */
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String mobile;
    public String email;
    public String id;
    public String password;
    public String name;
    public String planType;
    public boolean planned;
    public String create_at;
    public String avata;
    public Status status;
    public Message message;


    public User(){
        status = new Status();
        message = new Message();
        status.isOnline = false;
        status.timestamp = 0;
        message.idReceiver = "0";
        message.idSender = "0";
        message.text = "";
        message.timestamp = 0;
    }
 //User(String name,String mobile,String email,String id,String password){

}
