package petshops.developerpet.com.petshops.Activities;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import petshops.developerpet.com.petshops.DataBase.SharedReferenceHelper;
import petshops.developerpet.com.petshops.Model.Pet;
import petshops.developerpet.com.petshops.data.StaticConfig;
import petshops.developerpet.com.petshops.R;


public class Add_page extends AppCompatActivity implements  View.OnClickListener{

    double longitude;
    double latitude;
    String straddress;

    ImageView Image1, Image2, Image3, getAddressbyMap;

    EditText Name , ContactNumber, CountactPerson,
            AvailablePets, AvailableProducts, AddressShop;

    Button ShopOpenFrom, ShopOpenTo, shopWeekendOpenFrom, shopWeekendOpenTo;
    FloatingActionButton AddShopData;

/*
    set this later
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
*/


    DatabaseReference mDatabase;
    FirebaseDatabase mInstance;

    Uri ImagePath1 , ImagePath2 , ImagePath3 ;
    String Image_Path1 = "empty" , Image_Path2 = "empty", Image_Path3 = "empty" ;
    String TypeModiffer ;
    ProgressBar progressBar;

    private static final int PICK_IMAGE_REQUEST1 = 234;
    private static final int PICK_IMAGE_REQUEST2 = 235;
    private static final int PICK_IMAGE_REQUEST3 = 236;

    //firebase objects
    SimpleDateFormat inputFormat,outputFormat;
    public static Pet PetsData = new Pet();
    String idPost;

    private StorageReference storageReference;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        TypeModiffer = i.getStringExtra("type");
        idPost = i.getStringExtra("idPost");

        getSupportActionBar().setTitle("Add "+TypeModiffer);

        mInstance = FirebaseDatabase.getInstance();
        mDatabase = mInstance.getReference("data");

        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        Image1 = (ImageView) findViewById(R.id.shopeimage1);
        Image2 = (ImageView) findViewById(R.id.shopeimage2);
        Image3 = (ImageView) findViewById(R.id.shopeimage3);

        Image1.setOnClickListener(this);
        Image2.setOnClickListener(this);
        Image3.setOnClickListener(this);

        getAddressbyMap = (ImageView) findViewById(R.id.getAddressbyMap);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        Name = (EditText) findViewById(R.id.name);
        ((EditText) findViewById(R.id.addphotos)).setEnabled(false);

        //Name.setHint(TypeModiffer+R.string.name);
        Name.setHint(TypeModiffer+" name ");

        ShopOpenFrom = (Button) findViewById(R.id.shopOpenfrom);
        ShopOpenTo = (Button) findViewById(R.id.shopOpento);

        shopWeekendOpenFrom = (Button) findViewById(R.id.shopWeekendOpenfrom);
        shopWeekendOpenTo = (Button) findViewById(R.id.shopWeekendOpento);


        ShopOpenFrom.setOnClickListener(this);
        ShopOpenTo.setOnClickListener(this);

        shopWeekendOpenFrom.setOnClickListener(this);
        shopWeekendOpenTo.setOnClickListener(this);
        getAddressbyMap.setOnClickListener(this);


        AddressShop = (EditText) findViewById(R.id.addressShop);

        ContactNumber = (EditText) findViewById(R.id.contactNumber);
        CountactPerson = (EditText) findViewById(R.id.contactPerson);
        AvailablePets = (EditText) findViewById(R.id.availablePets);
        AvailableProducts = (EditText) findViewById(R.id.availableProducts);

        ContactNumber.setText(SharedReferenceHelper.getInstance(Add_page.this).getPhoneNumber());
        //CountactPerson.setEnabled(false);

        AddShopData = (FloatingActionButton) findViewById(R.id.addShopData);

        AddShopData.setOnClickListener(this);

        // edit shop

        switch(TypeModiffer){
            case "shops" :
                AvailableProducts.setVisibility(View.VISIBLE);
                AvailablePets.setHint(R.string.available_pets);
                AvailableProducts.setHint(R.string.available_products);
                break;
            case "clinics" :
            case "spas" :
            case "others" :
            case "grooming" :
                AvailablePets.setHint(R.string.available_serice);
                AvailableProducts.setVisibility(View.GONE);
                break;
        }


