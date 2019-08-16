package petshops.developerpet.com.petshops.Silder_Fragments;


import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import petshops.developerpet.com.petshops.Activities.Add_Adopation;
import petshops.developerpet.com.petshops.Model.Add_Sale;
import petshops.developerpet.com.petshops.data.StaticConfig;
import petshops.developerpet.com.petshops.R;
import petshops.developerpet.com.petshops.TimeAgo.getlongtoago;

public class Purchase_History extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    public Purchase_History(){}

    RecyclerView recyclerview;
    List<Add_Sale> Pets_list = new ArrayList<>();
    MyAdapter recycler;
    Context context;
    private LovelyProgressDialog dialogFindAllFriend;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    String getArgument = "sale";
    //database reference
    private DatabaseReference mDatabase;
    //progress dialog
    private ProgressDialog progressDialog;
    //list to hold all the uploaded images

    Button btnDatePicker, btnDatePicker2;
    String txtDateFrom, txtDateTo;
    private int mYear, mMonth, mDay, mYear2, mMonth2, mDay2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.purchase_history_fragment, container, false);

        try {
            getArgument = getArguments().getString("key_value");//Get pass data with its key value
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            Pets_list.clear();
            recyclerview = (RecyclerView) rootView.findViewById(R.id.recycleListProducts);
            mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
            mSwipeRefreshLayout.setOnRefreshListener(this);
            progressDialog = new ProgressDialog(getContext());
            // displaying progress dialog while fetching images
            progressDialog.setMessage(" Please wait ...");
            progressDialog.show();
            mDatabase = FirebaseDatabase.getInstance().getReference(getArgument);
            fetchData();
            //adding an event listener to fetch values
        } catch (Exception e) {
            Toast.makeText(getContext(), "erorr :  " + e, Toast.LENGTH_SHORT).show();
        }


        btnDatePicker =(Button) rootView.findViewById(R.id.filter_from);
        btnDatePicker2 =(Button) rootView.findViewById(R.id.filter_to);

        btnDatePicker.setOnClickListener(this);
        btnDatePicker2.setOnClickListener(this);


        ((Button) rootView.findViewById(R.id.filter)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // finish test
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        //dismissing the progress dialog
                        progressDialog.dismiss();
                        if (snapshot.exists()){
                            Pets_list.clear();
                            //iterating through all the values in database
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                Add_Sale upload = postSnapshot.getValue(Add_Sale.class);
                                if(StaticConfig.UID.equals(upload.getIdowner())){
                                    try {
                                        // Date Calculation
                                        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                        Date Times = new Date(upload.getTimestamp());
                                        String timeStamp = new SimpleDateFormat("MM/dd/yyyy").format(Times);

                                        Date FromDate = null;
                                        Date ToDate = null;
                                        Date TimeStamp = null;

                                        try {
                                            FromDate = dateFormat.parse(timeStamp);

                                            FromDate = dateFormat.parse(txtDateFrom);
                                            ToDate = dateFormat.parse(txtDateTo);
                                        } catch (Exception e) {
                                            Toast.makeText(getContext(), "erorr 4348 :  " + e, Toast.LENGTH_SHORT).show();
                                        }


                                        // Get msec from each, and subtract.
                                        final long difffrom = FromDate.getTime() - TimeStamp.getTime();
                                        long diffto = TimeStamp.getTime() - ToDate.getTime();
                                        final long diffMinutes = difffrom / (60 * 1000) % 60;
                                        final long diffMinutes2 = diffto / (60 * 1000) % 60;

                                        if (diffMinutes > 0 && diffMinutes2 > 0) {
                                            Pets_list.add(upload);
                                        }
                                    }catch (Exception e){
                                        Toast.makeText(getContext(), "T>>>> :  " + e, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            try {
                                recycler = new MyAdapter(getContext(), Pets_list);
                                RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(getContext());
                                recyclerview.setLayoutManager(layoutmanager);
                                recyclerview.setItemAnimator(new DefaultItemAnimator());
                                recyclerview.setAdapter(recycler);
                                mSwipeRefreshLayout.setRefreshing(false);
                                recycler.notifyDataSetChanged();
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "error recycle :  " + e, Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getContext(), "No Post exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });
            }
        });

        return rootView;
    }

    @Override
    public void onRefresh(){
        fetchData();
    }
    @Override
    public void onClick(View v) {

        if (v == btnDatePicker) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            txtDateFrom = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();}
        if (v == btnDatePicker2) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            // MM/dd/yyyy HH:mm:ss
                            txtDateTo = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }

}

    public void fetchData() {

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //dismissing the progress dialog
                progressDialog.dismiss();
                if (snapshot.exists()){
                    Pets_list.clear();
                    //iterating through all the values in database
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Add_Sale upload = postSnapshot.getValue(Add_Sale.class);
                        if(StaticConfig.UID.equals(upload.getIdowner())){
                            Pets_list.add(upload);
                        }
                    }
                    try {
                        recycler = new MyAdapter(getContext(), Pets_list);
                        RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(getContext());
                        recyclerview.setLayoutManager(layoutmanager);
                        recyclerview.setItemAnimator(new DefaultItemAnimator());
                        recyclerview.setAdapter(recycler);
                        mSwipeRefreshLayout.setRefreshing(false);
                        recycler.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "error recycle :  " + e, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    recycler = new MyAdapter(getContext(), Pets_list);
                    RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(getContext());
                    recyclerview.setLayoutManager(layoutmanager);
                    recyclerview.setItemAnimator(new DefaultItemAnimator());
                    recyclerview.setAdapter(recycler);
                    mSwipeRefreshLayout.setRefreshing(false);
                    recycler.notifyDataSetChanged();
                    Toast.makeText(getContext(), "No Post exist", Toast.LENGTH_SHORT).show();
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
        List<Add_Sale> listPets;
        LovelyProgressDialog dialogWaitDeleting;
        public MyAdapter(Context context, List<Add_Sale> listPets) {
            this.listPets = listPets;
            this.context = context;
            dialogWaitDeleting = new LovelyProgressDialog(context);
        }
        @Override
        public MyAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if(getArgument.equals("sale")){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_pets_layout, parent, false);
            }else{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_pets_layout_adopation, parent, false);
            }
            MyAdapter.MyHolder myHolder = new MyAdapter.MyHolder(view);
            return myHolder;
        }
        public void onBindViewHolder(MyAdapter.MyHolder holder, int position) {
            Add_Sale data = listPets.get(position);

            holder.Edit_delete.setVisibility(View.VISIBLE);

            Glide.with(context).load(data.getPath1()).into(holder.imageView);
            holder.Price.setText("Rs. " + data.getPrice());
            if(getArgument.equals("sale")){
                holder.type.setText("Type :" + data.getType());
                holder.breed.setText("Breed : " + data.getBreed());
            }else{
                holder.duration.setText(data.getDuration());
            }
            holder.timestamp.setText(new getlongtoago().getlongtoago(data.getTimestamp()));

        }
        @Override
        public int getItemCount() {
            return listPets.size();
        }
        class MyHolder extends RecyclerView.ViewHolder {
            ImageView imageView, editpost, deletepost;
            TextView Price, type, breed, timestamp, duration;
            LinearLayout Edit_delete;
            private final Context context;
            public MyHolder(final View itemView) {
                super(itemView);
                context = itemView.getContext();
                final Intent[] intent = new Intent[1];

                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                editpost = (ImageView) itemView.findViewById(R.id.editpost);
                deletepost = (ImageView) itemView.findViewById(R.id.deletepost);
                Edit_delete = (LinearLayout) itemView.findViewById(R.id.edit_delete);

                timestamp = (TextView) itemView.findViewById(R.id.timestamp);
                Price = (TextView) itemView.findViewById(R.id.price);
                if(getArgument.equals("sale")){
                    type = (TextView) itemView.findViewById(R.id.type);
                    breed = (TextView) itemView.findViewById(R.id.breed);
                }else{
                    duration = (TextView) itemView.findViewById(R.id.duration);
                }

                Edit_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String idpost = listPets.get(getAdapterPosition()).getIdPost();
                        Add_Adopation.Data_Adopation_sale = listPets.get(getAdapterPosition());
                        intent[0] = new Intent(context, Add_Adopation.class);
                        intent[0].putExtra("idPost", idpost);
                        intent[0].putExtra("post_type", getArgument);
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
                                        FirebaseDatabase.getInstance().getReference().child(getArgument).child(idpost).removeValue();
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