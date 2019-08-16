package petshops.developerpet.com.petshops.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import petshops.developerpet.com.petshops.Activities.Add_page;
import petshops.developerpet.com.petshops.Activities.CatigoriesPage;
import petshops.developerpet.com.petshops.Activities.View_Details_Pet;
import petshops.developerpet.com.petshops.DataBase.SharedReferenceHelper;
import petshops.developerpet.com.petshops.Model.Pet;
import petshops.developerpet.com.petshops.R;
import petshops.developerpet.com.petshops.ShowDetails;
import petshops.developerpet.com.petshops.Silder_Fragments.Categories_History;

public class ListPets_fragment extends Fragment{// implements SwipeRefreshLayout.OnRefreshListener {
    public  double Latitude = 0 , Longitude = 0;
    public ListPets_fragment() {
    }

    public  static  RecyclerView recyclerview;
    static List<Pet> pets = new ArrayList<>();
    public static MyAdapter recycler;
    public static RecyclerView.LayoutManager layoutmanager;
    Context context;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_pets_fragment, container, false);
        ((FloatingActionButton) rootView.findViewById(R.id.addshop)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CatigoriesPage.class));
                getActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        try {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerview = (RecyclerView) rootView.findViewById(R.id.recycleListProducts);
            mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
          //  mSwipeRefreshLayout.setOnRefreshListener(this);
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Please wait ...");

            recycler = new MyAdapter(getContext(), pets);
            layoutmanager = new LinearLayoutManager(getContext());
            recyclerview.setLayoutManager(layoutmanager);
            recyclerview.setItemAnimator(new DefaultItemAnimator());
            recyclerview.setAdapter(recycler);
            //  mSwipeRefreshLayout.setRefreshing(false);
            recycler.notifyDataSetChanged();
            mDatabase = FirebaseDatabase.getInstance().getReference("data");
           // fetchData();
            //adding an event listener to fetch values


        } catch (Exception e) {
            Toast.makeText(getContext(), "erorr :  " + e, Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }
/*
    @Override
    public void onRefresh() {

        if(Latitude == 0 && Longitude == 0){
          //  turnOnGpsMsg();
        }else{
         //   fetchData();
        }
    }
*/
    void msg(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }

    private float getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] distance = new float[2];
        Location.distanceBetween(lat1, lon1, lat2, lon2, distance);
        return distance[0] / 1000;

    }
    public void fetchData() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // dismissing the progress dialog
                // progressDialog.dismiss();
                pets.clear();
                if (snapshot.exists()) {
                    //iterating through all the values in database
                    try {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Pet petsdata = postSnapshot.getValue(Pet.class);
                            //if (SharedReferenceHelper.getInstance(getContext()).getUID().equals(petsdata.getId())) {
                           // if(getDistance(petsdata.getLatitude(), petsdata.getLongitude(), Latitude, Longitude) >)
                           // pets.add(petsdata);

                        }
                        recycler = new MyAdapter(getContext(), pets);
                        RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(getContext());
                        recyclerview.setLayoutManager(layoutmanager);
                        recyclerview.setItemAnimator(new DefaultItemAnimator());
                        recyclerview.setAdapter(recycler);
                        mSwipeRefreshLayout.setRefreshing(false);
                    } catch (Exception e){
                        Toast.makeText(getContext(), "error recycle :  " + e, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "No Post exist", Toast.LENGTH_SHORT).show();
                    recycler = new MyAdapter(getContext(), pets);
                    RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(getContext());
                    recyclerview.setLayoutManager(layoutmanager);
                    recyclerview.setItemAnimator(new DefaultItemAnimator());
                    recyclerview.setAdapter(recycler);
                    mSwipeRefreshLayout.setRefreshing(false);
                    recycler.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

   public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
        Context context;
        List<Pet> listPets;
        public MyAdapter(Context context, List<Pet> listPets) {
            this.listPets = listPets;
            this.context = context;
        }

        @Override
        public MyAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_homepage_categories_pets_layout, parent, false);
            MyAdapter.MyHolder myHolder = new MyAdapter.MyHolder(view);
            return myHolder;
        }

        public void onBindViewHolder(MyAdapter.MyHolder holder, int position) {
            Pet data = listPets.get(position);
            Glide.with(context).load(data.getPath1()).into(holder.imageView);
            holder.dataCategories.setText("Name : " + data.getName() +
                    "\nType : " + data.getType() +
                    "\nOpen "+data.getOpenfrom()+" "+data.getOpento()+
                    "\nPhone : "+data.getContactnumber()+
                    "\nAddress : " + data.getAddress()+
                    "\n");
            holder.ratingBar.setRating((float) data.getRate());

        }

        @Override
        public int getItemCount() {return listPets.size();}
        class MyHolder extends RecyclerView.ViewHolder {
            ImageView imageView ;
            TextView dataCategories;
            RatingBar ratingBar;

            private final Context context;
            public MyHolder(final View itemView) {
                super(itemView);
                context = itemView.getContext();
                final Intent[] intent = new Intent[1];
                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                dataCategories = (TextView) itemView.findViewById(R.id.dataCategories);
                ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         Pet pet= listPets.get(getAdapterPosition());
                        intent[0] = new Intent(context, ShowDetails.class);
                        intent[0].putExtra("idpost", pet.getIdPost());
                        intent[0].putExtra("iduser", pet.getId());
                        intent[0].putExtra("distance", pet.getDistance());
                        context.startActivity(intent[0]);

                    }
                });
            }
        }

    }
}