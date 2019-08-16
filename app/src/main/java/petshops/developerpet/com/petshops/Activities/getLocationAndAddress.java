package petshops.developerpet.com.petshops.Activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import petshops.developerpet.com.petshops.R;


public class getLocationAndAddress extends AppCompatActivity {


    TextView txtLocationAddress;
    SupportMapFragment mapFragment;
    GoogleMap map;
    ArrayList markerPoints= new ArrayList();
    LatLng center;
    RelativeLayout cardView;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    FloatingActionButton getLocation;
    public static String STR_EXTRA_ACTION_LOCATION = "location";

    double longitude = 0;
    double latitude = 0;
    String straddress;

    Geocoder geocoder;
    List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_location_address);

        txtLocationAddress = (TextView) findViewById(R.id.txtLocationAddress);
        txtLocationAddress.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txtLocationAddress.setSingleLine(true);
        txtLocationAddress.setMarqueeRepeatLimit(-1);
        txtLocationAddress.setSelected(true);

        cardView = (RelativeLayout) findViewById(R.id.cardView);
        getLocation = (FloatingActionButton) findViewById(R.id.getlocation);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.getUiSettings().setZoomControlsEnabled(true);
                LatLng latLng = new LatLng(20.5937, 78.9629);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                //initCameraIdle();

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                        if (markerPoints.size() > 0) {
                            markerPoints.clear();
                            map.clear();
                        }
                        getAddressFromLocation(latLng.latitude, latLng.longitude);
                        latitude = latLng.latitude;
                        longitude = latLng.longitude;
                        markerPoints.add(latLng);
                        MarkerOptions options = new MarkerOptions();
                        options.position(latLng);
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        map.addMarker(options);
                }
            });}
        });


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(getLocationAndAddress.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    printToast("Google Play Service Repair");
                } catch (GooglePlayServicesNotAvailableException e) {
                    printToast("Google Play Service Not Available");
                }
            }
        });

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((latitude == 0 || longitude == 0) || (latitude == 0 && longitude == 0) ){
                    LayoutInflater myInflater = LayoutInflater.from(getLocationAndAddress.this);
                    View view = myInflater.inflate(R.layout.your_custom_layout, null);
                    TextView textshow = (TextView) view.findViewById(R.id.textshow);
                    textshow.setText("Type on Map where your  "+getIntent().getStringExtra("typedata")+" exist  ... ");
                    Toast mytoast = new Toast(getLocationAndAddress.this);
                    mytoast.setView(view);
                    mytoast.setDuration(Toast.LENGTH_LONG);
                    mytoast.show();
                    return;
                }
                Intent data = new Intent();
                data.putExtra("longitude", longitude);
                data.putExtra("latitude", latitude);
                data.putExtra("address",straddress);
                setResult(RESULT_OK, data);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                finish();

            }
        });

    }

    private void initCameraIdle() {
        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                center = map.getCameraPosition().target;
                latitude = center.latitude;
                longitude = center.longitude;
                getAddressFromLocation(latitude, longitude);
            }
        });
    }

    private void getAddressFromLocation(double latitude, double longitude) {

        try {
            geocoder = new Geocoder(this, Locale.ENGLISH);
            addresses = geocoder.getFromLocation(latitude ,longitude , 1);
            StringBuilder str = new StringBuilder();
            if (geocoder.isPresent()) {
                Toast.makeText(this, "waite ... geocoder present", Toast.LENGTH_SHORT).show();
                Address returnAddress = addresses.get(0);

                String cc = returnAddress.getFeatureName();
                String localityString = returnAddress.getLocality();
                String city = returnAddress.getCountryName();

                str.append(cc + ", ");
                str.append(localityString + ", ");
                str.append(city + "");


                Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

                straddress = str.toString();
                txtLocationAddress.setText(str.toString());

            } else {
             //   Toast.makeText(this, "geocoder not present", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
           // Toast.makeText(this, "error in geocoder  "+e, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                if (!place.getAddress().toString().contains(place.getName())) {
                    txtLocationAddress.setText(place.getName() + ", " + place.getAddress());
                    straddress = place.getName() + ", " + place.getAddress();
                } else {
                    straddress = (String) place.getAddress();
                    txtLocationAddress.setText(place.getAddress());
                }
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16);
                map.animateCamera(cameraUpdate);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                printToast("Error in retrieving place info");
            }
        }
    }

    private void printToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
