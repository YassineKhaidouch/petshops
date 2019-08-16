package petshops.developerpet.com.petshops.Silder_Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import petshops.developerpet.com.petshops.DataBase.SharedReferenceHelper;
import petshops.developerpet.com.petshops.Model.Pet;
import petshops.developerpet.com.petshops.R;

public class Categories_History extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    public Categories_History() {
        // Required empty public constructor
    }

    RecyclerView recyclerview;
    List<Pet> pets = new ArrayList<>();
    MyAdapter recycler;
    Context context;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.categories_history_fragment, container, false);

        try {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerview = (RecyclerView) rootView.findViewById(R.id.recycleListProducts);
            mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
            mSwipeRefreshLayout.setOnRefreshListener(this);

            progressDialog = new ProgressDialog(getContext());
            // displaying progress dialog while fetching images
            progressDialog.setMessage(" Please wait ...");
            progressDialog.show();
            mDatabase = FirebaseDatabase.getInstance().getReference("data");
            fetchData();
            //adding an event listener to fetch values
        } catch (Exception e) {
            Toast.makeText(getContext(), "erorr :  " + e, Toast.LENGTH_SHORT).show();
        }


        return rootView;
    }

    @Override
    public void onRefresh(){
        fetchData();
    }
    @Override
    public void onClick(View v) {

    }

    public void fetchData() {

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //dismissing the progress dialog
                progressDialog.dismiss();
                pets.clear();
                if (snapshot.exists()){
                    //iterating through all the values in database

                    try {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Pet petsdata = postSnapshot.getValue(Pet.class);
                        if (SharedReferenceHelper.getInstance(getContext()).getUID().equals(petsdata.getId())){
                            pets.add(petsdata);
                        }
                    }
                        recycler = new MyAdapter(getContext(), pets);
                        RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(getContext());
                        recyclerview.setLayoutManager(layoutmanager);
                        recyclerview.setItemAnimator(new DefaultItemAnimator());
                        recyclerview.setAdapter(recycler);
                        mSwipeRefreshLayout.setRefreshing(false);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "error recycle :  " + e, Toast.LENGTH_SHORT).show();
                    }
                }else{
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

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
        private Context context;
        List<Pet> listPets;
        public MyAdapter(Context context, List<Pet> listPets) {
            this.listPets = listPets;
            this.context = context;
        }
        @Override
        public MyAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_categories_pets_layout , parent, false);
            MyAdapter.MyHolder myHolder = new MyAdapter.MyHolder(view);
            return myHolder;
        }
        public void onBindViewHolder(MyAdapter.MyHolder holder, int position) {
            Pet data = listPets.get(position);
            Glide.with(context).load(data.getPath1()).into(holder.imageView);
            holder.dataCategories.setText("Name : "+data.getName()+"\nType : "+data.getType()+"\nAddress : "+data.getAddress());

        }
        @Override
        public int getItemCount() {
            return listPets.size();
        }
        class MyHolder extends RecyclerView.ViewHolder {

            ImageView imageView, editpost, deletepost;
            TextView dataCategories;
            private final Context context;
            public MyHolder(final View itemView) {
                super(itemView);
                context = itemView.getContext();
                final Intent[] intent = new Intent[1];

                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                dataCategories = (TextView) itemView.findViewById(R.id.dataCategories);
                editpost = (ImageView) itemView.findViewById(R.id.editpost);
                deletepost = (ImageView) itemView.findViewById(R.id.deletepost);

                editpost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String idpost = listPets.get(getAdapterPosition()).getIdPost();
                        String typedata = listPets.get(getAdapterPosition()).getType();
                        Add_page.PetsData = listPets.get(getAdapterPosition());
                        intent[0] = new Intent(context, Add_page.class);
                        intent[0].putExtra("idPost", idpost);
                        intent[0].putExtra("type",typedata  );
                        context.startActivity(intent[0]);
                    }
                });

                deletepost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String idpost = listPets.get(getAdapterPosition()).getIdPost();
                        new AlertDialog.Builder(context)
                                .setTitle("Delete Post ")
                                .setMessage("Are you sure, you want to delete  this post ?")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();

                                        FirebaseDatabase.getInstance().getReference().child("data").child(idpost).removeValue();
                                        if(!listPets.get(getAdapterPosition()).getPath3().equals("empty"))
                                            FirebaseStorage.getInstance().getReferenceFromUrl(listPets.get(getAdapterPosition()).getPath3()).delete();
                                        if(!listPets.get(getAdapterPosition()).getPath1().equals("empty"))
                                            FirebaseStorage.getInstance().getReferenceFromUrl(listPets.get(getAdapterPosition()).getPath1()).delete();
                                        if(!listPets.get(getAdapterPosition()).getPath2().equals("empty"))
                                            FirebaseStorage.getInstance().getReferenceFromUrl(listPets.get(getAdapterPosition()).getPath2()).delete();

                                    }})
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
                    public void onClick(View v) {
                        /*
                        ListViewPets = listPets.get(getAdapterPosition());
                        intent[0] = new Intent(context, View_Details_Pet.class);
                        intent[0].putExtra("id", 0);
                        context.startActivity(intent[0]);
                        */
                    }
                });
            }
        }

    }
}