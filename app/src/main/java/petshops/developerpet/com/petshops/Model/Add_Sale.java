package petshops.developerpet.com.petshops.Model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by root on 3/19/18.
 */


@IgnoreExtraProperties
public class Add_Sale {
/*

    image 1,2,3
    pet type
    breed
    age : years months
    price
    owner
    phone
    descriptions

 */
    public String idPost;
    public String idowner;
    public String type;
    public String ownername;
    public String breed;
    public String years;
    public String months;
    public String price;
    public String duration;
    public String phone;
    public String descriptions;
    public long timestamp;
    public String path1;
    public String path2;
    public String path3;
    public String create_at;

    public Add_Sale(){}

    public Add_Sale(
            String idPost,
            String idowner,
            String type,
            String ownername,
            String breed,
            String years,
            String duration,
            Long timestamp,
            String months,
            String price,
            String phone,
            String descriptions,
            String path1,
            String path2,
            String path3,
            String create_at){
        this.idowner = idowner;
        this.idPost = idPost;
        this.type = type;
        this.ownername = ownername;
        this.breed = breed;
        this.duration = duration;
        this.years = years;
        this.timestamp = timestamp;
        this.months = months;
        this.price = price;
        this.phone = phone;
        this.descriptions = descriptions;
        this.path1 = path1;
        this.path2 = path2;
        this.path3 = path3;
        this.create_at = create_at;
    }

    public String getCreate_at(){return  create_at;}
    public long getTimestamp(){return timestamp;}
    public String getDuration(){return duration;}
    public String getIdPost(){return  idPost;}
    public String getOwnername(){return  ownername;}
    public String getIdowner(){return  idowner;}
    public String getType(){return  type;}
    public String getBreed(){return breed;}
    public String getYears(){return years;}
    public String getMonths(){return months;}
    public String getPrice(){return price;}
    public String getPhone(){return phone;}
    public String getDescriptions(){return descriptions;}
    public String getPath1(){return path1;}
    public String getPath2(){return path2;}
    public String getPath3(){return path3;}
}
