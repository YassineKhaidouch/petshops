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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import petshops.developerpet.com.petshops.Activities.Add_New_Product;
import petshops.developerpet.com.petshops.Model.MyBasket;
import petshops.developerpet.com.petshops.Model.Product;
import petshops.developerpet.com.petshops.R;
import petshops.developerpet.com.petshops.TimeAgo.getlongtoago;
import petshops.developerpet.com.petshops.data.StaticConfig;


public class Products_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public Products_Fragment() {
    }

    RecyclerView recyclerview;
    List<Product> products = new ArrayList<>();
    RecyclerviewAdapter recycler;
    Context context;
    private LovelyProgressDialog dialogFindAllFriend;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //database reference
    private DatabaseReference mDatabase;
    //progress dialog
    private ProgressDialog progressDialog;
    //list to hold all the uploaded images

    View rootView;

    boolean isVisibled = true;

    public  Button GoToPay;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_products, container, false);
        GoToPay = (Button) rootView.findViewById(R.id.gotopay);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerview = (RecyclerView) rootView.findViewById(R.id.recycleListProducts);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        ((FloatingActionButton) rootView.findViewById(R.id.addNewProduct)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Add_New_Product.class));
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(this);
        try {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Getting Products...");
            progressDialog.show();
            mDatabase = FirebaseDatabase.getInstance().getReference("products");
            fetchData();
        } catch (Exception e) {
            Toast.makeText(getContext(), "error :  " + e, Toast.LENGTH_SHORT).show();
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
                    products.clear();
                    //iterating through all the values in database
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Product upload = postSnapshot.getValue(Product.class);
                        products.add(upload);
                    }
                    try {
                        recycler = new RecyclerviewAdapter(getContext(), products);
                        RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(getContext());
                        recyclerview.setLayoutManager(layoutmanager);
                        recyclerview.setItemAnimator(new DefaultItemAnimator());
                        recyclerview.setAdapter(recycler);
                        recycler.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "error recycle :  " + e, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "No Products exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }


    class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyHolder> {
        private Context context;
        List<Product> products;
        LovelyProgressDialog dialogWaitDeleting;
        List<MyBasket> mybasket = new ArrayList<>();
        public RecyclerviewAdapter(Context context, List<Product> listProducts) {
            this.products = listProducts;
            this.context = context;
            dialogWaitDeleting = new LovelyProgressDialog(context);
            mybasket.clear();
        }
        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_pets_products, parent, false);
            MyHolder myHolder = new MyHolder(view);
            return myHolder;
        }
        public void onBindViewHolder(MyHolder holder, int position) {
            Product data = products.get(position);
            Glide.with(context).load(data.imagePath).into(holder.ContentProduct);
            holder.Title.setText("title : " + data.title);
            holder.Price.setText(data.price + " " + data.currency);
            holder.Category.setText("category: " + data.category);
            holder.CurrentDate.setText((new getlongtoago().getlongtoago(data.timestamp)));
            holder.ProductsId.setText(data.price2 + " " + data.currency);
            holder.Description.setText("description :" + data.description);
        }
        @Override
        public int getItemCount() {
            return products.size();
        }
        class MyHolder extends RecyclerView.ViewHolder {
            ImageView ContentProduct, Basket;
            TextView Title, Category, CurrentDate, Description, Price, ProductsId;
            private final Context context;
            LinearLayout BasketDialog;
            ImageView removebasket, addbasket, cancelbasket, savebasket, deletebasket;
            EditText numberunity;
            TextView quantitybasket;
            public MyHolder(final View itemView) {
                super(itemView);
                context = itemView.getContext();
                final Intent[] intent = new Intent[1];

                ContentProduct = (ImageView) itemView.findViewById(R.id.contentProduct);
                Title = (TextView) itemView.findViewById(R.id.title);
                Category = (TextView) itemView.findViewById(R.id.category);
                CurrentDate = (TextView) itemView.findViewById(R.id.postDate);
                Description = (TextView) itemView.findViewById(R.id.description);
                Price = (TextView) itemView.findViewById(R.id.price);
                ProductsId = (TextView) itemView.findViewById(R.id.productsId);
                // the basket
                BasketDialog = (LinearLayout) itemView.findViewById(R.id.basketdialog);
                removebasket = (ImageView) itemView.findViewById(R.id.removebasket);
                addbasket = (ImageView) itemView.findViewById(R.id.addbasket);
                cancelbasket = (ImageView) itemView.findViewById(R.id.cancelbasket);
                savebasket = (ImageView) itemView.findViewById(R.id.savebasket);
                deletebasket = (ImageView) itemView.findViewById(R.id.deletebasket);

                quantitybasket = (TextView) itemView.findViewById(R.id.quantitybasket);
                numberunity = (EditText) itemView.findViewById(R.id.numberunity);

                Basket = (ImageView) itemView.findViewById(R.id.basket);
                Basket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  itemView.setClipToOutline(false);
                        Basket.setVisibility(View.GONE);
                        BasketDialog.setVisibility(View.VISIBLE);
                        quantitybasket.setVisibility(View.GONE);

                    }
                });

                cancelbasket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Basket.setVisibility(View.VISIBLE);
                        BasketDialog.setVisibility(View.GONE);
                        quantitybasket.setVisibility(View.VISIBLE);
                    }
                });

                addbasket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String x = numberunity.getText().toString();
                        if (!TextUtils.isEmpty(x)) {
                            try {
                                int NumberUnity = Integer.parseInt(x);
                                NumberUnity++;
                                if (NumberUnity <= 1) {
                                    removebasket.setVisibility(View.GONE);
                                } else {
                                    removebasket.setVisibility(View.VISIBLE);
                                }
                                numberunity.setText(String.valueOf(NumberUnity));
                            } catch (Exception e) {
                            }
                        } else {
                            numberunity.setText(String.valueOf(1));
                        }
                    }
                });
                // less one
                removebasket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String x = numberunity.getText().toString();
                        if (!TextUtils.isEmpty(x)) {
                            try {
                                int NumberUnity = Integer.parseInt(x);
                                NumberUnity--;
                                if (NumberUnity <= 1) {
                                    removebasket.setVisibility(View.GONE);
                                } else {
                                    removebasket.setVisibility(View.VISIBLE);
                                }
                                numberunity.setText(String.valueOf(NumberUnity));
                            } catch (Exception e) {
                            }
                        } else {
                            numberunity.setText(String.valueOf(1));
                        }
                    }
                });
                deletebasket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        numberunity.setText("1");
                        Basket.setVisibility(View.VISIBLE);
                        BasketDialog.setVisibility(View.GONE);
                        quantitybasket.setVisibility(View.GONE);
                        if (!mybasket.isEmpty()) {
                            mybasket.remove(getAdapterPosition());
                        }
                        // Toast.makeText(context, "err "+e , Toast.LENGTH_SHORT).show();
                    }
                });

                final String basketid = FirebaseDatabase.getInstance().getReference().push().getKey();
                String CustomerId = "";
                if (StaticConfig.UID != null) {
                    CustomerId = StaticConfig.UID;
                } else {
                    CustomerId = FirebaseDatabase.getInstance().getReference().push().getKey();
                }
                final String finalCustomerId = CustomerId;
                savebasket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String x = numberunity.getText().toString();
                        int NumberUnity = 1;
                        if (!TextUtils.isEmpty(x)) {
                            try{
                                NumberUnity = Integer.parseInt(x);
                            }catch(Exception e){}
                        }
                        if (NumberUnity >= 1) {
                            Date CurrentDate = new Date();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            String DateCreate = dateFormat.format(CurrentDate);

                            String productid = products.get(getAdapterPosition()).productId;
                            String imagepath = products.get(getAdapterPosition()).imagePath;

                            int price = products.get(getAdapterPosition()).price;
                            int totalprice = NumberUnity * price;
                            mybasket.add(new MyBasket(
                                    productid,
                                    imagepath,
                                    basketid,
                                    DateCreate,
                                    finalCustomerId,
                                    price,
                                    NumberUnity,
                                    totalprice
                            ));

                            // if user loged in add data to firebase
                            // add data to localdb
                            GoToPay.setVisibility(View.VISIBLE);
                            Basket.setVisibility(View.VISIBLE);
                            BasketDialog.setVisibility(View.GONE);
                            quantitybasket.setVisibility(View.VISIBLE);
                            quantitybasket.setText(String.valueOf(NumberUnity));
                        }
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                   /*
                    String productkey = listProducts.get(getAdapterPosition()).getPushkey();
                    try {
                        intent[0] = new Intent(context, DetailsProduct.class);
                        intent[0].putExtra("id", productkey);
                        context.startActivity(intent[0]);
                    }catch (Exception e){
                        Toast.makeText(context, "Error : "+ e,Toast.LENGTH_LONG).show();
                    }
*/

                    }
                });
            }
        }


    }
}





