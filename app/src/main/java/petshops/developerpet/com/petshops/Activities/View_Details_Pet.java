package petshops.developerpet.com.petshops.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import petshops.developerpet.com.petshops.DataBase.SharedReferenceHelper;
import petshops.developerpet.com.petshops.Model.Add_Sale;
import petshops.developerpet.com.petshops.Model.Notify_me;
import petshops.developerpet.com.petshops.R;
import petshops.developerpet.com.petshops.data.StaticConfig;

import static petshops.developerpet.com.petshops.Activities.ViewImageGallery.imageIDs;


public class View_Details_Pet extends AppCompatActivity {

    public static Add_Sale ListViewPets = new Add_Sale();
    Button CallNow, ChatNow, interested;
    boolean exist = false;
    boolean bidExist = false;

    DatabaseReference mDatabase;
    FirebaseDatabase mInstance;

    SharedReferenceHelper share;
    LovelyProgressDialog dialogWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details_pet);
        getSupportActionBar().setTitle(getResources().getString(R.string.pet_details));


        try{

        dialogWait = new LovelyProgressDialog(this);
        share = SharedReferenceHelper.getInstance(this);

        StaticConfig.UID = share.getUID();

        mInstance = FirebaseDatabase.getInstance();
        mDatabase = mInstance.getReference("Notifications");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView image1 = (ImageView) findViewById(R.id.image1);
        ImageView image2 = (ImageView) findViewById(R.id.image2);
        ImageView image3 = (ImageView) findViewById(R.id.image3);

        Glide.with(this).load(ListViewPets.getPath1()).into(image1);
        Glide.with(this).load(ListViewPets.getPath2()).into(image2);
        Glide.with(this).load(ListViewPets.getPath3()).into(image3);
            imageIDs.clear();
            if(!ListViewPets.getPath1().equals("empty")){
                imageIDs.add(ListViewPets.getPath1());
            }if(!ListViewPets.getPath2().equals("empty")){
                imageIDs.add(ListViewPets.getPath2());
            }if(!ListViewPets.getPath3().equals("empty")){
                imageIDs.add(ListViewPets.getPath3());
            }

            ((LinearLayout) findViewById(R.id.imagelayoutview)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(View_Details_Pet.this, ViewImageGallery.class));
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

                }
            });

        interested = (Button) findViewById(R.id.interested);
        CallNow = (Button) findViewById(R.id.callnow);
        ChatNow = (Button) findViewById(R.id.chatnow);
        RelativeLayout contactView = (RelativeLayout) findViewById(R.id.contactView);

        int i = getIntent().getIntExtra("id", 0);


        if (i == 1) {
            if(!ListViewPets.getIdowner().equals(StaticConfig.UID)) {
                CallNow.setVisibility(View.GONE);
                ChatNow.setVisibility(View.GONE);
                interested.setVisibility(View.VISIBLE);
                contactView.setVisibility(View.GONE);
            }else{
                contactView.setVisibility(View.GONE);
                CallNow.setVisibility(View.GONE);
                ChatNow.setVisibility(View.GONE);
                interested.setVisibility(View.GONE);
            }
            ((RelativeLayout) findViewById(R.id.ageview)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.textview1)).setText("Duration to Adopation ");
            ((TextView) findViewById(R.id.price)).setText(ListViewPets.getDuration());
        } else {
            ((TextView) findViewById(R.id.price)).setText(ListViewPets.getPrice());

            if (!ListViewPets.getIdowner().equals(StaticConfig.UID)) {
                CallNow.setVisibility(View.VISIBLE);
                ChatNow.setVisibility(View.VISIBLE);
                contactView.setVisibility(View.VISIBLE);
                interested.setVisibility(View.GONE);
            }else{
                CallNow.setVisibility(View.GONE);
                ChatNow.setVisibility(View.GONE);
                interested.setVisibility(View.GONE);
                contactView.setVisibility(View.GONE);
            }
        }



        ((TextView) findViewById(R.id.typepet)).setText(ListViewPets.getType());
        ((TextView) findViewById(R.id.breed)).setText(ListViewPets.getBreed());
        ((TextView) findViewById(R.id.years)).setText(ListViewPets.getYears());
        ((TextView) findViewById(R.id.months)).setText(ListViewPets.getMonths());
        ((TextView) findViewById(R.id.ownername)).setText(ListViewPets.getOwnername());
        ((TextView) findViewById(R.id.phone)).setText(ListViewPets.getPhone());
        ((TextView) findViewById(R.id.description)).setText(ListViewPets.getDescriptions());


        interested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // see if adoption bid was created
                interested.setEnabled(false);
                bidExist = false;
                Query query = mDatabase.orderByChild("idBider").equalTo(StaticConfig.UID);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                try {
                                    Notify_me notify = postSnapshot.getValue(Notify_me.class);
                                    if(notify.idpost.equals(ListViewPets.getIdPost())) {
                                        bidExist = true;
                                    }
                                }catch (Exception e){}
                            }
                        }
                        if (bidExist) {
                            dialogWait.dismiss();
                            new LovelyInfoDialog(View_Details_Pet.this)
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setIcon(R.drawable.ic_add_friend)
                                    .setTitle("This Intersted For this Adoption Already  Exist")
                                    .setMessage("You should waite acceptions from users")
                                    //.setMessage("if your describe text not impact for employer, you can edit your bid ...")
                                    .show();
                        } else {
                            AddNewBid();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                // else create one
            }
        });
        CallNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.content.Intent.ACTION_DIAL, Uri.parse("tel:" + ListViewPets.getPhone())));
            }
        });

        ChatNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add friend ...
                //
                ChatNow.setEnabled(false);
                exist = false;
                FirebaseDatabase.getInstance().getReference().child("friend/" + StaticConfig.UID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                String idCust = d.getValue(String.class);
                                if (idCust.equals(ListViewPets.getIdowner()) ) {
                                    exist = true;
                                    //Toast.makeText(View_Details_Pet.this, "id owner "+idCust, Toast.LENGTH_SHORT).show();
                                    //return;
                                }
                            }
                        }
                        if (exist) {
                            dialogWait.dismiss();
                            new LovelyInfoDialog(View_Details_Pet.this)
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setIcon(R.drawable.ic_add_friend)
                                    .setTitle("Fail")
                                    .setMessage("Friend Already exist ,go to Inbox page")
                                    .show();

                        } else {
                            addFriend(ListViewPets.getIdowner(),true);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }
        });

        }catch (Exception e){
            Toast.makeText(this, "ERRor L653 : "+e, Toast.LENGTH_SHORT).show();
        }

    }
    // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void addFriend(final String idFriend, boolean isIdFriend) {
        if (idFriend != null){
            if (isIdFriend) {
                FirebaseDatabase.getInstance().getReference().child("friend/" + StaticConfig.UID).push().setValue(idFriend)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    addFriend(idFriend, false);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialogWait.dismiss();
                                new LovelyInfoDialog(View_Details_Pet.this)
                                        .setTopColorRes(R.color.colorAccent)
                                        .setIcon(R.drawable.ic_add_friend)
                                        .setTitle("False")
                                        .setMessage("False to add friend success")
                                        .show();
                            }
                        });
            } else {
                FirebaseDatabase.getInstance().getReference().child("friend/" + idFriend).push().setValue(StaticConfig.UID).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            addFriend(null, false);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialogWait.dismiss();
                                new LovelyInfoDialog(View_Details_Pet.this)
                                        .setTopColorRes(R.color.colorAccent)
                                        .setIcon(R.drawable.ic_add_friend)
                                        .setTitle("False")
                                        .setMessage("False to add friend success")
                                        .show();
                            }
                        });
            }
        } else {
            dialogWait.dismiss();
            new LovelyInfoDialog(this)
                    .setTopColorRes(R.color.colorPrimary)
                    .setIcon(R.drawable.ic_add_friend)
                    .setTitle("Success")
                    .setMessage("Add friend success")
                    .show();
        }
    }

    public  void AddNewBid(){

        // show dialog add text
        // Apply now
        // cancel

        View view = null;
        View vewInflater = LayoutInflater.from(this).inflate(R.layout.dialog_edit_bid,(ViewGroup) view  , false);
        final EditText input = (EditText)vewInflater.findViewById(R.id.edit_bid);
        new AlertDialog.Builder(this)
                .setTitle("Add bid for Adoption")
                .setView(vewInflater)
                .setPositiveButton("Apply now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        String testDiscribe = input.getText().toString();
                        if(TextUtils.isEmpty(testDiscribe)){
                            input.setError("you should type a text here ");
                            return;
                        }
                        Notify_me create_bid = new Notify_me();

                        String pushKey =  mDatabase.push().getKey();
                        create_bid.timestampAdoption =  ListViewPets.getTimestamp();
                        create_bid.timestamp =  System.currentTimeMillis();
                        create_bid.idBid = pushKey;
                        create_bid.idBider = StaticConfig.UID;
                        create_bid.idpost = ListViewPets.getIdPost();
                        create_bid.idowner = ListViewPets.getIdowner();
                        create_bid.nameBider = share.getName();
                        create_bid.nameOwner = ListViewPets.getOwnername();
                        create_bid.typeBid = "adoption" ;// ListViewPets.getType(); //adoption | sale
                        create_bid.description = testDiscribe;
                        create_bid.notified = false ; // true | false
                        create_bid.status = "waiting"; // waiting | accepted | refused | deleted
                        // data set ///
                        mDatabase.child(pushKey).setValue(create_bid).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialogInterface.dismiss();
                                new LovelyInfoDialog(View_Details_Pet.this)
                                        .setTopColorRes(R.color.green1)
                                        .setIcon(R.drawable.ic_add_friend)
                                        .setTitle("Bid Added Successfully")
                                        .setMessage("Waite a notification from Owner of this Adopting")
                                        .show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialogInterface.dismiss();
                                new LovelyInfoDialog(View_Details_Pet.this)
                                        .setTopColorRes(R.color.red1)
                                        .setIcon(R.drawable.ic_add_friend)
                                        .setTitle("False")
                                        .setMessage("False to add Bid success\nTry again")
                                        .show();
                            }
                        });
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }
}

