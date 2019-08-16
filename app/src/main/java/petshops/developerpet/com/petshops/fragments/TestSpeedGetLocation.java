package petshops.developerpet.com.petshops.fragments;


import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.TimeUnit;

import petshops.developerpet.com.petshops.R;


public class TestSpeedGetLocation extends Fragment implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static final long INTERVAL = 1000 * 2;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    TextView time, data_location;
    boolean locationGot = true;

    Long StartTime , StopTime ;
    public TestSpeedGetLocation(){}
    void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    View rootview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootview = inflater.inflate(R.layout.fragment_test_speed_get_location, container, false);
        time = (TextView) rootview.findViewById(R.id.timetext);
        data_location = (TextView) rootview.findViewById(R.id.data_location);

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        mGoogleApiClient.connect();

        StartTime = System.currentTimeMillis();

        return rootview;
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
        }
    }


    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onLocationChanged(Location location) {
        StopTime = System.currentTimeMillis();
        long diff = StopTime - StartTime;
        diff = TimeUnit.MILLISECONDS.toSeconds(diff);
        time.setText("delay Time : " + diff + " seconds");

        data_location.setText("get lat : "+location.getLatitude()+"\nget long : "+location.getLongitude()+
                "\ntime now : "+location.getTime()+"Time Delay : "+ diff);
        locationGot = false;
        stopLocationUpdates();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }




}
