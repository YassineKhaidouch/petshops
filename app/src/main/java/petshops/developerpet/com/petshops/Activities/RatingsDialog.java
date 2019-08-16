package petshops.developerpet.com.petshops.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import petshops.developerpet.com.petshops.Model.Rating;
import petshops.developerpet.com.petshops.data.StaticConfig;
import petshops.developerpet.com.petshops.R;


public class RatingsDialog extends AppCompatActivity {

    RatingBar rating_bar;
    EditText Comment;
    TextView Submit;
    String idPost, idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ratings_dialog);

        Intent i = getIntent();
        idPost = i.getStringExtra("idpost");
        idUser = i.getStringExtra("iduser");

        if(idPost == null && StaticConfig.UID == null){
            return;
        }

        rating_bar = (RatingBar) findViewById(R.id.rating_bar);
        Comment = (EditText) findViewById(R.id.comment);

        rating_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                msg("rating : "+String.valueOf(rating_bar.getRating()));
            }
        });

        Submit = (TextView) findViewById(R.id.submit);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String comments = Comment.getText().toString();
                    if (TextUtils.isEmpty(comments)) {
                        msg("You should put comments");
                        Comment.setError("add text ...");
                        return;
                    }

                    Submit.setEnabled(false);
                    Submit.setBackgroundColor(Color.GREEN);

                    Rating rate = new Rating();
                    rate.description = Comment.getText().toString().trim();
                    rate.idPost = idPost;
                    rate.idOwner = idUser;
                    rate.ratevalue = rating_bar.getRating();
                    rate.idRater = StaticConfig.UID;
                    rate.idRate = "//push";


                    FirebaseDatabase.getInstance().getReference("rate").push().setValue(rate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            try {
                                FirebaseDatabase.getInstance().getReference("rate").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            int i = 0, sum = 0;
                                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                                Rating upload = postSnapshot.getValue(Rating.class);
                                                i++;
                                                sum += upload.ratevalue;
                                            }
                                            FirebaseDatabase.getInstance().getReference("data").child(idPost).child("rate").setValue(sum / i).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    msg("post uploaded successfully");
                                                    //startActivity(new Intent(RatingsDialog.this, SuccessRating.class));
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    msg("post uploaded failed\n try again");
                                                }
                                            });
                                            ;

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            } catch (Exception e) {
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            msg("post uploaded failed\n try again");
                        }
                    });
                }catch (Exception e){}
            }
        });

        ((TextView) findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    void msg(String text){
        Toast.makeText(this,text,Toast.LENGTH_LONG).show();
    }
}
