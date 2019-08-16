package petshops.developerpet.com.petshops.Activities;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import petshops.developerpet.com.petshops.Model.Add_Sale;
import petshops.developerpet.com.petshops.data.StaticConfig;
import petshops.developerpet.com.petshops.R;

public class Add_Adopation extends AppCompatActivity implements View.OnClickListener {
    ImageView Image1, Image2, Image3;
    FloatingActionButton AddSaleData;

    DatabaseReference mDatabase;
    FirebaseDatabase mInstance;

    Uri ImagePath1 , ImagePath2 , ImagePath3 ;
    String Image_Path1 = "empty" , Image_Path2 = "empty", Image_Path3 = "empty" ;
    ProgressBar progressBar;

    private static final int PICK_IMAGE_REQUEST1 = 234;
    private static final int PICK_IMAGE_REQUEST2 = 235;
    private static final int PICK_IMAGE_REQUEST3 = 236;
    //firebase objects
    private StorageReference storageReference;
    private DatabaseReference mDatabaseReference;

    EditText Typepet, Breed, Duration, Price, Ownername, Phone, Description, Years, Months ;


    int[] idarrayEditText = {
            R.id.typepet,
            R.id.breed,
            R.id.duration,
            R.id.price,
            R.id.ownername,
            R.id.phone,
            R.id.description,
            R.id.years,
            R.id.months};

    EditText[] EditTEXT = new EditText[idarrayEditText.length];

    String idPost;
    Intent i, intent_Get_Image;
    public static Add_Sale Data_Adopation_sale = new Add_Sale();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_adopation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add new Adoption");

        i = getIntent();
        idPost = i.getStringExtra("idPost");

        Image1 = (ImageView) findViewById(R.id.image1);
        Image2 = (ImageView) findViewById(R.id.image2);
        Image3 = (ImageView) findViewById(R.id.image3);

        Image2.setVisibility(View.GONE);
        Image3.setVisibility(View.GONE);

        Image1.setOnClickListener(this);
        Image2.setOnClickListener(this);
        Image3.setOnClickListener(this);

        intent_Get_Image = new Intent();
        intent_Get_Image.setType("image/*");
        intent_Get_Image.setAction(Intent.ACTION_GET_CONTENT);

        ((EditText) findViewById(R.id.addphotos)).setEnabled(false);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mInstance = FirebaseDatabase.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        AddSaleData = (FloatingActionButton) findViewById(R.id.addSaleData);

        AddSaleData.setOnClickListener(this);

        Typepet = (EditText) findViewById(R.id.typepet);
        Breed =(EditText) findViewById(R.id.breed);
        Duration =(EditText) findViewById(R.id.duration);
        Price = (EditText)findViewById(R.id.price);
        Ownername =(EditText) findViewById(R.id.ownername);
        Phone = (EditText)findViewById(R.id.phone);
        Description =(EditText) findViewById(R.id.description);
        Years = (EditText)findViewById(R.id.years);
        Months = (EditText)findViewById(R.id.months);

