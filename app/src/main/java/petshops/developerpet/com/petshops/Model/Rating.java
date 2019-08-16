package petshops.developerpet.com.petshops.Model;

/**
 * Created by root on 3/22/18.
 */

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Rating {


    public Rating(){}

    public String idPost;
    public String idRater;
    public String idOwner;
    public String description;
    public String idRate;
    public float ratevalue;
}
