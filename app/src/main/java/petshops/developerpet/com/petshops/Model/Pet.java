package petshops.developerpet.com.petshops.Model;

/**
 * Created by root on 2/13/18.
 */

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Pet {

    public String idPost;
    public String id;
    public String type;
    public String name;
    public String openfrom;
    public String opento;
    public String wopenfrom;
    public String wopento;
    public String contactnumber;
    public String contactperson;
    public String available;
    public String availableproducts;
    public float rate;
    public double latitude;
    public double longitude;
    public float distance;
    public String address;

    public String path1;
    public String path2;
    public String path3;

    public Pet(){}


    public Pet(
            String idPost,
            String id,
            String type,
            String nameshope,
            String openfrom,
            String opento,
            String wopenfrom,
            String wopento,
            String contactnumber,
            String contactperson,
            String available,
            String availableproducts,
            float rate,
            double latitude,
            double longitude,
            String myAdd,
            String path1,
            String path2,
            String path3
    ){

        this.id = id;
        this.idPost = idPost;
        this.name = nameshope;
        this.type = type;
        this.openfrom = openfrom;
        this.opento = opento;
        this.wopenfrom = wopenfrom;
        this.wopento = wopento;
        this.contactnumber = contactnumber;
        this.contactperson = contactperson;
        this.available = available;
        this.availableproducts = availableproducts;
        this.rate = rate;

        this.latitude = latitude;
        this.longitude = longitude;
        this.address = myAdd;
        this.path1 = path1;
        this.path2 = path2;
        this.path3 = path3;


    }

    public void setDistance(float distance){
        this.distance = distance;
    }

    public String getIdPost(){return  idPost;}
    public String getId(){return  id;}
    public String getType(){return  type;}
    public String getName(){return  name;}
    public String getOpenfrom(){ return openfrom;}
    public String getOpento (){ return opento;}
    public String getWopenfrom(){ return  wopenfrom;}
    public String getWopento(){ return  wopento;}
    public String getContactnumber(){ return  contactnumber;}
    public String getContactperson(){ return  contactperson;}
    public String getAvailablepets(){ return  available;}
    public String getAvailableproducts(){ return availableproducts;}
    public float getRate(){return rate;}

    public double getLatitude(){return latitude;}
    public double getLongitude(){return longitude;}
    public float getDistance(){return distance;}
    public String getAddress(){return address;}
    public String getPath1(){return path1;}
    public String getPath2(){return path2;}
    public String getPath3(){return path3;}
}