        try {
            for (int i = 0; i < idarrayEditText.length; i++) {
                final int b = i;
                EditTEXT[b] = (EditText) findViewById(idarrayEditText[b]); // Fetch the view id from array
                EditTEXT[b].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String text = EditTEXT[b].getText().toString().trim();
                        if(text.length() == 0 ){
                            EditTEXT[b].setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                        }else{
                            EditTEXT[b].setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable s) {}});
            }
        }catch (Exception e){}

        if(i.getStringExtra("post_type") != null){
            if(i.getStringExtra("post_type").equals("sale")){
               // printToast(">> post type sale");
                getSupportActionBar().setTitle("Edit Sale");
                ((RelativeLayout) findViewById(R.id.agelayout)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.duration)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.price)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.price)).setHint("Price");
                mDatabase = mInstance.getReference("sale");
            }else{
                getSupportActionBar().setTitle("Edit Adoption");
                ((RelativeLayout) findViewById(R.id.agelayout)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.duration)).setVisibility(View.VISIBLE);
                mDatabase = mInstance.getReference("adopation");
            }
            Typepet.setText(Data_Adopation_sale.getType());
            Breed.setText(Data_Adopation_sale.getBreed());
            Duration.setText(Data_Adopation_sale.getDuration());
            Price.setText(Data_Adopation_sale.getPrice());
            Ownername.setText(Data_Adopation_sale.getOwnername());
            Phone.setText(Data_Adopation_sale.getPhone());
            Description.setText(Data_Adopation_sale.getDescriptions());
            Years.setText(Data_Adopation_sale.getYears());
            Months.setText(Data_Adopation_sale.getMonths());

            Image_Path1  = Data_Adopation_sale.getPath1();
            Image_Path2 = Data_Adopation_sale.getPath2();
            Image_Path3 = Data_Adopation_sale.getPath3();


            if(!Image_Path1.equals("empty")){
                Glide.with(Add_Adopation.this).load(Data_Adopation_sale.getPath1()).into(Image1);
            }else{
                Image1.setVisibility(View.GONE);
            }
            if(!Image_Path2.equals("empty")){
                Glide.with(Add_Adopation.this).load(Data_Adopation_sale.getPath2()).into(Image2);
            }else{
                Image2.setVisibility(View.GONE);
            }
            if(!Image_Path3.equals("empty")){
                Glide.with(Add_Adopation.this).load(Data_Adopation_sale.getPath3()).into(Image3);
            }else{
                Image3.setVisibility(View.GONE);
            }

        }else{
            mDatabase = mInstance.getReference("adopation");
        }




    }
    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case  R.id.image1:
                //showFileChooser1();
                startActivityForResult(Intent.createChooser(intent_Get_Image, "Select Picture"), PICK_IMAGE_REQUEST1);
                break;
            case  R.id.image2:
                //showFileChooser2();
                startActivityForResult(Intent.createChooser(intent_Get_Image, "Select Picture"), PICK_IMAGE_REQUEST2);
                break;
            case  R.id.image3:
                //showFileChooser3();
                startActivityForResult(Intent.createChooser(intent_Get_Image, "Select Picture"), PICK_IMAGE_REQUEST3);
                break;
            case  R.id.addSaleData:
                try {

                    //String id = StaticConfig.UID;
/*
                    if(TextUtils.isEmpty(id)){
                        printToast("your id is null [error when siged up ! test  error ]");
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    if(TextUtils.isEmpty(name)){
                        printToast("you should put name of "+TypeModiffer);
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
  */
                    //    ((TextView) findViewById(R.id.)).getText().toString().trim();

                    String typepet = Typepet.getText().toString().trim();
                    String breed = Breed.getText().toString().trim();
                    String duration = Duration.getText().toString().trim();
                    String price = Price.getText().toString().trim();
                    String ownername = Ownername.getText().toString().trim();
                    String phone = Phone.getText().toString().trim();
                    String description = Description.getText().toString().trim();
                    String years = Years.getText().toString().trim();
                    String months = Months.getText().toString().trim();

                    if(Image_Path1.equals("empty")){// || Image_Path2.equals("empty") || Image_Path3.equals("empty")){
                        ((LinearLayout) findViewById(R.id.imagelayoutbar)).setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                        ((EditText) findViewById(R.id.addphotos)).setError("Add Photos ");
                        return;
                    }else{
                        ((LinearLayout) findViewById(R.id.imagelayoutbar)).setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                    }
                    if(TextUtils.isEmpty(typepet)){
                        printToast(String.valueOf(R.string.putdata));
                        ((TextView) findViewById(R.id.typepet)).setError("don't let  this empty");
                        Typepet.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                        return;
                    }else{
                        Typepet.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                    }
                    if(TextUtils.isEmpty(breed)){
                        printToast("please put ");
                        ((TextView) findViewById(R.id.breed)).setError("don't let  this empty");
                        Breed.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                        return;
                    }else{
                        Breed.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));

                    }

                    if(TextUtils.isEmpty(price)){
                        printToast("please put ");
                        Price.setError("don't let  this empty");
                        Price.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                        return;
                    }else{
                        Price.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));

                    }
                    if(TextUtils.isEmpty(phone)){
                        printToast("please put ");
                        ((TextView) findViewById(R.id.phone)).setError("don't let  this empty");
                        Phone.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                        return;
                    }else{
                        Phone.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));

                    }
                    if(TextUtils.isEmpty(description)){
                        printToast("please put ");
                        ((TextView) findViewById(R.id.description)).setError("don't let  this empty");
                        Description.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                        return;
                    }else{
                        Description.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                    }
                    if(TextUtils.isEmpty(ownername)){
                        printToast("please put ");
                        ((TextView) findViewById(R.id.description)).setError("don't let  this empty");
                        Ownername.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                        return;
                    }else{
                        Ownername.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));

                    }

                    String pushKey = "";
                    if(i.getStringExtra("post_type") != null){
                        pushKey = Data_Adopation_sale.getIdPost();
                    }else{
                        pushKey =  mDatabase.push().getKey();
                    }

                    /*
                    (
            String idPost,
            String idowner,
            String type,
            String ownername,
            String breed,
            String years,
            String duration,
            Long timestamp =   System.currentTimeMillis(),,
            String months,
            String price,
            String phone,
            String descriptions,
            String path1,
            String path2,
            String path3,
            currentdate)
                     */
                    Add_Sale add_sale_data = new Add_Sale(
                            pushKey,
                            StaticConfig.UID,
                            typepet,
                            ownername,
                            breed,
                            years,
                            duration,
                            System.currentTimeMillis(),
                            months,
                            price,
                            phone,
                            description,
                            Image_Path1,
                            Image_Path2,
                            Image_Path3,
                            new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a").format(new Date())
                    );


                    progressBar.setVisibility(View.VISIBLE);
                    mDatabase.child(pushKey).setValue(add_sale_data)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    printToast(String.valueOf(getResources().getString(R.string.data_add_successfully)));
                                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            printToast("Data Added Failed : " + e);
                        }
                    });
                }catch (Exception e){printToast("Error : "+e);}
                break;
        }
    }


    /*

  System.currentTimeMillis(); >> set current time;
  calcul

     String time = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(listFriend.getListFriend().get(position).message.timestamp));
            String today = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(System.currentTimeMillis()));
            if (today.equals(time)) {
                ((ItemFriendViewHolder) holder).txtTime.setText(new SimpleDateFormat("HH:mm").format(new Date(listFriend.getListFriend().get(position).message.timestamp)));
            } else {
                ((ItemFriendViewHolder) holder).txtTime.setText(new SimpleDateFormat("MMM d").format(new Date(listFriend.getListFriend().get(position).message.timestamp)));
            }
                        mDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                        String name_shope = dataSnapshot1.getValue(String.class);
                                        if(name.equals(name_shope)){
                                            printToast("Name "+name+" is already exist \nSet new name");
                                            return;
                                        }
                                    }
                                }
                             }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
    */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            ImagePath1 = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), ImagePath1);
                Image1.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadFile(ImagePath1,1);
        }
        if (requestCode == PICK_IMAGE_REQUEST2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            ImagePath2 = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), ImagePath2);
                Image2.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadFile(ImagePath2,2);
        }
        if (requestCode == PICK_IMAGE_REQUEST3 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            ImagePath3 = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), ImagePath3);
                Image3.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadFile(ImagePath3,3);
        }
    }


    private void printToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void showFileChooser1() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST1);
    }

    private void showFileChooser2() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST2);
    }
    private void showFileChooser3() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST3);
    }
    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void uploadFile(Uri filePath , final int i) {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            StorageReference sRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(filePath));
            StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            if(i == 1){
                                Image_Path1 = taskSnapshot.getDownloadUrl().toString();
                            }else if(i == 2){
                                Image_Path2 = taskSnapshot.getDownloadUrl().toString();
                            }else if(i == 3){
                                Image_Path3 = taskSnapshot.getDownloadUrl().toString();
                            }

                            if(Image_Path1.equals("empty")){
                                ((LinearLayout) findViewById(R.id.imagelayoutbar)).setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                                Image2.setVisibility(View.VISIBLE);
                                Image3.setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.addphotos)).setText(getResources().getString(R.string.add_image));
                            }else{
                                ((LinearLayout) findViewById(R.id.imagelayoutbar)).setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                            }

                            // Image_Path2.equals("empty")
                            // !Image_Path3.equals("empty")){


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {

        }
    }
/*
 getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add new Adoption");

*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
             onBackPressed();
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

