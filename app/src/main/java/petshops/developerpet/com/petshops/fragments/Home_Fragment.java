package petshops.developerpet.com.petshops.fragments;

/**
 * Created by root on 1/25/18.
 */

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import petshops.developerpet.com.petshops.CustomInfoWindowGoogleMap;
import petshops.developerpet.com.petshops.MainActivity;
import petshops.developerpet.com.petshops.Model.Pet;
import petshops.developerpet.com.petshops.data.StaticConfig;
import petshops.developerpet.com.petshops.Activities.CatigoriesPage;
import petshops.developerpet.com.petshops.Activities.Add_page;
import petshops.developerpet.com.petshops.R;
import petshops.developerpet.com.petshops.ShowDetails;

import static android.content.Context.LOCATION_SERVICE;


public class Home_Fragment extends Fragment implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener ,
        View.OnClickListener,
        OnMapReadyCallback {



    Long StartTime , StopTime ;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;

    ProgressDialog progressDialog;;
    FloatingActionButton FAB;

    public double x =0;
    public double y =0;
    String myaddress = "";
    Geocoder geocoder;
    List<Address> addresses;
    LocationManager locationManager;

    DatabaseReference mDatabase;
    FirebaseDatabase mInstance;

    viewAdapter recycler;
    RecyclerView recyclerview;
    List<Pet> pets = new ArrayList<>();
    List<Pet> pets_list = new ArrayList<>();
    List<Pet> petsRcyc = new ArrayList<>();

    EditText Search;
    int p = StaticConfig.Position;
    public static LinearLayout LinearLayout_Search;

    ImageView shops;
    ImageView clinics;
    ImageView spas;
    ImageView grooming;
    ImageView others;
    ImageView BarSilderView;
    LinearLayout Tabs_Bar;

    boolean Tabs_Bar_Show = true;
    String[] TypesCatigories = {"shops", "clinics", "spas", "grooming", "others"};

    int[] TypeMarker = {
            R.drawable.markershope,
            R.drawable.markerclinics,
            R.drawable.markerspas,
            R.drawable.markergromming,
            R.drawable.markerother
    };
    public Home_Fragment() {}

/*
onLocationChanged
        x = location.getLatitude();
            y = location.getLongitude();
            getmylocation();
            progressDialog.dismiss();
            StopLocationUpdate();

 */

    private static final long INTERVAL = 1000 * 2;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {}
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onLocationChanged(Location location) {
       // StopTime = System.currentTimeMillis();
      //  long diff = StopTime - StartTime;
       // diff = TimeUnit.MILLISECONDS.toSeconds(diff);
       // msg("delay Time : " + diff + " seconds");
        x = location.getLatitude();
        y = location.getLongitude();
        progressDialog.dismiss();
        stopLocationUpdates();
        getmylocation();
        FAB.setEnabled(true);
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    void GetLocation() {
        try {
            createLocationRequest();
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        //    StartTime = System.currentTimeMillis();
        }catch(Exception e){}
    }


    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.home_fragment, container, false);
        mInstance = FirebaseDatabase.getInstance();
        mDatabase = mInstance.getReference("data");

        // progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        shops = (ImageView) rootView.findViewById(R.id.shops);
        clinics = (ImageView) rootView.findViewById(R.id.clinics);
        spas = (ImageView) rootView.findViewById(R.id.spas);
        grooming = (ImageView) rootView.findViewById(R.id.grooming);
        others = (ImageView) rootView.findViewById(R.id.others);
        BarSilderView = (ImageView) rootView.findViewById(R.id.sildbar);
        Tabs_Bar = (LinearLayout) rootView.findViewById(R.id.tabs);

        BarSilderView.setImageResource(R.drawable.sildbarbuttom_hide);
        BarSilderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tabs_Bar_Show) {
                    BarSilderView.setImageResource(R.drawable.sildbarbuttom);
                    Tabs_Bar.setVisibility(View.GONE);
                    Tabs_Bar_Show = false;
                } else {
                    BarSilderView.setImageResource(R.drawable.sildbarbuttom_hide);
                    Tabs_Bar.setVisibility(View.VISIBLE);
                    Tabs_Bar_Show = true;
                }
            }
        });

        recyclerview = (RecyclerView) rootView.findViewById(R.id.recycleListPotsStore);
        Search = (EditText) rootView.findViewById(R.id.search);
        LinearLayout_Search = (LinearLayout) rootView.findViewById(R.id.linearlayouttop);

        FloatingActionButton AddShop = (FloatingActionButton) rootView.findViewById(R.id.addshop);
        AddShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CatigoriesPage.class));
                getActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
        mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);


        FAB = (FloatingActionButton) rootView.findViewById(R.id.mylocation);

        //  Check gps
        locationManager = (LocationManager) (getContext()).getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // showGPSDisabledAlertToUser();
            // showSettingsAlert();
            SetUsersOnMap("");
            //FAB.setVisibility(View.GONE);
        } else {
            if(x == 0 && y == 0){
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Getting Location...");
                progressDialog.show();
                GetLocation();
            }else{
                getmylocation();
            }
        }


        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FAB.setEnabled(false);
                GetLocation();
