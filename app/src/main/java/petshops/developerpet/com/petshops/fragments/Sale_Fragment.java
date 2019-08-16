package petshops.developerpet.com.petshops.fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import static android.app.Activity.RESULT_OK;

/**
 * Created by root on 2/11/18.
 */


public class Sale_Fragment extends Fragment implements View.OnClickListener {

    public Sale_Fragment(){}

    View rootView;

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }






    EditText Typepet, Breed , Price, Ownername, Phone, Description, Years, Months ;

    int[] idarrayEditText = {
            R.id.typepet,
            R.id.breed,
            R.id.price,
            R.id.ownername,
            R.id.phone,
            R.id.description,
            R.id.years,
            R.id.months};

    EditText[] EditTEXT = new EditText[idarrayEditText.length];


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.sale_fragment, container, false);
        Image1 = rootView.findViewById(R.id.image1);
        Image2 = rootView.findViewById(R.id.image2);
        Image3 = rootView.findViewById(R.id.image3);
        Image1.setOnClickListener(this);
        Image2.setOnClickListener(this);
        Image3.setOnClickListener(this);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mInstance = FirebaseDatabase.getInstance();
        mDatabase = mInstance.getReference("sale");
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        AddSaleData = (FloatingActionButton) rootView.findViewById(R.id.addSaleData);
        AddSaleData.setOnClickListener(this);
        ((EditText) rootView.findViewById(R.id.addphotos)).setEnabled(false);
        Typepet = rootView.findViewById(R.id.typepet);
        Breed = rootView.findViewById(R.id.breed);
        Price = rootView.findViewById(R.id.price);
        Ownername = rootView.findViewById(R.id.ownername);
        Phone = rootView.findViewById(R.id.phone);
        Description = rootView.findViewById(R.id.description);
        Years = rootView.findViewById(R.id.years);
        Months = rootView.findViewById(R.id.months);
        try {
            for (int i = 0; i < idarrayEditText.length; i++) {
                final int b = i;
                EditTEXT[b] =  rootView.findViewById(idarrayEditText[b]); // Fetch the view id from array
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


        return rootView;
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case  R.id.image1:
                showFileChooser1();
                break;
            case  R.id.image2:
                showFileChooser2();
                break;
            case  R.id.image3:
                showFileChooser3();
                break;
            case  R.id.addSaleData:
                try {

                    String typepet = Typepet.getText().toString().trim();
                    String breed = Breed.getText().toString().trim();
                    String years =  Years.getText().toString().trim();
                    String months = Months.getText().toString().trim();
                    String price = Price.getText().toString().trim();
                    String ownername = Ownername.getText().toString().trim();
                    String phone = Phone.getText().toString().trim();
                    String description = Description.getText().toString().trim();

                    if(!Image_Path1.equals("empty") || !Image_Path2.equals("empty") || !Image_Path3.equals("empty")){
                        ((LinearLayout) rootView.findViewById(R.id.imagelayoutbar)).setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                    }else{
                        ((LinearLayout) rootView.findViewById(R.id.imagelayoutbar)).setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                        ((EditText) rootView.findViewById(R.id.addphotos)).setError("Add Photos ");
                        return;
                    }

                    if(TextUtils.isEmpty(typepet)){
                        Typepet.setError("don't let  this empty");
                        Typepet.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                        return;
                    }else{
                        Typepet.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                    }
                    if(TextUtils.isEmpty(breed)){
                        Breed.setError("don't let  this empty");
                        Breed.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                        return;
                    }else{
                        Breed.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                    }
                    if(TextUtils.isEmpty(years)){
                        Years.setError("don't let  this empty");
                        Years.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                        return;
                    }else{
                        Years.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                    }
                    if(TextUtils.isEmpty(months)){
                        Months.setError("don't let  this empty");
                        Months.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                        return;
                    }else{
                        Months.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                    }
                    if(TextUtils.isEmpty(price)){
                        Price.setError("don't let  this empty");
                        Price.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                        return;
                    }else{
                        Price.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                    }
                    if(TextUtils.isEmpty(ownername)){
                        Ownername.setError("don't let  this empty");
                        Ownername.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                        return;
                    }else{
                        Ownername.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                    }
                    if(TextUtils.isEmpty(phone)){
                        Phone.setError("don't let  this empty");
                        Phone.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                        return;
                    }else{
                        Phone.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                    }
                    if(TextUtils.isEmpty(description)){
                        Description.setError("don't let  this empty");
                        Description.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                        return;
                    }else{
                        Description.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                    }



                    progressBar.setVisibility(View.VISIBLE);
                    String pushKey = mDatabase.push().getKey();



                    /*
                    (
            String idPost,
            String idowner,
            String type,
            String ownername,
            String breed,
            String years,
            String duration,
            Long timestamp,
            String months,
            String price,
            String phone,
            String descriptions,
            String path1,
            String path2,
            String path3)
                     */
                    Add_Sale add_sale_data = new Add_Sale(
                            pushKey,
                            StaticConfig.UID,
                            typepet,
                            ownername,
                            breed,
                            years,
                            "",
                            System.currentTimeMillis(),
                            months,
                            price,
                            phone,
                            description,
                            Image_Path1,
                            Image_Path2,
                            Image_Path3,
                            new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a").format(new Date()));


                    mDatabase.child(pushKey).setValue(add_sale_data)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    printToast("Data Added Successfully");
                                    // reflesh this fram
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.detach(Sale_Fragment.this).attach(Sale_Fragment.this).commit();
                                    try {
                                        for (int i = 0; i < idarrayEditText.length; i++) {
                                            final int b = i;
                                            EditTEXT[b] =  rootView.findViewById(idarrayEditText[b]); // Fetch the view id from array
                                            EditTEXT[b].setText("");
                                        }
                                    }catch (Exception e){}

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            ImagePath1 = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), ImagePath1);
                Image1.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadFile(ImagePath1,1);
        }
        if (requestCode == PICK_IMAGE_REQUEST2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            ImagePath2 = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), ImagePath2);
                Image2.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadFile(ImagePath2,2);
        }
        if (requestCode == PICK_IMAGE_REQUEST3 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            ImagePath3 = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), ImagePath3);
                Image3.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadFile(ImagePath3,3);
        }
    }


    private void printToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
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
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void uploadFile(Uri filePath , final int i) {
        //checking if file is available
        if (filePath != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            //getting the storage reference
            StorageReference sRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(filePath));
            //adding the file to reference
            StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                            progressDialog.dismiss();
                            //displaying success toast
                            Toast.makeText(getContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            if(i == 1){
                                Image_Path1 = taskSnapshot.getDownloadUrl().toString();
                            }else if(i == 2){
                                Image_Path2 = taskSnapshot.getDownloadUrl().toString();
                            }else if(i == 3){
                                Image_Path3 = taskSnapshot.getDownloadUrl().toString();
                            }
                            if(!Image_Path1.equals("empty") || !Image_Path2.equals("empty") || !Image_Path3.equals("empty")){
                                ((LinearLayout) rootView.findViewById(R.id.imagelayoutbar)).setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                            }else{
                                ((LinearLayout) rootView.findViewById(R.id.imagelayoutbar)).setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                                ((TextView) rootView.findViewById(R.id.addphotos)).setText(getResources().getString(R.string.add_image));
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
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
            //display an error if no file is selected
        }
    }
}