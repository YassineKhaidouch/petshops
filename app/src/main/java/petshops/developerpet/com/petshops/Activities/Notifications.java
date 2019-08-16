package petshops.developerpet.com.petshops.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

import petshops.developerpet.com.petshops.Model.Notify_me;
import petshops.developerpet.com.petshops.R;
import petshops.developerpet.com.petshops.TimeAgo.getlongtoago;
import petshops.developerpet.com.petshops.data.StaticConfig;

public class Notifications extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    Switch switchAB;
    RecyclerviewAdapter recycler;
    RecyclerView recyclerview;
    List<Notify_me>  notifyMe = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public  static Notify_me MyNotification = new Notify_me();
    Query query;
    private LovelyProgressDialog dialogWait;
    DatabaseReference reference;

    Button deleteAll, selecteAll;
    List<String> SelectedNotify = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);



        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.switch_layout);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);

        Switch Switchbutton = (Switch) findViewById(R.id.switchAB);
        Switchbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(Notifications.this, "On", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Notifications.this, "Off", Toast.LENGTH_SHORT).show();
                }
            }
        });


        ((ImageView)findViewById(R.id.backToHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        dialogWait = new LovelyProgressDialog(this).setCancelable(false);

        reference = FirebaseDatabase.getInstance().getReference("Notifications");

        Intent i = new Intent();

        if(i.getStringExtra("notify_type") != null){

            Notify_me notify_me = new Notify_me();
            notify_me.description = MyNotification.description;
            notify_me.nameBider = MyNotification.nameBider;
            notify_me.idBider = MyNotification.idBider;
            notify_me.nameOwner = MyNotification.nameOwner;
            notify_me.typeBid = MyNotification.typeBid;
            notify_me.status = MyNotification.status;
            notify_me.notified = MyNotification.notified;


            notifyMe.add(notify_me);


            RecyclerviewAdapter recycler = new RecyclerviewAdapter(notifyMe);
            RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(Notifications.this);
            recyclerview.setLayoutManager(layoutmanager);
            recyclerview.setItemAnimator(new DefaultItemAnimator());
            recyclerview.setAdapter(recycler);

            return;
        }else{
            FetchNotification();
        }

        selecteAll = (Button) findViewById(R.id.selecteAll);
        deleteAll = (Button) findViewById(R.id.deleteAll);
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(String idbids : SelectedNotify ){
                    FirebaseDatabase.getInstance().getReference("Notifications").child(idbids).removeValue();
                }
            }
        });
        selecteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Notify_me idbid : notifyMe){
                    FirebaseDatabase.getInstance().getReference("Notifications").child(idbid.idBid).removeValue();
                }
            }
        });
    }
    @Override
    public void onRefresh() {
        FetchNotification();
    }
    void FetchNotification(){

        try {
            query = reference.orderByChild("idowner").equalTo(StaticConfig.UID);
            query.addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){
                    notifyMe.clear();
                    if(dataSnapshot.exists()){
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Notify_me notify = postSnapshot.getValue(Notify_me.class);
                            notifyMe.add(notify);
                        }
                        recycler = new RecyclerviewAdapter(notifyMe);
                        RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(Notifications.this);
                        recyclerview.setLayoutManager(layoutmanager);
                        recyclerview.setItemAnimator(new DefaultItemAnimator());
                        recyclerview.setAdapter(recycler);
                        recycler.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }else{
                        recycler = new RecyclerviewAdapter(notifyMe);
                        RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(Notifications.this);
                        recyclerview.setLayoutManager(layoutmanager);
                        recyclerview.setItemAnimator(new DefaultItemAnimator());
                        recyclerview.setAdapter(recycler);
                        recycler.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                        msg("No one Interested for your Adoption yet :)");
                        // display pic waiting interested
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }catch (Exception e){
            msg("error when fetching data : "+e);
        }
    }


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        switchAB = (Switch)menu.findItem(R.id.switchId).getActionView().findViewById(R.id.switchAB);
        switchAB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplication(), "ON", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplication(), "OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return true;
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
*/


    public void msg(String text) {
        Toast.makeText(Notifications.this, text, Toast.LENGTH_LONG).show();
    }

    class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyHolder> {
        List<Notify_me> notifyme ;
        public RecyclerviewAdapter(List<Notify_me> notifyme) {
            this.notifyme = notifyme;
        }
        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list, parent, false);
            MyHolder myHolder = new MyHolder(view);
            return myHolder;
        }

        public void onBindViewHolder(MyHolder holder, int position) {
            Notify_me data = notifyme.get(position);
            /*
    public boolean notified ; // true | false
    public String status ; // waiting | accepted | refused | deleted
             */
            holder.name.setText("From "+data.nameBider+" To me ("+data.nameOwner+")");
            holder.txtPostDetails.setText("Details : ...");
            holder.txtDescription.setText("Describe : "+data.description);
            holder.txtTimestamp.setText("\nStatus : "+data.status+"         "+new getlongtoago().getlongtoago(data.timestamp));
            /*
            if (data.email.equals("old")) {
                holder.name.setText("Notification : " + data.getname());
                holder.email.setText("Waiting accepting ...");
            } else if (data.email.equals("new")) {
                holder.name.setText("Notification : " + data.getname());
                holder.email.setText("Status : New Notification");
            } else if (data.email.equals("accept")) {
                holder.name.setText("Notification : " + data.getname());
                holder.email.setText("Status : Accepted");
            } else if (data.email.equals("refuse")) {
                holder.name.setText("Notification : " + data.getname());
                holder.email.setText("Status : Refused");
            } else {
                holder.name.setText(data.getname());
                holder.email.setText(data.getemail());
            }
            try {
                if (data.avatar.equals("default")) {
                    holder.avatar.setImageResource(R.drawable.default_avata);
                } else {
                    byte[] decodedString = Base64.decode(data.avatar, Base64.DEFAULT);
                    Bitmap src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.avatar.setImageBitmap(src);
                }
            } catch (Exception e) {
            }
            */

        }
        @Override
        public int getItemCount() {
            return notifyme.size();
        }
        class MyHolder extends RecyclerView.ViewHolder {
            TextView  txtPostDetails, txtDescription, name, txtTimestamp ;
            Button startChat, refuseChat;
            CheckBox checkBox;
            boolean exist = false;
            private final Context context;
            public MyHolder(final View itemView) {
                super(itemView);
                context = itemView.getContext();
                final Intent[] intent = new Intent[1];
                checkBox = (CheckBox) itemView.findViewById(R.id.check);
                name = (TextView) itemView.findViewById(R.id.txtName);
                txtPostDetails = (TextView) itemView.findViewById(R.id.txtPostDetails);
                txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
                txtTimestamp = (TextView) itemView.findViewById(R.id.txtTimestamp);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            deleteAll.setVisibility(View.VISIBLE);
                            String idbid = notifyme.get(getAdapterPosition()).idBid;
                            SelectedNotify.add(idbid);
                            if(SelectedNotify.size() >= 2){
                                selecteAll.setVisibility(View.VISIBLE);
                            }else{
                                selecteAll.setVisibility(View.GONE);
                            }
                        }else{
                            // remove
                            // SelectedNotify.remove(getAdapterPosition());
                        }
                    }
                });
                startChat = (Button) itemView.findViewById(R.id.startChat);
                refuseChat = (Button) itemView.findViewById(R.id.refuseChat);
                startChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // add friend | notify user you have been accepted
                        // send new message auto "I am interested for your bid ..."
                        exist = false;
                        FirebaseDatabase.getInstance().getReference().child("friend/" + StaticConfig.UID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                                        String idCust = d.getValue(String.class);
                                        if (idCust.equals(notifyme.get(getAdapterPosition()).idBider)) {
                                            exist = true;
                                            //Toast.makeText(getApplicationContext(), "id owner " + idCust + "\nExist  >>> open chat auto...", Toast.LENGTH_SHORT).show();
                                            // open chat activity idbider + myUId
                                            return;
                                        }
                                    }
                                }
                                if (exist) {
                                    dialogWait.dismiss();
                                    new LovelyInfoDialog(Notifications.this)
                                            .setTopColorRes(R.color.colorPrimary)
                                            .setIcon(R.drawable.ic_add_friend)
                                            .setTitle("Fail")
                                            .setMessage("Custmore Already exist ,go to recent chat page")
                                            .show();
                                } else {
                                    addFriend(notifyme.get(getAdapterPosition()).idBider, true);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                });
                refuseChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String idbid = notifyme.get(getAdapterPosition()).idBid;
                        new AlertDialog.Builder(context)
                                .setTitle("Delete Notification")
                                .setMessage("Are you sure, you want to delete  this Notification ?")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        FirebaseDatabase.getInstance().getReference("Notifications").child(idbid).removeValue();
                                        // notify bidder (him notify is deleted by user == "Onername" || us () )

                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();

                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*
                        String Id = notifyme.get(getAdapterPosition()).getid();
                        intent[0] = new Intent(context, UserProfile.class);
                        intent[0].putExtra("idDoctor", Id);
                        if (StaticConfig.STR_EXTRA_TYPE_USER.equals("Doctor")) {
                            intent[0].putExtra("Data", "notify");
                        } else {
                            intent[0].putExtra("Data", "visitor");
                        }
                        context.startActivity(intent[0]);
                        */
                    }
                });

            }

            private void addFriend(final String idFriend, boolean isIdFriend) {
                if (idFriend != null) {
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
                                        new LovelyInfoDialog(context)
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
                                new LovelyInfoDialog(context)
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
                    new LovelyInfoDialog(context)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_add_friend)
                            .setTitle("Success")
                            .setMessage("Add friend success")
                            .show();
                    // open chat activity myid + idbider

                    // id room + my id
                }
            }

        }
    }
}
