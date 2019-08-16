package petshops.developerpet.com.petshops.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.constraint.solver.SolverVariable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

import petshops.developerpet.com.petshops.Activities.Add_Adopation;
import petshops.developerpet.com.petshops.Activities.Notifications;
import petshops.developerpet.com.petshops.Activities.View_Details_Pet;
import petshops.developerpet.com.petshops.MainActivity;
import petshops.developerpet.com.petshops.Model.Add_Sale;
import petshops.developerpet.com.petshops.Model.Notify_me;
import petshops.developerpet.com.petshops.R;
import petshops.developerpet.com.petshops.TimeAgo.getlongtoago;
import petshops.developerpet.com.petshops.data.SharedPreferenceHelper;
import petshops.developerpet.com.petshops.data.StaticConfig;

import static petshops.developerpet.com.petshops.Activities.Notifications.MyNotification;


public class SensorService extends Service {
    public int counter=0;

    public SensorService(Context applicationContext) {
        super();
    }
    public SensorService() {}


    DatabaseReference reference ;
    SharedPreferenceHelper shar;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
//        startTimer();

        return START_STICKY;
    }
    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 10000, 10000); //
    }


    @Override
    public void onCreate() {
        shar = SharedPreferenceHelper.getInstance(this);
        reference = FirebaseDatabase.getInstance().getReference();
        FetchData_new_Adoption("sale");
        FetchData_new_Adoption("adopation");
        FetchData_Biding_Adoption();
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {

            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent(this, SensorService.class);
        sendBroadcast(broadcastIntent);
    }

    void FetchData_new_Adoption(String typedata){
        final String TypeData = typedata;
        final boolean[] notifed = {false};
        reference.child(typedata).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //iterating through all the values in database
                    int notifynumber = 0;
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Add_Sale upload = postSnapshot.getValue(Add_Sale.class);
                        try{
                            int timestamp = (int) new getlongtoago().getlongtoagoSecond(upload.timestamp);
                            if((timestamp < 5 && !notifed[0] ) && (!shar.getUID().equals(upload.getIdowner()))){
                                CreateNotifyNewPost(upload.idPost, TypeData, upload.descriptions, notifynumber, upload);
                                notifynumber++;
                                notifed[0] = true;
                            }
                        }catch (Exception e){}
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    void FetchData_Biding_Adoption(){
        Query query = reference.child("Notifications").orderByChild("idowner").equalTo(StaticConfig.UID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        try {
                            Notify_me notify = postSnapshot.getValue(Notify_me.class);
                            if(!notify.notified && (!shar.getUID().equals(notify.idBider))) {
                                AlertnewBid(notify.nameBider, counter, notify.typeBid, notify);
                                // createNotify(notify.fromid, notify.fromname,counter);
                                reference.child("Notifications").child(postSnapshot.getKey()).child("notified").setValue(true);
                                counter ++;

                            }
                        }catch (Exception e){}
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // notification add user to chat

    public void AlertnewBid(String namebider , int counterid, String notify_type, Notify_me Mynotify) {

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.customnotification);
        //Intent intent = new Intent(this, NotificationView.class);
        Intent intent = new Intent(this, Notifications.class);
        intent.putExtra("notify_type", notify_type );
        MyNotification = Mynotify;
        PendingIntent pIntent = PendingIntent.getActivity(this, counterid , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ppap_notification_topbar)
                .setTicker("PAPP You get new "+notify_type+ " User ...")
                .setAutoCancel(true)
                .setContentIntent(pIntent)
                .setContent(remoteViews);
        long[] vib = {200,2000};
        builder.setLights(1000,1000,2000);
        builder.setVibrate(vib);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);
        remoteViews.setImageViewResource(R.id.imagenotileft, R.mipmap.ppap_notification_small);
        remoteViews.setTextViewText(R.id.title,"New Bid "+notify_type);
        remoteViews.setTextViewText(R.id.text,"My name is "+namebider + " I have been showen your "+notify_type+" post . Show more ...");
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.notify(counterid, builder.build());
    }

    public void CreateNotifyNewPost(String IdPost , String TypePost,String discribe, int id, Add_Sale sale_adoption) {
        int typeid = 0;
        if(TypePost.equals("sale")){
            typeid = 0;
        }else{
            typeid = 1;
        }
        Intent activityIntent = new Intent(this, View_Details_Pet.class);
      //  activityIntent.putExtra("AlertNew", TypePost ); // alert new post
     //   activityIntent.putExtra("idPost", IdPost);
        activityIntent.putExtra("id",typeid);
        Add_Adopation.Data_Adopation_sale = sale_adoption;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, id, activityIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ppap_notification_topbar)
                .setTicker("PAPP people for Pets ")
                .setContentTitle("New "+TypePost+" Post ")
                .setContentText(discribe)
                .setContentIntent(pendingIntent)
                .setLights(1000,1000,2000)
                .setVibrate(new long[] { 1000, 1000})
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true);
            notificationBuilder.setSmallIcon(R.mipmap.ppap_notification_topbar);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
        notificationManager.notify(id, notificationBuilder.build());
    }
}