/*
                LatLng latLng = new LatLng(x,y);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng)
                        .snippet(myaddress)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.markerme01));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
                mGoogleMap.animateCamera(cameraUpdate);
                mGoogleMap.addMarker(markerOptions);
*/
            }
        });



        Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String text = Search.getText().toString();
                if (text.length() > 0) {
                    Query query = mDatabase.orderByChild("name").startAt(text).endAt("~");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            petsRcyc.clear();
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    try {
                                        Pet petsdata = postSnapshot.getValue(Pet.class);
                                        if (petsdata.type.equals(TypesCatigories[p])) {
                                            petsRcyc.add(petsdata);}
                                    } catch (Exception e) {
                                        msg("error : " + e);}
                                }
                                recyclerview.setVisibility(View.VISIBLE);
                            } else {
                                msg("no items found");
                            }

                            recycler = new viewAdapter(getContext(), petsRcyc);
                            RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                            recyclerview.setLayoutManager(layoutmanager);
                            recyclerview.setItemAnimator(new DefaultItemAnimator());
                            recyclerview.setAdapter(recycler);
                            recycler.setClickListener(new ItemClickListener() {
                                @Override
                                public void onClickItems(View view, int position) {
                                    String idpost = petsRcyc.get(position).getIdPost();
                                    SetUsersOnMap(idpost);
                                    recyclerview.setVisibility(View.GONE);
                                }
                            });}
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}});
                } else {
                    recyclerview.setVisibility(View.GONE);
                    petsRcyc.clear();
                    recycler = new viewAdapter(getContext(), petsRcyc);
                    RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(getContext());
                    recyclerview.setLayoutManager(layoutmanager);
                    recyclerview.setItemAnimator(new DefaultItemAnimator());
                    recyclerview.setAdapter(recycler);
                }
            }
        });


        shops.setImageResource(R.mipmap.shops_s);
        clinics.setImageResource(R.mipmap.clinics);
        spas.setImageResource(R.mipmap.spas);
        grooming.setImageResource(R.mipmap.grooming);
        others.setImageResource(R.mipmap.others);

        /*
        int ya = ((getScreenWidth()) - 100 * 5) / 6;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100, 120);
        lp.setMarginStart(ya / 2);
        lp.setMarginEnd(ya / 2);
        shops.setLayoutParams(lp);
        clinics.setLayoutParams(lp);
        spas.setLayoutParams(lp);
        grooming.setLayoutParams(lp);
        others.setLayoutParams(lp);
        */

        shops.setOnClickListener(this);
        clinics.setOnClickListener(this);
        spas.setOnClickListener(this);
        grooming.setOnClickListener(this);
        others.setOnClickListener(this);
        SetImages(p);

        return rootView;
    }


    void msg(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }

    private float getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] distance = new float[2];
        Location.distanceBetween(lat1, lon1, lat2, lon2, distance);
        return distance[0] / 1000;

    }

    public int getScreenWidth() {
        return  Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    void SetImages(int i) {
        shops.setImageResource(R.mipmap.shops);
        clinics.setImageResource(R.mipmap.clinics);
        spas.setImageResource(R.mipmap.spas);
        grooming.setImageResource(R.mipmap.grooming);
        others.setImageResource(R.mipmap.others);

        String[] titleshome = {"Shops", "Clinics", "Spas", "Grooming", "Others"};
        MainActivity.toolbar.setTitle("Home - " + titleshome[i]);


        switch (i) {
            case 0:
                shops.setImageResource(R.mipmap.shops_s);
                break;
            case 1:
                clinics.setImageResource(R.mipmap.clinics_s);
                break;
            case 2:
                spas.setImageResource(R.mipmap.spas_s);
                break;
            case 3:
                grooming.setImageResource(R.mipmap.grooming_s);
                break;
            case 4:
                others.setImageResource(R.mipmap.others_s);
                break;
        }
    }


    void getmylocation(){
        try {
            geocoder = new Geocoder(getContext(), Locale.ENGLISH);
            addresses = geocoder.getFromLocation(x, y, 1);
            StringBuilder str = new StringBuilder();
            if (geocoder.isPresent()) {
                Address returnAddress = addresses.get(0);
                String cc = returnAddress.getFeatureName();
                String localityString = returnAddress.getLocality();
                String city = returnAddress.getCountryName();
                str.append(cc + ", ");
                str.append(localityString + ", ");
                str.append(city + "");
                myaddress = str.toString();
            } else {}
        } catch (IOException e) {}
        LatLng latLng = new LatLng(x, y);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng)
                .snippet(myaddress)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.markerme01));
        mGoogleMap.clear();
        SetUsersOnMap("");
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        mGoogleMap.animateCamera(cameraUpdate);
        mGoogleMap.addMarker(markerOptions);
    }

    public void SetUsersOnMap(final String IdPost) {
        try {
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        try {
                            if (dataSnapshot.exists()) {
                                pets.clear();
                                pets_list.clear();
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    try {
                                        Pet petsdata = postSnapshot.getValue(Pet.class);

                                        if (petsdata.type.equals(TypesCatigories[p])) {
                                            pets.add(petsdata);
                                            if(getDistance(petsdata.getLatitude(), petsdata.getLongitude(), x, y) <= 50){
                                                pets_list.add(petsdata);
                                            }
                                        }
                                    } catch (Exception e) {
                                        msg("error : " + e);
                                    }
                                }
                                try {
                                    // ListPets_fragment listPets_fragment = new ListPets_fragment();
                                    //setListPets(pets);
                                    //ListPets_fragment.pets = pets_list;
                                    ListPets_fragment.recycler = new ListPets_fragment.MyAdapter(getContext(), pets_list);
                                    ListPets_fragment.layoutmanager = new LinearLayoutManager(getContext());
                                    ListPets_fragment.recyclerview.setLayoutManager( ListPets_fragment.layoutmanager );
                                    ListPets_fragment.recyclerview.setItemAnimator(new DefaultItemAnimator());
                                    ListPets_fragment.recyclerview.setAdapter(ListPets_fragment.recycler);
                                    ListPets_fragment.recycler.notifyDataSetChanged();

                                } catch (Exception e) {
                                    msg("error when call list pets functions : " + e);
                                }


                                MarkerOptions markerOptions = new MarkerOptions();
                                int i = 0;

                                for (Pet pet : pets) {
                                    LatLng latLng = new LatLng(pet.getLatitude(), pet.getLongitude());
                                    if (IdPost.equals(pet.getIdPost())) {
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(TypeMarker[p]))
                                                .position(latLng)
                                                .snippet(String.valueOf(i));
                                        Marker m = mGoogleMap.addMarker(markerOptions);
                                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
                                        mGoogleMap.animateCamera(cameraUpdate);
                                        pets.get(i).setDistance(getDistance(pet.getLatitude(), pet.getLongitude(), x, y));
                                        m.showInfoWindow();
                                        i++;
                                    } else {
                                        markerOptions
                                                .icon(BitmapDescriptorFactory.fromResource(TypeMarker[p]))
                                                .position(latLng)
                                                .snippet(String.valueOf(i));
                                        pets.get(i).setDistance(getDistance(pet.getLatitude(), pet.getLongitude(), x, y));
                                        i++;
                                        mGoogleMap.addMarker(markerOptions);
                                    }

                                }
                                CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getContext(),pets);
                                mGoogleMap.setInfoWindowAdapter(customInfoWindow);
                            } else {msg("no items found");}

                        }catch (Exception e){printToast("Error 1231  : "+e);}}}
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }catch (Exception e){printToast("Error 234 : "+e);}}



    @Override
    public void onClick(View v) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.shops:
                p = 0;
                SetImages(p);
                ft.detach(Home_Fragment.this).attach(Home_Fragment.this).commit();
                StaticConfig.Position = p;
                break;
            case R.id.clinics:
                p = 1;
                SetImages(p);
                ft.detach(Home_Fragment.this).attach(Home_Fragment.this).commit();
                StaticConfig.Position = p;
                break;
            case R.id.spas:
                p = 2;
                SetImages(p);
                ft.detach(Home_Fragment.this).attach(Home_Fragment.this).commit();
                StaticConfig.Position = p;
                break;
            case R.id.grooming:
                p = 3;
                SetImages(p);
                ft.detach(Home_Fragment.this).attach(Home_Fragment.this).commit();
                StaticConfig.Position = p;
                break;
            case R.id.others:
                p = 4;
                SetImages(p);
                ft.detach(Home_Fragment.this).attach(Home_Fragment.this).commit();
                StaticConfig.Position = p;
                break;
            case R.id.addData:
                startActivity(new Intent(getContext(), Add_page.class).putExtra("type", TypesCatigories[p]));
                break;
        }
        if(v.getId() == R.id.grooming ||
                v.getId() == R.id.others ||
                v.getId() == R.id.spas ||
                v.getId() == R.id.clinics ||
                v.getId() == R.id.shops  ){
            mGoogleMap.clear();
            recyclerview.setVisibility(View.GONE);
            petsRcyc.clear();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //mGoogleMap.setTrafficEnabled(true);
       /*
        CircleOptions options = new CircleOptions();
        //options.center(location);
        //Radius in meters
        options.radius(10);
        options.fillColor( getResources()
                .getColor(R.color.blue3 ) );
        options.strokeColor( getResources()
                .getColor( R.color.blue2) );
        options.strokeWidth(10);
        mGoogleMap.addCircle(options);
        */

       //mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                try {
                    final int i = Integer.parseInt(marker.getSnippet());
                    startActivity(new Intent(getContext(), ShowDetails.class)
                            .putExtra("distance", new DecimalFormat("###.#").format(pets.get(i).getDistance()))
                            .putExtra("iduser",pets.get(i).getId())
                            .putExtra("idpost",pets.get(i).getIdPost())
                    );
                }catch(Exception e){}
            }
        });


    }


    private void printToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

