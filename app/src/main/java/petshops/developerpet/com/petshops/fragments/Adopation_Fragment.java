package petshops.developerpet.com.petshops.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import petshops.developerpet.com.petshops.Activities.Add_Adopation;
import petshops.developerpet.com.petshops.Activities.View_Details_Pet;
import petshops.developerpet.com.petshops.Model.Add_Sale;
import petshops.developerpet.com.petshops.R;
import petshops.developerpet.com.petshops.TimeAgo.getlongtoago;

import static petshops.developerpet.com.petshops.Activities.View_Details_Pet.ListViewPets;

public class Adopation_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public Adopation_Fragment() {}

    RecyclerView recyclerview;
    List<Add_Sale> Pets_list = new ArrayList<>();
    viewAdapter recycler;
    Context context;
    private LovelyProgressDialog dialogFindAllFriend;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //database reference
    private DatabaseReference mDatabase;
    //progress dialog
    private ProgressDialog progressDialog;
    //list to hold all the uploaded images

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_adopation, container, false);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_adopation, container, false);
        Pets_list.clear();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerview = (RecyclerView) rootView.findViewById(R.id.recycleListProducts);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        ((FloatingActionButton) rootView.findViewById(R.id.addAdopation)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Add_Adopation.class));
            }
        });


        mSwipeRefreshLayout.setOnRefreshListener(this);

        try {
            progressDialog = new ProgressDialog(getContext());
            // displaying progress dialog while fetching images
            progressDialog.setMessage(" Please wait ...");
            progressDialog.show();
            mDatabase = FirebaseDatabase.getInstance().getReference("adopation");
            fetchData();
            //adding an event listener to fetch values
        } catch (Exception e) {
            Toast.makeText(getContext(), "erorr :  " + e, Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }

    @Override
    public void onRefresh() {
        fetchData();
    }

    public void fetchData() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //dismissing the progress dialog
                progressDialog.dismiss();
                if (snapshot.exists()) {
                    Pets_list.clear();
                    //iterating through all the values in database
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Add_Sale upload = postSnapshot.getValue(Add_Sale.class);
                        Pets_list.add(upload);
                    }
                    try {
                        recycler = new viewAdapter(getContext(), Pets_list);
                        RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(getContext());
                        recyclerview.setLayoutManager(layoutmanager);
                        recyclerview.setItemAnimator(new DefaultItemAnimator());
                        recyclerview.setAdapter(recycler);
                        recycler.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "erorr recycle :  " + e, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    recycler = new viewAdapter(getContext(), Pets_list);
                    RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(getContext());
                    recyclerview.setLayoutManager(layoutmanager);
                    recyclerview.setItemAnimator(new DefaultItemAnimator());
                    recyclerview.setAdapter(recycler);
                    recycler.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "No Post exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }


    class viewAdapter extends RecyclerView.Adapter<viewAdapter.MyHolder> {
        private Context context;
        List<Add_Sale> listPets;
        LovelyProgressDialog dialogWaitDeleting;
        public viewAdapter(Context context, List<Add_Sale> listPets) {
            this.listPets = listPets;
            this.context = context;
            dialogWaitDeleting = new LovelyProgressDialog(context);
        }
        @Override
        public viewAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_pets_layout_adopation, parent, false);
            viewAdapter.MyHolder myHolder = new viewAdapter.MyHolder(view);
            return myHolder;
        }
        public void onBindViewHolder(viewAdapter.MyHolder holder, int position) {
            Add_Sale data = listPets.get(position);
              /*
            public String idPost;
    public String idowner;
    public String type;
    public String ownername;
    public String breed;
    public String years;
    public String months;
    public String price;
    public String duration;
    public String phone;
    public String descriptions;
    public long timestamp;
    public String path1;
    public String path2;
    public String path3;
         */
            Glide.with(context).load(data.getPath1()).into(holder.imageView);
            holder.Price.setText(
                    "Type : "+data.getType()+
                    "\nBreed : "+data.getBreed()+
                    "\nOwner :"+data.getOwnername()+
                    "\nphone :"+data.getPhone()+
                    "\nDescribe : "+data.getPrice());
            holder.duration.setText(data.getDuration());
            try {
                holder.timestamp.setText(new getlongtoago().getlongtoago(data.getTimestamp()));
            }catch (Exception e){
                holder.timestamp.setText("R"+e);
            }
            /*
            String time = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(data.getTimestamp()));
            String today = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(System.currentTimeMillis()));
            if (today.equals(time)) {
                holder.timestamp.setText((new SimpleDateFormat("HH:mm")).format(new Date(data.getTimestamp()))+"--");
            } else {
                holder.timestamp.setText((new SimpleDateFormat("MMM d")).format(new Date(data.getTimestamp()))+"--");
            }
            */
        }
        @Override
        public int getItemCount() {
            return listPets.size();
        }
        class MyHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView Price, duration, timestamp;
            private final Context context;
            public MyHolder(final View itemView) {
                super(itemView);
                context = itemView.getContext();
                final Intent[] intent = new Intent[1];

                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                duration = (TextView) itemView.findViewById(R.id.duration);
                timestamp = (TextView) itemView.findViewById(R.id.timestamp);
                Price = (TextView) itemView.findViewById(R.id.price);

                /*
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final String productkey = listProducts.get(getAdapterPosition()).getPushkey();
                        String producttitle = listProducts.get(getAdapterPosition()).getTitle();
                        new AlertDialog.Builder(context)
                                .setTitle("Delete Product ")
                                .setMessage("Are you sure want to delete "+producttitle+ " ?")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        FirebaseDatabase.getInstance().getReference().child("Products").child(productkey).removeValue();

                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();

                        return false;
                    }
                });
                */
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //String postkey = listPets.get(getAdapterPosition()).getIdPost();

                        ListViewPets = listPets.get(getAdapterPosition());
                        intent[0] = new Intent(context, View_Details_Pet.class);
                        intent[0].putExtra("id",1);
                        context.startActivity(intent[0]);
                    }
                });
            }
        }
    }
}