/*
    class viewAdapter extends RecyclerView.Adapter<viewAdapter.MyHolder> {
        private Context context;
        List<Product> products;
        LovelyProgressDialog dialogWaitDeleting;
        public viewAdapter(Context context, List<Product> products) {
            this.products = products ;
            this.context = context;
            dialogWaitDeleting = new LovelyProgressDialog(context);
        }
        @Override
        public viewAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_pets_products, parent, false);
            viewAdapter.MyHolder myHolder = new viewAdapter.MyHolder(view);
            return myHolder;
        }
        public void onBindViewHolder(viewAdapter.MyHolder holder, int position) {
            Product data = products.get(position);

            Glide.with(context).load(data.imagePath).into(holder.imageView);
            holder.timestamp.setText(new getlongtoago().getlongtoago(data.timestamp));
        }
        @Override
        public int getItemCount() {
            return products.size();
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
                timestamp = (TextView) itemView.findViewById(R.id.timestamp);
                Price = (TextView) itemView.findViewById(R.id.price);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //String postkey = listPets.get(getAdapterPosition()).getIdPost();
                         = listPets.get(getAdapterPosition());
                        intent[0] = new Intent(context, View_Details_Pet.class);
                        intent[0].putExtra("id",1);
                        context.startActivity(intent[0]);
                  }});}}}

 */