class viewAdapter extends RecyclerView.Adapter<viewAdapter.MyHolder> {
    private Context context;
    List<Pet> pet;
    private ItemClickListener clickListener;
    public viewAdapter(Context context, List<Pet> pet) {this.pet = pet;this.context = context;}
    public void setClickListener(ItemClickListener itemClickListener){this.clickListener = itemClickListener;}
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_item_name_char, parent, false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    public void onBindViewHolder(MyHolder holder, int position) {
        Pet data = pet.get(position);
        String[] TypeCatigory = {"shop", "clinic", "spas", "grooming", "other"};
        int s = StaticConfig.Position;
        String[] Char = {"S","C","S","G","O"};
        int Drawbles[] = {
                R.drawable.circle_background_shops,
                R.drawable.circle_background_clinics,
                R.drawable.circle_background_spas,
                R.drawable.circle_background_gromming,
                R.drawable.circle_background_others
        };
        holder.name.setText(data.name+"\nPet "+TypeCatigory[s]);
        try {
            //holder.character.setText((data.name).toString().charAt(1));
            holder.character.setText(Char[s]);
            holder.character.setBackgroundResource(Drawbles[s]);
        } catch (Exception e) {holder.character.setText("E" + e);}
    }
    @Override
    public int getItemCount() {
        return pet.size();
    }
    class MyHolder extends RecyclerView.ViewHolder {
        TextView name, character;
        private final Context context;
        public MyHolder(final View itemView) {
            super(itemView);
            context = itemView.getContext();
            final Intent[] intent = new Intent[1];
            name = (TextView) itemView.findViewById(R.id.txtName);
            character = (TextView) itemView.findViewById(R.id.txtChar);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (clickListener != null) clickListener.onClickItems(v, getAdapterPosition());
                        //String idpost = pet.get(getAdapterPosition()).getIdPost();
                        //msg(idpost);
                    } catch (Exception e) {
                        msg("eror 3345 : " + e);
                    }
                }

            });
        }
        void msg(String text){
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

}
}