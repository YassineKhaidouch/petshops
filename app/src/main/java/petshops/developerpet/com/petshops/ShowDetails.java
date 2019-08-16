package petshops.developerpet.com.petshops;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import petshops.developerpet.com.petshops.Activities.RatingsDialog;
import petshops.developerpet.com.petshops.Activities.ViewImageGallery;
import petshops.developerpet.com.petshops.Activities.View_Details_Pet;
import petshops.developerpet.com.petshops.Model.Pet;
import petshops.developerpet.com.petshops.data.StaticConfig;

import static petshops.developerpet.com.petshops.Activities.ViewImageGallery.imageIDs;


public class ShowDetails extends AppCompatActivity {
    ImageView imageView;
    String idPost, idUser, distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);

        Intent i = getIntent();
        idPost = i.getStringExtra("idpost");
        idUser = i.getStringExtra("iduser");
        distance = i.getStringExtra("distance");

        if(idPost == null && idUser == null){
            return;
        }

        imageView = (ImageView) findViewById(R.id.imageView);
        ((ImageView) findViewById(R.id.back_to)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ratings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idUser.equals(StaticConfig.UID)){
                    msg("You Couldn't Rate YOUR account !!!");
                    return;
                }
                startActivity(new Intent(ShowDetails.this, RatingsDialog.class)
                        .putExtra("idpost",idPost)
                        .putExtra("iduser",idUser)
                );
            }
        });

        ((TextView) findViewById(R.id.distance)).setText(distance+"Km away");

        FirebaseDatabase.getInstance().getReference("data").child(idPost).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Pet pet = dataSnapshot.getValue(Pet.class);
                    ((TextView) findViewById(R.id.openDays)).setText(pet.getOpenfrom()+" - "+pet.getOpento());
                    ((TextView) findViewById(R.id.weekends)).setText(pet.getWopenfrom()+" - "+pet.getWopento());
                    ((TextView) findViewById(R.id.address)).setText(pet.getAddress());
                    ((TextView) findViewById(R.id.contacts)).setText(pet.getContactnumber()+"\n"+pet.getContactperson());
                    ((TextView) findViewById(R.id.textname)).setText(pet.getName());
                    Glide.with(ShowDetails.this).load(pet.getPath1()).into(imageView);

                    ((LinearLayout) findViewById(R.id.ratings)).setVisibility(View.VISIBLE);
                    final String tell = pet.getContactnumber();
                    ((LinearLayout) findViewById(R.id.callnow)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {;
                            startActivity(new Intent(android.content.Intent.ACTION_DIAL, Uri.parse("tel:" + tell)));

                        }
                    });
                    final String path1 = pet.getPath1();
                    final String path2 = pet.getPath2();
                    final String path3 = pet.getPath3();
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageIDs.clear();
                            if(!path1.equals("empty")){
                                imageIDs.add(path1);
                            }
                            if(!path2.equals("empty")){
                                imageIDs.add(path2);
                            }
                            if(!path3.equals("empty")){
                                imageIDs.add(path3);
                            }
                            startActivity(new Intent(ShowDetails.this, ViewImageGallery.class));
                            overridePendingTransition(R.anim.slide_up, R.anim.slide_up);

                        }
                    });
                }else{

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void msg(String text){
        Toast.makeText(this,text,Toast.LENGTH_LONG).show();
    }


}
