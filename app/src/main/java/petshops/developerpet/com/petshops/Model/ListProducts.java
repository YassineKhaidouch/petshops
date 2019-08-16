package petshops.developerpet.com.petshops.Model;

/**
 * Created by root on 1/25/18.
 */
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ListProducts {


    public String title;
    public String imagepath;
    public int price;
    public String productsId;
    public String description;
    public String pushkey;
    public String postDate;
    public String category;
    public String baskets;


    public ListProducts(){}

    public ListProducts(String Title , String Imagepath, int Price, String ProductsId, String Description,String Pushkey,String category,String postDate){
        this.title = Title ;
        this.imagepath = Imagepath;
        this.price = Price;
        this.productsId = ProductsId;
        this.pushkey = Pushkey;
        this.description = Description;
        this.category = category;
        this.postDate = postDate;
    }

    public int getPrice (){return price ;}
    public String getTitle (){return title ;}
    public String getImagepath (){return imagepath ;}
    public String getProductsId (){return productsId ;}
    public String getDescription (){return description ;}
    public String getPushkey (){return pushkey ;}
    public String getPostDate (){return postDate ;}
    public String getCategory (){return category ;}
    public String getBaskets (){return baskets ;}

}
