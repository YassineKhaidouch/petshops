package petshops.developerpet.com.petshops.Model;

/**
 * Created by root on 2/26/18.
 */

public class CommentsRatings {
    String idCommenter;
    String idPost;
    String comment;
    float rate;

    public CommentsRatings(){}

    public CommentsRatings(String idCommenter,String idPost,String comment,float rate){
        this.idPost = idPost;
        this.comment = comment;
        this.idCommenter = idCommenter;
        this.rate = rate;
    }

    public float getRate(){return  rate;}
    public String getComment(){return  comment;}
    public String getIdCommenter(){return idCommenter;}
    public String getIdPost(){return idPost;}
}
