package petshops.developerpet.com.petshops.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.ArrayList;
import java.util.List;

import petshops.developerpet.com.petshops.Activities.View_Details_Pet;
import petshops.developerpet.com.petshops.Model.Add_Sale;
import petshops.developerpet.com.petshops.R;
import petshops.developerpet.com.petshops.TimeAgo.getlongtoago;

import static petshops.developerpet.com.petshops.Activities.View_Details_Pet.ListViewPets;

public class Purchase_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    RecyclerView recyclerview;
    List<Add_Sale> Pets_list = new ArrayList<>();
    RecyclerviewAdapter recycler;
    Context context;
    private LovelyProgressDialog dialogFindAllFriend;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //database reference
    private DatabaseReference mDatabase;
    //progress dialog
    private ProgressDialog progressDialog;
    //list to hold all the uploaded images
    public Purchase_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_purchase_, container, false);
        try {

            recyclerview = (RecyclerView) rootView.findViewById(R.id.recycleListProducts);
            mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
            mSwipeRefreshLayout.setOnRefreshListener(this);

            progressDialog = new ProgressDialog(getContext());
            // displaying progress dialog while fetching images
            progressDialog.setMessage(" Please wait ...");
            progressDialog.show();
            mDatabase = FirebaseDatabase.getInstance().getReference("sale");
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
                        recycler = new RecyclerviewAdapter(getContext(), Pets_list);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        recyclerview.setLayoutManager(linearLayoutManager);
                        recyclerview.setItemAnimator(new DefaultItemAnimator());
                        recyclerview.setAdapter(recycler);
                        recycler.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "erorr recycle :  " + e, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    recycler = new RecyclerviewAdapter(getContext(), Pets_list);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    recyclerview.setLayoutManager(linearLayoutManager);
                    recyclerview.setItemAnimator(new DefaultItemAnimator());
                    recyclerview.setAdapter(recycler);
                    recycler.notifyDataSetChanged();
                    Toast.makeText(getContext(), "No Post exist", Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

    }



    public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyHolder> {
        private Context context;
        List<Add_Sale> listPets;
        LovelyProgressDialog dialogWaitDeleting;
        public RecyclerviewAdapter(Context context, List<Add_Sale> listPets) {
            this.listPets = listPets;
            this.context = context;
            dialogWaitDeleting = new LovelyProgressDialog(context);
        }
        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_pets_layout, parent, false);
            MyHolder myHolder = new MyHolder(view);
            return myHolder;
        }


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void onBindViewHolder(MyHolder holder, int position) {
            Add_Sale data = listPets.get(position);
            Glide.with(context).load(data.getPath1()).into(holder.imageView);
            holder.Price.setText("Price : " + data.getPrice());
            holder.type.setText("Type :" + data.getType());
            String smallDescribe = "";
            try {

                for (int i = 0; i < data.getDescriptions().length(); i++) {
                    if(i < 30){
                        smallDescribe += data.getDescriptions().charAt(i);
                    }
                }
            }catch (Exception e){
                smallDescribe = "EE@#@1";
            }
            holder.breed.setText("Breed : " + data.getBreed()+
                    "\nAge : "+data.getYears()+ " years "+data.getMonths()+" Months"+
                    "\nOwner : "+ data.getOwnername()+
                    "\nPhone : "+data.getPhone()+
                    "\nDescribe : "+smallDescribe+" ..."
            );
            holder.timestamp.setText(new getlongtoago().getlongtoago(data.getTimestamp()));

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
            TextView Price, type, breed, timestamp;
            private final Context context;
            public MyHolder(final View itemView) {
                super(itemView);
                context = itemView.getContext();
                final Intent[] intent = new Intent[1];

                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                type = (TextView) itemView.findViewById(R.id.type);
                breed = (TextView) itemView.findViewById(R.id.breed);
                timestamp = (TextView) itemView.findViewById(R.id.timestamp);
                Price = (TextView) itemView.findViewById(R.id.price);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //String postkey = listPets.get(getAdapterPosition()).getIdPost();

                        ListViewPets = listPets.get(getAdapterPosition());
                        intent[0] = new Intent(context, View_Details_Pet.class);
                        intent[0].putExtra("id", 0);
                        context.startActivity(intent[0]);

                    }
                });
            }
        }

    }
}