        if(idPost != null){
            Name.setText(PetsData.getName());
            ContactNumber.setText(PetsData.getContactnumber());
            CountactPerson.setText(PetsData.getContactperson());
            AvailablePets.setText(PetsData.getAvailablepets());
            AvailableProducts.setText(PetsData.getAvailableproducts());
            AddressShop.setText(PetsData.getAddress());

            ShopOpenFrom.setText(PetsData.openfrom);
            ShopOpenTo.setText(PetsData.getOpento());
            shopWeekendOpenFrom.setText(PetsData.getWopenfrom());
            shopWeekendOpenTo.setText(PetsData.getWopento());

            latitude = PetsData.getLatitude();
            longitude = PetsData.getLongitude();

            Image_Path1  = PetsData.getPath1();
            Image_Path2 = PetsData.getPath2();
            Image_Path3 = PetsData.getPath3();

            Glide.with(Add_page.this).load(PetsData.getPath1()).into(Image1);
            Glide.with(Add_page.this).load(PetsData.getPath2()).into(Image2);
            Glide.with(Add_page.this).load(PetsData.getPath3()).into(Image3);
        }


        Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = Name.getText().toString().trim();
                if(text.length() <= 1 ){
                    Name.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                }else{
                    Name.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}});

        ContactNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = ContactNumber.getText().toString().trim();
                if(text.length() <= 1 ){
                    ContactNumber.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                }else{
                    ContactNumber.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}});

        CountactPerson.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = CountactPerson.getText().toString().trim();
                if(text.length() <= 1 ){
                    CountactPerson.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                }else{
                    CountactPerson.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}});
        AddressShop.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = AddressShop.getText().toString().trim();
                if(text.length() <= 1 ){
                    AddressShop.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                }else{
                    AddressShop.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}});

        AvailablePets.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = AvailablePets.getText().toString().trim();
                if(text.length() <= 1 ){
                    AvailablePets.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                }else{
                    AvailablePets.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}});

        AvailableProducts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = AvailableProducts.getText().toString().trim();
                if(text.length() <= 1 ){
                    AvailableProducts.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                }else{
                    AvailableProducts.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}});

         inputFormat = new SimpleDateFormat("HH:mm");
         outputFormat = new SimpleDateFormat("KK:mm a");



    }

    // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if (v == ShopOpenFrom) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);
            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String input = hourOfDay+":"+minute;
                            try {
                                ShopOpenFrom.setText("from "+outputFormat.format(inputFormat.parse(input)));
                                ShopOpenFrom.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            //ShopOpenFrom.setText("To "+new SimpleDateFormat ("hh:mm a").format(hourOfDay+":"+minute));
                            String openfrom = ShopOpenFrom.getText().toString().trim();
                            String opento = ShopOpenTo.getText().toString().trim();
                            if(TextUtils.isEmpty(openfrom) || TextUtils.isEmpty(opento)){
                                ((LinearLayout) findViewById(R.id.weekdaysform)).setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                                return;
                            }else{
                                ((LinearLayout) findViewById(R.id.weekdaysform)).setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                            }

                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
        if (v == ShopOpenTo) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);
            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String input = hourOfDay+":"+minute;
                            try {
                                ShopOpenTo.setText("To "+outputFormat.format(inputFormat.parse(input)));
                                ShopOpenTo.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            //ShopOpenTo.setText("To "+new SimpleDateFormat ("hh:mm a").format(hourOfDay+":"+minute));
                            String openfrom = ShopOpenFrom.getText().toString().trim();
                            String opento = ShopOpenTo.getText().toString().trim();
                            if(TextUtils.isEmpty(openfrom) || TextUtils.isEmpty(opento)){
                                ((LinearLayout) findViewById(R.id.weekdaysform)).setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                                return;
                            }else{
                                ((LinearLayout) findViewById(R.id.weekdaysform)).setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                            }


                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();}
    if (v == shopWeekendOpenFrom) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);
            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String input = hourOfDay+":"+minute;
                            try {
                                shopWeekendOpenFrom.setText("from "+outputFormat.format(inputFormat.parse(input)));
                                shopWeekendOpenFrom.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                            String wopenfrom = shopWeekendOpenFrom.getText().toString().trim();
                            String wopento = shopWeekendOpenTo.getText().toString().trim();
                            if(TextUtils.isEmpty(wopenfrom) || TextUtils.isEmpty(wopento)){
                                ((LinearLayout) findViewById(R.id.weekendayform)).setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                                return;
                            }else{
                                ((LinearLayout) findViewById(R.id.weekendayform)).setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                            }
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();}
    if (v == shopWeekendOpenTo) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);
            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String input = hourOfDay+":"+minute;
                            try {
                                shopWeekendOpenTo.setText("to "+outputFormat.format(inputFormat.parse(input)));
                                shopWeekendOpenTo.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            //shopWeekendOpenTo.setText("To "+hourOfDay + ":" + minute);
                            //shopWeekendOpenTo.setText("To "+new SimpleDateFormat ("hh:mm a").format(hourOfDay+":"+minute));
                            String wopenfrom = shopWeekendOpenFrom.getText().toString().trim();
                            String wopento = shopWeekendOpenTo.getText().toString().trim();
                            if(TextUtils.isEmpty(wopenfrom) || TextUtils.isEmpty(wopento)){
                                ((LinearLayout) findViewById(R.id.weekendayform)).setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                                return;
                            }else{
                                ((LinearLayout) findViewById(R.id.weekendayform)).setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                            }

                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    if (v == getAddressbyMap) {
      //   startActivity(new Intent(this, getLocationAndAddress.class));
        startActivityForResult(new Intent(this, getLocationAndAddress.class).putExtra("typedata",TypeModiffer), 2300);
    }

    switch(v.getId()){
        case  R.id.shopeimage1:
            showFileChooser1();
            break;
        case  R.id.shopeimage2:
            showFileChooser2();
            break;
        case  R.id.shopeimage3:
            showFileChooser3();
            break;
        case  R.id.addShopData:
            try {

                String id = StaticConfig.UID;
                String type = TypeModiffer;

                final String name = Name.getText().toString().trim();
                String openfrom = ShopOpenFrom.getText().toString().trim();
                String opento = ShopOpenTo.getText().toString().trim();
                String wopenfrom = shopWeekendOpenFrom.getText().toString().trim();
                String wopento = shopWeekendOpenTo.getText().toString().trim();
                String address = AddressShop.getText().toString().trim();
                String contactnumber = ContactNumber.getText().toString().trim();
                String contactperson = CountactPerson.getText().toString().trim();
                String available = AvailablePets.getText().toString().trim();
                String availableproducts = AvailableProducts.getText().toString().trim();
                double rate = 0;

                if(TextUtils.isEmpty(id)){
                    printToast("your id is null [error when siged up ! test  error ]");
                    return;
                }

                if(!Image_Path1.equals("empty") || !Image_Path2.equals("empty") || !Image_Path3.equals("empty")){
                    ((LinearLayout) findViewById(R.id.imagelayoutbar)).setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                }else{
                    ((LinearLayout) findViewById(R.id.imagelayoutbar)).setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                    ((EditText) findViewById(R.id.addphotos)).setError("Add Photos ");
                    return;}
                if(TextUtils.isEmpty(name)){
                    Name.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                    Name.setError("you sould put name of "+TypeModiffer);
                    printToast("you should put name of "+TypeModiffer);
                    return;
                }else{
                    Name.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                }

                if(TextUtils.isEmpty(openfrom) && TextUtils.isEmpty(opento)){
                    ((LinearLayout) findViewById(R.id.weekdaysform)).setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                    return;
                }else{
                    ((LinearLayout) findViewById(R.id.weekdaysform)).setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                }
                if(TextUtils.isEmpty(wopenfrom) && TextUtils.isEmpty(wopento)){
                    ((LinearLayout) findViewById(R.id.weekendayform)).setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                    return;
                }else{
                    ((LinearLayout) findViewById(R.id.weekendayform)).setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                }

                if(longitude == 0 && latitude == 0){
                    printToast("Please get location data (latidude,longitude)\n by click on location icon");
                    getAddressbyMap.setImageResource(R.mipmap.erro_location_isempty);
                    return;
                }else{
                    getAddressbyMap.setImageResource(R.drawable.gpslocationmarker);
                }

                if(TextUtils.isEmpty(address)){
                    AddressShop.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                    return;
                }else{
                    AddressShop.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                }
                if(TextUtils.isEmpty(contactnumber)){
                    ContactNumber.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                    return;
                }else{
                    ContactNumber.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                }
                if(TextUtils.isEmpty(contactperson)){
                    CountactPerson.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                    return;
                }else{
                    CountactPerson.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                }

                if(TextUtils.isEmpty(available)){
                    AvailablePets.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                    return;
                }else{
                    CountactPerson.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                }

                switch(TypeModiffer){
                    case "shops" :
                        if(TextUtils.isEmpty(availableproducts)){
                            AvailableProducts.setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                            return;
                        }else{
                            CountactPerson.setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                        }
                        break;
                }



/*
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
                        public void onCancelled(DatabaseError databaseError) {}
                    });
*/
                // see if date already in db [name & address & location & id & ...]

                String pushKey = "";
                if(idPost != null){
                    pushKey = PetsData.getIdPost();
                }else{
                    pushKey = mDatabase.push().getKey();
                }

                Pet pet = new Pet(
                        pushKey,
                        id,
                        type,
                        name,
                        openfrom,
                        opento,
                        wopenfrom,
                        wopento,
                        contactnumber,
                        contactperson,
                        available,
                        availableproducts,
                        0,
                        latitude,
                        longitude,
                        address,
                        Image_Path1,
                        Image_Path2,
                        Image_Path3 );

                progressBar.setVisibility(View.VISIBLE);

                mDatabase.child(pushKey).setValue(pet)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBar.setVisibility(View.GONE);
                                printToast(String.valueOf(getResources().getString(R.string.data_add_successfully)));
                                overridePendingTransition(R.anim.zoom_out, R.anim.zoom_in);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        printToast("Add data Failed : " + e);
                    }
                });

            }catch (Exception e){printToast("Error : "+e);}

            break;
    }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2300 && resultCode == RESULT_OK) {
            latitude = data.getDoubleExtra("latitude",0.0);
            longitude = data.getDoubleExtra("longitude", 0.0);
            straddress = data.getStringExtra("address");
            AddressShop.setText(straddress);
        }

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
        //checking if file is available
        if (filePath != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(R.string.uploading);
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
                            Toast.makeText(getApplicationContext(), R.string.fileuploaded, Toast.LENGTH_LONG).show();
                            if(i == 1){
                                Image_Path1 = taskSnapshot.getDownloadUrl().toString();
                            }else if(i == 2){
                                Image_Path2 = taskSnapshot.getDownloadUrl().toString();
                            }else if(i == 3){
                                Image_Path3 = taskSnapshot.getDownloadUrl().toString();
                            }

                            if(!Image_Path1.equals("empty") || !Image_Path2.equals("empty") || !Image_Path3.equals("empty")){
                                ((LinearLayout) findViewById(R.id.imagelayoutbar)).setBackground(getResources().getDrawable(R.drawable.bt_shape_green));
                                ((TextView) findViewById(R.id.addphotos)).setText(getResources().getString(R.string.add_image));
                            }else{
                                ((LinearLayout) findViewById(R.id.imagelayoutbar)).setBackground(getResources().getDrawable(R.drawable.bt_shape_red));
                            }

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
            //display an error if no file is selected
        }
    }
}
